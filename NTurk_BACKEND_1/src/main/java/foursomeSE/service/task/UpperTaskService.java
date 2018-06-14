package foursomeSE.service.task;

import foursomeSE.entity.communicate.report.Reports;
import foursomeSE.entity.task.CTask;
import foursomeSE.entity.communicate.CTaskForInspection;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.statistics.TaskGrowth;
import foursomeSE.entity.statistics.TaskNum;
import foursomeSE.entity.statistics.TaskParticipation;
import foursomeSE.entity.statistics.TaskStatusData;
import foursomeSE.entity.task.Task;

import java.util.List;

public interface UpperTaskService {
//    /**
//     * 不确定是怎么用这个方法的（下面说的都没做）
//     * 如果是Requester，那么需要这个任务是他发布的（他不能以requester的身份看其他人的哪怕是新任务）
//     * 如果是Worker，可以看到没有结束的新任务（不能是指定模式下且没指定他的），和他领取过的所有任务。（也就是getWorkerTasks和getNewTasks里规定的）
//     * */


    // 前端需要在一个任务结束后判断task是否还能继续，这时就调用这个方法
    CTask getById(long id);

    /**
     * task的要求:（没做）
     *  taskName，taskDescription前端来
     *  requesterId 后端设成username一致的就好
     *  createTime后端设
     *  workerRequirement必须有，据此判断requiredExperience和nominees(必须是存在的id)。
     *  taskCategory必须有，据此判断是否有tagsForAnnotation
     *  rewardPerMicrotask必须有
     *  taskStatus后端设
     *  imgNames必须有
     * 扣钱，不够报错。
     * */
    void add(Task task, String username);

    /**
     * 返回当前在进行中的，且worker没有领取的任务
     * */
    List<CTask> getNewTasks(String username);

    /**
     * 返回worker曾领取过的任务
     * */
    List<CTask> getWorkerTasks(String username);

    /**
     * 返回requester所有的任务
     * */
    List<CTask> getRequesterTasks(String username);

    /**
     *
     * */
    EnterResponse enterTask(long taskId, String username);




//    /**
//     * 返回worker自己没参加过的underReview的task
//     * */
//    List<CTask> getNewInspectionTasks(String username);
//
//    /**
//     * 返回worker自己做过的underReview的task，不管有没有完成mandatory time
//     * */
//    List<CTaskForInspection> getWorkerInspectionTasks(String username);

    /**
     * Statistics
     */
    List<TaskNum> getTaskNums();

    List<TaskGrowth> getTaskGrowth();

    List<TaskStatusData> getTaskStatus();

    List<TaskParticipation> getTaskParticipation();
}
