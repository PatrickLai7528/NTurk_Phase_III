package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.annotation.RAnnotations;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyNotValidException;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.util.MyConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static foursomeSE.service.annotation.AnnotationUtils.anttById;
import static foursomeSE.service.task.TaskUtils.mtById;
import static foursomeSE.service.task.TaskUtils.mtByImg;
import static foursomeSE.service.user.UserUtils.userByUsername;

public abstract class AbstractUpperAnnotationServiceImpl<T extends Annotation>
        implements UpperAnnotationService<T>, MyConstants {
    private ContractJPA contractJPA;
    private AbstractAnnotationJPA<T> abstractAnnotationJPA;
    private WorkerJPA workerJPA;
    private MicrotaskJPA microtaskJPA;
    private AnnotationJPA annotationJPA;


    public AbstractUpperAnnotationServiceImpl(ContractJPA contractJPA,
                                              AbstractAnnotationJPA<T> abstractAnnotationJPA,
                                              WorkerJPA workerJPA,
                                              MicrotaskJPA microtaskJPA,
                                              AnnotationJPA annotationJPA) {
        this.contractJPA = contractJPA;
        this.abstractAnnotationJPA = abstractAnnotationJPA;
        this.workerJPA = workerJPA;
        this.microtaskJPA = microtaskJPA;

    }

//    @Override
//    public T getById(long id) {
//        T t = abstractAnnotationJPA.findById(id)
//                .orElseThrow(() -> new MyObjectNotFoundException("annotation with id " + id + " is not found"));
//        t.setImgName(mtById(microtaskJPA, t.getMicrotaskId()).getImgName());
//        return t;
//    }

    @Override
    public T getByImgName(String imgName) {
        Microtask mt = mtByImg(microtaskJPA, imgName);


        Annotation a = annotationJPA.findLatestByImgName(imgName);
        // 除非是第一次，不然肯定是有的。
        if (a == null) { // 如果是第一次，直接特殊处理吧。
            return null;
        }

        T t = anttById(abstractAnnotationJPA, a.getAnnotationId());

        List<T> previous = abstractAnnotationJPA.findByMicrotaskIdAndCreateTimeBeforeAndAnnotationStatus(
                mt.getMicrotaskId(), a.getCreateTime(), AnnotationStatus.PASSED
        );

        ArrayList<Object> cores = previous.stream().map(Annotation::core).collect(Collectors.toCollection(ArrayList::new));
        t.setCore(cores);

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

            Contract translucent = contractJPA.findByTaskIdByUsername(taskId, username);
            if (translucent == null) {
                Contract c = new Contract();
                c.setTaskId(taskId);
                c.setWorkerId(worker.getId());
                c.setLastEditTime(LocalDateTime.now());
                contractJPA.save(c);
            } else {
                translucent.setLastEditTime(LocalDateTime.now());
                contractJPA.save(translucent);
            }


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
            });
        } else {
            throw new MyNotValidException();
        }
    }
}


