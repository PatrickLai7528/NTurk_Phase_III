package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.annotation.RAnnotations;
import foursomeSE.entity.communicate.report.Report;
import foursomeSE.entity.communicate.report.Reports;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.user.Worker;
import foursomeSE.entity.verification.VerificationType;
import foursomeSE.error.MyNotValidException;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.gold.GoldJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.annotation.AnnotationUtils.anttById;
import static foursomeSE.service.task.TaskUtils.mtByImg;
import static foursomeSE.service.user.UserUtils.userByUsername;

public abstract class AbstractUpperAnnotationServiceImpl<T extends Annotation>
        implements UpperAnnotationService<T>, MyConstants {
    private ContractJPA contractJPA;
    private AbstractAnnotationJPA<T> abstractAnnotationJPA;
    private WorkerJPA workerJPA;
    private MicrotaskJPA microtaskJPA;
    private AnnotationJPA annotationJPA;
    private GoldJPA goldJPA;


    public AbstractUpperAnnotationServiceImpl(ContractJPA contractJPA,
                                              AbstractAnnotationJPA<T> abstractAnnotationJPA,
                                              WorkerJPA workerJPA, MicrotaskJPA microtaskJPA,
                                              AnnotationJPA annotationJPA,
                                              GoldJPA goldJPA) {
        this.contractJPA = contractJPA;
        this.abstractAnnotationJPA = abstractAnnotationJPA;
        this.workerJPA = workerJPA;
        this.microtaskJPA = microtaskJPA;
        this.annotationJPA = annotationJPA;
        this.goldJPA = goldJPA;
    }

    @Override
    public T getByImgName(String imgName, int whatFor, String username) {
        Microtask mt = mtByImg(microtaskJPA, imgName);
        T t; // t是最新的那个antt / gold的那个antt


        if (isTrap(imgName, username)) {
            VerificationType vType;
            if (whatFor == 1) {
                vType = VerificationType.QUALITY;
            } else if (whatFor == 2) {
                vType = VerificationType.COVERAGE;
            } else {
                throw new MyNotValidException();
            }
            BigInteger bigInteger = goldJPA.goldAidByImgName(imgName, vType.ordinal());
            // 这个就一定可以拿到的，不然不会发给别人pass了的。
            // 但是如果是requester拿呢？那还是得返回最新的。worker永远不会去拿pass了的。
            t = anttById(abstractAnnotationJPA, bigInteger.longValue());
        } else {
            BigInteger b = annotationJPA.findLatestByImgName(imgName);
            // 除非是第一次，不然肯定是有的。
            if (b == null) { // 如果是第一次，直接特殊处理。
                return null;
            }

            t = anttById(abstractAnnotationJPA, b.longValue());
        }


        List<T> previous = abstractAnnotationJPA.findByMicrotaskIdAndCreateTimeBeforeAndAnnotationStatus(
                mt.getMicrotaskId(), t.getCreateTime(), AnnotationStatus.PASSED
        );
        ArrayList<Object> cores = previous.stream().map(Annotation::core).collect(Collectors.toCollection(ArrayList::new));


        if (t.getAnnotationStatus() == AnnotationStatus.FAILED) {
            t.setCore(null);
        } else if (t.getAnnotationStatus() == AnnotationStatus.REVIEWABLE) {
            if (whatFor == 3) {
                t.setCore(null);
            }
        } else if (t.getAnnotationStatus() == AnnotationStatus.PASSED) {
            if (!isTrap(imgName, username) && !(t instanceof GeneralAnnotation)) {
                cores.add(t.core());
                t.setCore(null);
            }
        }

        t.setCores(cores);
        return t;
    }

    @Override
    public void saveAnnotations(RAnnotations<T> rAnnotations, String username) {
        if (rAnnotations.getAnnotations().isEmpty()) {
            throw new MyNotValidException();
        }

        // 所以这里存那么多其实是有问题的 ... 5个里面存一个就可以了
        T t0 = rAnnotations.getAnnotations().get(0);
        String imgName = t0.getImgName();
        Microtask mt0 = mtByImg(microtaskJPA, imgName);


        if (mt0.getLastRequestTime().isAfter(LocalDateTime.now().minusMinutes(TASK_DURATION))) {
            long taskId = mt0.getTaskId();
            Worker worker = userByUsername(workerJPA, username);

            rAnnotations.getAnnotations().forEach(a -> {
                Microtask mt = mtByImg(microtaskJPA, a.getImgName());

                a.setUsername(username);
                a.setMicrotaskId(mt.getMicrotaskId());
                a.setAnnotationStatus(AnnotationStatus.REVIEWABLE);
                a.setCreateTime(LocalDateTime.now());
                a.setIteration(mt.getIteration());
                abstractAnnotationJPA.save(a);

                mt.setParallel(0);
                mt.setMicrotaskStatus(MicrotaskStatus.YET_TO_VERIFY_QUALITY);
                microtaskJPA.save(mt);

                CriticalSection.drawRecords
                        .removeIf(ii -> ii.username.equals(username)
                                && ii.microtaskId == mt.getMicrotaskId());
            });
        } else {
            throw new MyNotValidException();
        }
    }

    private boolean isTrap(String imgName, String username) {
        Microtask mt = mtByImg(microtaskJPA, imgName);
        return mt.getIsSample() == 1 // 如果是worker在看一个完成了的microtask，那么一定是gold。其实不需要看是不是isSample了。
                && mt.getMicrotaskStatus() == MicrotaskStatus.PASSED
                && workerJPA.findByEmailAddress(username).isPresent();
    }

    // 写成这样实在是脑残了，
    // 明明就是找一下所有的passed，干吗需要区分最新的还是不是最新的。。
    @Override
    public Reports getJson(long taskId, String username) {
        List<String> imgs = microtaskJPA.retrieveImgNames(taskId);

        ArrayList<Report> reports = new ArrayList<>();

        for (String img : imgs) {
            Microtask microtask = mtByImg(microtaskJPA, img);
            if (microtask.getMicrotaskStatus() == MicrotaskStatus.PASSED) {
                Report r = new Report();
                r.setImgName(img);

                BigInteger b = annotationJPA.findLatestByImgName(img);
                // 这个方法找到的原不一定是最新的，但是在这里最新的那个一定是通过的。
                T t = anttById(abstractAnnotationJPA, b.longValue());


                // 先这么写。。。懒得再分一个子方法了
                if (t instanceof GeneralAnnotation) {
                    r.setCore(((GeneralAnnotation) t).getAnswer());
                } else {
                    List<T> previous = abstractAnnotationJPA.findByMicrotaskIdAndCreateTimeBeforeAndAnnotationStatus(
                            microtask.getMicrotaskId(), t.getCreateTime(), AnnotationStatus.PASSED
                    );
                    previous.add(t);

                    ArrayList<Object> cores = previous.stream().map(Annotation::core).collect(Collectors.toCollection(ArrayList::new));
                    r.setCore(cores);
                }

                reports.add(r);
            }
        }

        Reports result = new Reports();
        result.setReports(reports);
        return result;
    }

//    public static void main(String[] args) {
//        System.out.println("e");
//
//        Report t = new Report();
//        t.setImgName("imgnames");
//        t.setCores(new int[] {1,2,3});
//        t.setCores("如果改成string");
//        Object o = t;
//
//        Gson gson = new Gson();
//        String s = gson.toJson(o);
//
//        System.out.println(s);
//
//    }

}


