package foursomeSE.schedule;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.service.task.FinishTaskService;
import foursomeSE.service.task.FinishTaskServiceImpl;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class SchedulerTask implements MyConstants {
    private FinishTaskService finishTaskService;
    private MicrotaskJPA microtaskJPA;
    private AnnotationJPA annotationJPA;

    public SchedulerTask(FinishTaskService finishTaskService,
                         MicrotaskJPA microtaskJPA,
                         AnnotationJPA annotationJPA) {
        this.finishTaskService = finishTaskService;
        this.microtaskJPA = microtaskJPA;
        this.annotationJPA = annotationJPA;
    }

    @Scheduled(fixedRate = 6000)
    public void reportCurrentTime() {
        finishTaskService.checkTask();
        checkMicroTask();
        checkInspection();
    }


    /**
     * 先写这儿
     */
    private void checkMicroTask() {
        microtaskJPA.checkUnfinished(LocalDateTime.now().minusMinutes(TASK_DURATION));
    }

    private void checkInspection() {
//        while (!CriticalSection.inspectRecords.isEmpty()) {
//            CriticalSection.Item item = CriticalSection.inspectRecords.get(0);
//            if (item.requestTime.isBefore(LocalDateTime.now().minusMinutes(INSPECTION_DURATION))) {
//                CriticalSection.Item i0 = CriticalSection.inspectRecords.get(0);
//                Annotation a = annotationJPA.findById(i0.annotationId).orElseThrow(() -> new MyObjectNotFoundException(""));
//                a.setParallel(a.getParallel() - 1);
//                annotationJPA.save(a);
//
//                CriticalSection.inspectRecords.remove(0);
//            } else {
//                break;
//            }
//        }

        // 其实写成上面那样，就够了

        ArrayList<CriticalSection.Item> toBeDeleted = CriticalSection.inspectRecords.stream()
                .filter(item -> item.requestTime.isBefore(
                        LocalDateTime.now().minusMinutes(INSPECTION_DURATION)
                )).collect(Collectors.toCollection(ArrayList::new));

        toBeDeleted.forEach(item -> {
            System.out.println();
            Annotation a = annotationJPA.findById(item.annotationId).orElseThrow(() -> new MyObjectNotFoundException(""));
            a.setParallel(a.getParallel() - 1);
            annotationJPA.save(a);

            CriticalSection.inspectRecords.remove(item);
        });
    }
}
