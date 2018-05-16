package foursomeSE.service.task;

import foursomeSE.entity.task.Task;

public interface FinishTaskService {
    /**
     * task传入前已确认完成
     * 给所有完成任务的人发奖励
     * 给所有领了但没做完的人abort掉
     * 如果是individual模式，且没有达到人数，退款，退款还有一种情况，就是一个做的人都没有
     * */
    void finishTask(Task task);

    /**
     * 供schedule轮询，检查任务是否到期
     * */
    void checkTask();
}
