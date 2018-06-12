package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.annotation.RAnnotations;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyNotValidException;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.util.MyConstants;

import java.time.LocalDateTime;
import java.util.Optional;

import static foursomeSE.service.annotation.AnnotationUtils.annotationByContractIdAndImgName;
import static foursomeSE.service.contract.ContractUtils.contractByTaskIdAndUsername;
import static foursomeSE.service.task.TaskUtils.mtById;
import static foursomeSE.service.task.TaskUtils.mtByImg;
import static foursomeSE.service.user.UserUtils.userByUsername;

public abstract class AbstractUpperAnnotationServiceImpl<T extends Annotation>
        implements UpperAnnotationService<T>, MyConstants {
    private ContractJPA contractJPA;
    private AbstractAnnotationJPA<T> annotationJPA;
    private WorkerJPA workerJPA;
    private MicrotaskJPA microtaskJPA;


    public AbstractUpperAnnotationServiceImpl(ContractJPA contractJPA,
                                              AbstractAnnotationJPA<T> annotationJPA,
                                              WorkerJPA workerJPA,
                                              MicrotaskJPA microtaskJPA) {
        this.contractJPA = contractJPA;
        this.annotationJPA = annotationJPA;
        this.workerJPA = workerJPA;
        this.microtaskJPA = microtaskJPA;
    }

//    @Override
//    public T getById(long id) {
//        T t = annotationJPA.findById(id)
//                .orElseThrow(() -> new MyObjectNotFoundException("annotation with id " + id + " is not found"));
//        t.setImgName(mtById(microtaskJPA, t.getMicrotaskId()).getImgName());
//        return t;
//    }

    @Override
    public T getByImgName(String imgName) {
        return null;
    }

    @Override
    public void saveAnnotations(RAnnotations<T> rAnnotations, String username) {
        if (rAnnotations.getAnnotations().isEmpty()) {
            throw new MyNotValidException();
        }

        // 所以这里存那么多其实是有问题的 ...
        T t0 = rAnnotations.getAnnotations().get(0);
        String imgName = t0.getImgName();
        Microtask mt0 = mtByImg(microtaskJPA, imgName);

        if (mt0.getLastRequestTime().isAfter(LocalDateTime.now().minusMinutes(TASK_DURATION))) {
            long taskId = mt0.getTaskId();
            Worker worker = userByUsername(workerJPA, username);

            Contract translucent = contractJPA.findByTaskIdByUsername(taskId, username);
            if(translucent == null) {
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
                Microtask microtask1 = mtByImg(microtaskJPA, a.getImgName());

                a.setAnnotationStatus(AnnotationStatus.REVIEWABLE);
                a.setUsername(username);
                a.setMicrotaskId(microtask1.getMicrotaskId());
//                a.setParallel(0);
                annotationJPA.save(a);

//                microtask1.setMicrotaskStatus(MicrotaskStatus.UNREVIEWED);
                microtaskJPA.save(microtask1);
            });
        } else {
            throw new MyNotValidException();
        }
    }
}


