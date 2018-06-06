package foursomeSE.schedule;

import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.service.task.FinishTaskService;
import foursomeSE.service.task.FinishTaskServiceImpl;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SchedulerTask implements MyConstants {
    private FinishTaskService finishTaskService;
    private MicrotaskJPA microtaskJPA;

    public SchedulerTask(FinishTaskService finishTaskService,
                         MicrotaskJPA microtaskJPA) {
        this.finishTaskService = finishTaskService;
        this.microtaskJPA = microtaskJPA;
    }

    @Scheduled(fixedRate = 6000)
    public void reportCurrentTime() {
        finishTaskService.checkTask();
        checkMicroTask();
    }


    /**
     * 先写这儿
     */
    private void checkMicroTask() {
        microtaskJPA.checkUnfinished(LocalDateTime.now().minusMinutes(TASK_DURATION));
    }

    private void checkInspection() {
        while (!CriticalSection.inspectRecords.isEmpty()) {
            CriticalSection.Item item = CriticalSection.inspectRecords.get(0);
            if (item.requestTime.isBefore(LocalDateTime.now().minusMinutes(INSPECTION_DURATION))) {
                CriticalSection.inspectRecords.remove(0);
            } else {
                break;
            }
        }
    }
}
