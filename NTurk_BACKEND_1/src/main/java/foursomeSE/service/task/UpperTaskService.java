package foursomeSE.service.task;

import foursomeSE.entity.communicate.CTask;
import foursomeSE.entity.communicate.CTaskForInspection;
import foursomeSE.entity.statistics.TaskGrowth;
import foursomeSE.entity.statistics.TaskNum;
import foursomeSE.entity.statistics.TaskParticipation;
import foursomeSE.entity.statistics.TaskStatusData;

import java.util.List;

public interface UpperTaskService {
    /**
     * 不确定是怎么用这个方法的（下面说的都没做）
     * 如果是Requester，那么需要这个任务是他发布的（他不能以requester的身份看其他人的哪怕是新任务）
     * 如果是Worker，可以看到没有结束的新任务（不能是指定模式下且没指定他的），和他领取过的所有任务。（也就是getWorkerTasks和getNewTasks里规定的）
     * */
    CTask getById(long id);

    /**
     * TODO 其实这个不应该是CTask
     * task的要求:（没做）
     *  taskName，taskDescription(可以空)
     *  requesterId set成和username一致的
     *  createTime自己设，endTime必须有
     *  workerRequirement必须有，据此判断requiredExperience和nominees(必须是存在的id)。
     *  taskCategory必须有，据此判断question
     *  rewardStrategy必须有。如果是指定模式：必须是INDIVIDUAL(虽然好像也没有关系)，必须有capacity。
     *
     * 扣钱，不够报错。
     * */
    void add(CTask task, String username);

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
     * 返回worker自己没参加过的underReview的task
     * */
    List<CTaskForInspection> getNewInspectionTasks(String username);

    /**
     * 返回worker自己做过的underReview的task，不管有没有完成mandatory time
     * */
    List<CTaskForInspection> getWorkerInspectionTasks(String username);

    /**
     * Statistics
     */
    List<TaskNum> getTaskNums();

    List<TaskGrowth> getTaskGrowth();

    List<TaskStatusData> getTaskStatus();

    List<TaskParticipation> getTaskParticipation();
}
