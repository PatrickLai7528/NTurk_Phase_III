package foursomeSE.service.verification;

import foursomeSE.entity.Gold;
import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.task.TaskStatus;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.Verification;
import foursomeSE.entity.verification.VerificationType;
import foursomeSE.error.MyFailTestException;
import foursomeSE.error.MyNotValidException;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.gold.GoldJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.verification.VerificationJPA;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.annotation.AnnotationUtils.anttById;
import static foursomeSE.service.task.TaskUtils.taskById;

public abstract class AbstractVerificationServiceImpl implements VerificationService, MyConstants {

    private MicrotaskJPA microtaskJPA;
    private TaskJPA taskJPA;
    private GoldJPA goldJPA;
    private AnnotationJPA annotationJPA;
    private VerificationJPA verificationJPA;

    public AbstractVerificationServiceImpl(MicrotaskJPA microtaskJPA, TaskJPA taskJPA, GoldJPA goldJPA, AnnotationJPA annotationJPA, VerificationJPA verificationJPA) {
        this.microtaskJPA = microtaskJPA;
        this.taskJPA = taskJPA;
        this.goldJPA = goldJPA;
        this.annotationJPA = annotationJPA;
        this.verificationJPA = verificationJPA;
    }

    @Override
    public EnterResponse enterVerification(long taskId, String username) {
        List<Microtask> candidates = microtaskJPA.getForVerification(taskId, username, getStt().ordinal());
        if (candidates.size() > NUM_OF_INSPECTION_PER_REQUEST) {
            candidates = candidates.subList(0, NUM_OF_INSPECTION_PER_REQUEST);
        }

        List<String> golds = new ArrayList<>();

        Task task = taskById(taskJPA, taskId);
        if (task.getIsCollecting() == 0) {
            long haveEnter = microtaskJPA.countInspectionTimes(taskId, username, getStt().ordinal());
            if (haveEnter + 1 == TRAPS.get(0)) { // 感觉应该直接写0或1的
                if (candidates.size() > 3) { // 这个为了简单，如果是最后几个，就不挖坑了
                    String img1 = goldJPA.findByTaskIdAndOrd(taskId, 0);
                    String img2 = goldJPA.findByTaskIdAndOrd(taskId, 1);
                    String img3 = goldJPA.findByTaskIdAndOrd(taskId, 2);

                    golds.add(img1);
                    golds.add(img2);
                    golds.add(img3);

                    candidates = candidates.subList(0, candidates.size() - 3);
                }
            } else if (haveEnter + 1 == TRAPS.get(1)) {
                // 这种重复做码也是有点不爽
                if (candidates.size() > 2) {
                    String img1 = goldJPA.findByTaskIdAndOrd(taskId, 3);
                    String img2 = goldJPA.findByTaskIdAndOrd(taskId, 4);

                    golds.add(img1);
                    golds.add(img2);

                    candidates = candidates.subList(0, candidates.size() - 2);
                }
            } else {
                ArrayList<Integer> find = TRAPS.stream().filter(t -> t == haveEnter + 1).collect(Collectors.toCollection(ArrayList::new));
                if (find.size() == 1) {
                    if (candidates.size() > 1) {
                        int ord = TRAPS.indexOf(find.get(0)) + 3;
                        String img = goldJPA.findByTaskIdAndOrd(taskId, ord);
                        golds.add(img);

                        candidates = candidates.subList(0, candidates.size() - 1);
                    }
                }
            }

        }

        candidates.forEach(mt -> {
            mt.setParallel(mt.getParallel() + 1);

            CriticalSection.Item item = new CriticalSection.Item();
            item.requestTime = LocalDateTime.now();
            item.username = username;
            item.microtaskId = mt.getMicrotaskId();
            getRecords().add(item);
        });


        EnterResponse response = new EnterResponse();
        ArrayList<String> imgs = candidates.stream().map(Microtask::getImgName).collect(Collectors.toCollection(ArrayList::new));
        imgs.addAll(golds);
        response.setImgNames(imgs);
        // TODO 打乱一下顺序

        return response;
    }

