package foursomeSE.service.inspection;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.communicate.CInspection;
import foursomeSE.entity.communicate.CInspectionContract;
import foursomeSE.entity.communicate.EnterInspectionResponse;
import foursomeSE.entity.inspection.Inspection;
import foursomeSE.entity.inspection.InspectionContract;
import foursomeSE.entity.inspection.RInspections;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.task.TaskStatus;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyNotValidException;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.inspection.InspectionContractJPA;
import foursomeSE.jpa.inspection.InspectionJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.annotation.AnnotationUtils.anttById;
import static foursomeSE.service.task.TaskUtils.mtById;
import static foursomeSE.service.task.TaskUtils.taskById;
import static foursomeSE.service.user.UserUtils.userByUsername;

@Service
public class UpperInspectionServiceImpl implements UpperInspectionService, MyConstants {
    private InspectionJPA inspectionJPA;
    private InspectionContractJPA inspectionContractJPA;
    private WorkerJPA workerJPA;
    private AnnotationJPA annotationJPA;
    private MicrotaskJPA microtaskJPA;
    private TaskJPA taskJPA;

    public UpperInspectionServiceImpl(InspectionJPA inspectionJPA,
                                      InspectionContractJPA inspectionContractJPA,
                                      WorkerJPA workerJPA,
                                      AnnotationJPA annotationJPA,
                                      MicrotaskJPA microtaskJPA,
                                      TaskJPA taskJPA) {
        this.inspectionJPA = inspectionJPA;
        this.inspectionContractJPA = inspectionContractJPA;
        this.workerJPA = workerJPA;
        this.annotationJPA = annotationJPA;
        this.microtaskJPA = microtaskJPA;
        this.taskJPA = taskJPA;
    }

    @Override
    public void add(CInspectionContract cInspectionContract, String username) {
//        InspectionContract toBeAdded = new InspectionContract();
//        toBeAdded.setContractId(cInspectionContract.getContractId());
//        toBeAdded.setWorkerId(userByUsername(workerJPA, username).getId());
//        inspectionContractJPA.save(toBeAdded);
//
//        InspectionContract withId = inspectionContractJPA
//                .findByContractIdAndWorkerId(toBeAdded.getContractId(), toBeAdded.getWorkerId())
//                .orElseThrow(() -> new MyObjectNotFoundException("not possible?"));
//
//        cInspectionContract.getInspections().forEach(i -> {
//            i.setInspectionContractId(withId.getInspectionContractId());
//        });
//        inspectionJPA.saveAll(cInspectionContract.getInspections());
    }

    @Override
    public List<CInspection> getBestKth(String imgName, String username) {
        List<Object[]> emm = inspectionJPA.getBestForImgname(imgName);
        List<CInspection> result = new ArrayList<>();
        emm.forEach(os -> {
            CInspection e = new CInspection(
                    Long.parseLong(String.valueOf(os[0])),
                    Double.parseDouble(String.valueOf(os[1]))
            );

            result.add(e);
        });
        return result.subList(0, K);
    }

    @Override
    public EnterInspectionResponse enterInspection(long taskId, String username) {
//        long haveDone = annotationJPA.countByTaskIdAndUsername(taskId, username);
//        long haveInspected = inspectionJPA.countByTaskIdAndUsername(taskId, username);
//        if (haveInspected + NUM_OF_INSPECTION_PER_REQUEST > 2 * haveDone) {
//            throw new MyNotValidException();
//        }

        List<Long> ids = annotationJPA.getIdsByTaskId(taskId, username)
                .stream().map(BigInteger::longValue).collect(Collectors.toCollection(ArrayList::new));
        if (ids.size() > NUM_OF_INSPECTION_PER_REQUEST) {
            ids = ids.subList(0, NUM_OF_INSPECTION_PER_REQUEST);
        }

        ids.forEach(l -> {
            Annotation a = annotationJPA.findById(l).get();// 这是肯定的，所以故意没用，故意让他如果出错就报null pointer吧，但应该是不可能的
            a.setParallel(a.getParallel() + 1);
            annotationJPA.save(a);

            CriticalSection.Item item = new CriticalSection.Item();
            item.requestTime = LocalDateTime.now();
            item.username = username;
            item.annotationId = l;
            CriticalSection.inspectRecords.add(item);
        });


        EnterInspectionResponse result = new EnterInspectionResponse();
        result.setAnnotationIds(ids);
        return result;
    }

    @Override
    public void saveInspections(RInspections rInspections, String username) {
        if (rInspections.getInspections().isEmpty()) {
            throw new MyNotValidException();
        }

        Inspection ispt = rInspections.getInspections().get(0);
        if (CriticalSection.inspectRecords.stream()
                .anyMatch(i -> i.username.equals(username)
                        && i.annotationId == ispt.getAnnotationId())) {
            rInspections.getInspections().forEach(i -> {
                // 接下来就对于这个inspection对应的annotation，microtask，task，
                i.setUsername(username);
                inspectionJPA.save(i);

                Annotation annotation = anttById(annotationJPA, i.getAnnotationId());
                Microtask microtask = mtById(microtaskJPA, annotation.getMicrotaskId());
                Task task = taskById(taskJPA, microtask.getTaskId());
                Worker worker = userByUsername(workerJPA, annotation.getUsername());
                Worker me = userByUsername(workerJPA, username);

                annotation.setParallel(annotation.getParallel() - 1);
                annotationJPA.save(annotation);
                CriticalSection.inspectRecords.removeIf(ii -> ii.username.equals(username)
                        && ii.annotationId == ispt.getAnnotationId());

                me.setCredit(me.getCredit() + task.getRewardPerMicrotask() * INSPECTION_PERCENT);
                workerJPA.save(me);


                List<Inspection> haveInspected = inspectionJPA.findByAnnotationId(i.getAnnotationId());
                if (haveInspected.size() == INSPECTION_PER_CONTRACT) {
                    double rateSum = haveInspected.stream().mapToDouble(Inspection::getRate).sum();

                    if (rateSum >= ACCEPTED_SUM) {
                        annotation.setAnnotationStatus(AnnotationStatus.PASSED);
                        annotationJPA.save(annotation);
                        microtask.setMicrotaskStatus(MicrotaskStatus.PASSED);
                        microtaskJPA.save(microtask);

                        worker.setCredit(worker.getCredit() + task.getRewardPerMicrotask() * ANNOTATION_PERCENT);
                        workerJPA.save(worker);

                        List<Microtask> np = microtaskJPA.findByTaskIdNotPassed(microtask.getTaskId());
                        if (np.isEmpty()) {
                            task.setTaskStatus(TaskStatus.FINISHED);
                            taskJPA.save(task);
                        }
                    } else {
                        annotation.setAnnotationStatus(AnnotationStatus.FAILED);
                        annotationJPA.save(annotation);
                        microtask.setMicrotaskStatus(MicrotaskStatus.FAILED);
                        microtaskJPA.save(microtask);
                    }
                }
            });
        } else {
            throw new MyNotValidException();
        }

    }
}
