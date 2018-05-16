package foursomeSE.service.task;

import foursomeSE.entity.task.Task;
import foursomeSE.service.common.CommonCongruentService;

public interface LowerTaskService extends CommonCongruentService<Task> {
    boolean isTaskBelongTo(long taskId, long requesterId);
}