    @Override
    public void saveVerifications(RVerifications verifications, String username) {
        if (verifications.getVerifications().size() != NUM_OF_INSPECTION_PER_REQUEST) {
            throw new MyNotValidException();
        }

        if (verifications.getVerifications().stream().anyMatch(v -> {
            Microtask mt = microtaskJPA.findByAnnotationId(v.getAnnotationId());

            return mt.getMicrotaskStatus() != MicrotaskStatus.PASSED // 说明是gold，有gold则下面肯定没有记录
                    && getRecords().stream().anyMatch(i -> i.username.equals(username) && i.microtaskId == mt.getMicrotaskId());
        })) {

            Task tsk = taskJPA.findByAnnotationId(verifications.getVerifications().get(0).getAnnotationId());

            if (tsk.getIsCollecting() == 1) {
                verifications.getVerifications().forEach(v -> {
                    v.setVerificationType(getVType());
                    v.setUsername(username);
                    verificationJPA.save(v);


                    Task task = taskJPA.findByAnnotationId(v.getAnnotationId());
                    Microtask microtask = microtaskJPA.findByAnnotationId(v.getAnnotationId());
                    Annotation annotation = anttById(annotationJPA, v.getAnnotationId());

                    microtask.setParallel(microtask.getParallel() - 1);
                    microtaskJPA.save(microtask);
                    getRecords().removeIf(ii -> ii.username.equals(username) && ii.microtaskId == microtask.getMicrotaskId());

                    List<Verification> haveVerified = verificationJPA.findByAnnotationIdAndVerificationType(annotation.getAnnotationId(), getVType());
                    if (haveVerified.size() == INSPECTION_PER_CONTRACT) {
                        int sum = haveVerified.stream().mapToInt(Verification::getRate).sum();
                        if (sum >= 2) {
                            // 这个再set一次也没什么问题吧
                            annotation.setAnnotationStatus(AnnotationStatus.PASSED);
                            annotationJPA.save(annotation);

                            microtask.setMicrotaskStatus(getSuccessfulNextStt());
                            microtaskJPA.save(microtask);

                            // 选gold
                            // 这个是不是很不符合软工原则啊。。
                            // 改成一个抽象方法让下面选择干不干吧
                            // 但是下面annotationStatus那里好像也只能那样了。。
                            if (getStt() == MicrotaskStatus.YET_TO_VERIFY_COVERAGE) {
                                microtask.setIteration(microtask.getIteration() + 1);
                                microtaskJPA.save(microtask);

                                List<Microtask> samples = microtaskJPA.getSampling(task.getTaskId());
                                List<Long> results = new ArrayList<>();
                                if (samples.stream().allMatch(s -> s.getMicrotaskStatus() == MicrotaskStatus.PASSED)) {
                                    for (Microtask s : samples) {
                                        List<BigInteger> candidates = annotationJPA.getSample(s.getMicrotaskId(), getVType().ordinal());
                                        if (!candidates.isEmpty()) {
                                            results.add(candidates.get(0).longValue());
                                        }
                                    }
                                }
                                if (results.size() >= 10) {
                                    results = results.subList(0, 10);
                                    for (int i = 0; i < 10; i++) {
                                        long l = results.get(i);
                                        Gold gold = new Gold();
                                        gold.annotationId = l;
                                        gold.ord = i;
                                        gold.taskId = task.getTaskId();
                                        goldJPA.save(gold);
                                    }

                                    task.setIsCollecting(0);
                                    taskJPA.save(task);
                                } else { // 应该不怎么会用到这里吧。。
                                    int deficit = 10 - results.size();
                                    List<Microtask> unSampled = microtaskJPA.getUnSampled(task.getTaskId());
                                    if (unSampled.size() > deficit) {
                                        unSampled = unSampled.subList(0, deficit);
                                    }
                                    for (Microtask m : unSampled) {
                                        m.setIsSample(1);
                                        microtaskJPA.save(m);
                                    }

                                }
                            }

                            // 也有可能是还没有先完就结束了。极端一点就是所有结果都是2/3
                            List<Microtask> notPassed = microtaskJPA.findByTaskIdNotPassed(task.getTaskId());
                            if (notPassed.isEmpty()) {
                                task.setTaskStatus(TaskStatus.FINISHED);
                                taskJPA.save(task);
                            }
                        } else {
                            if (getStt() == MicrotaskStatus.YET_TO_VERIFY_QUALITY) {
                                annotation.setAnnotationStatus(AnnotationStatus.FAILED);
                                annotationJPA.save(annotation);
                            }

                            microtask.setMicrotaskStatus(getFailedNextStt());
                            microtaskJPA.save(microtask);
                        }
                    }
                });
            } else {
                ArrayList<Verification> traps = verifications.getVerifications().stream().filter(v -> {
                    Microtask mt = microtaskJPA.findByAnnotationId(v.getAnnotationId());
                    return mt.getMicrotaskStatus() == MicrotaskStatus.PASSED;
                }).collect(Collectors.toCollection(ArrayList::new));

                verifications.getVerifications().removeAll(traps);

                ArrayList<Verification> diffs = traps.stream().filter(v -> {
                    List<Verification> vs = verificationJPA.findByAnnotationIdAndVerificationType(v.getAnnotationId(), getVType());
                    return vs.get(0).getRate() != v.getRate();
                }).collect(Collectors.toCollection(ArrayList::new));

                if (diffs.isEmpty()) {
                    for (Verification v : verifications.getVerifications()) {
                        v.setVerificationType(getVType());
                        v.setUsername(username);
                        verificationJPA.save(v);

                        Task task = taskJPA.findByAnnotationId(v.getAnnotationId());
                        Microtask microtask = microtaskJPA.findByAnnotationId(v.getAnnotationId());
                        Annotation annotation = anttById(annotationJPA, v.getAnnotationId());

                        microtask.setParallel(microtask.getParallel() - 1);
                        microtaskJPA.save(microtask);
                        getRecords().removeIf(ii -> ii.username.equals(username) && ii.microtaskId == microtask.getMicrotaskId());


                        if (v.getRate() == 1) {
                            annotation.setAnnotationStatus(AnnotationStatus.PASSED);
                            annotationJPA.save(annotation);

                            microtask.setMicrotaskStatus(getSuccessfulNextStt());
                            microtaskJPA.save(microtask);

                            // 这个操作也只有
                            List<Microtask> notPassed = microtaskJPA.findByTaskIdNotPassed(task.getTaskId());
                            if (notPassed.isEmpty()) {
                                task.setTaskStatus(TaskStatus.FINISHED);
                                taskJPA.save(task);
                            }
                        } else {
                            if (getStt() == MicrotaskStatus.YET_TO_VERIFY_QUALITY) {
                                annotation.setAnnotationStatus(AnnotationStatus.FAILED);
                                annotationJPA.save(annotation);
                            }

                            microtask.setMicrotaskStatus(getFailedNextStt());
                            microtaskJPA.save(microtask);
                        }
                    }
                } else {
                    ArrayList<Long> failedId = diffs.stream().map(Verification::getAnnotationId)
                            .collect(Collectors.toCollection(ArrayList::new));
                    throw new MyFailTestException(failedId);
                }
            }
        } else {
            throw new MyNotValidException();
        }
    }

    /**
     * private
     * */



    /**
     * protected (trivial)
     * */
    protected abstract MicrotaskStatus getStt();

    protected abstract MicrotaskStatus getSuccessfulNextStt();

    protected abstract MicrotaskStatus getFailedNextStt();

    protected abstract VerificationType getVType();

    protected abstract List<CriticalSection.Item> getRecords();

}
