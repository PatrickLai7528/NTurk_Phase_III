package foursomeSE.schedule;

import foursomeSE.service.task.FinishTaskService;
import foursomeSE.service.task.FinishTaskServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerTask {
    private FinishTaskService finishTaskService;

    public SchedulerTask(FinishTaskService finishTaskService) {
        this.finishTaskService = finishTaskService;
    }

    @Scheduled(fixedRate = 6000)
    public void reportCurrentTime() {
        finishTaskService.checkTask();
    }
}
