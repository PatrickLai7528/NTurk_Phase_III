package foursomeSE.entity.task;

import foursomeSE.entity.communicate.CTask;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue
    private long taskId;
    @NotNull
    private String taskName;
    private String taskDescription;
    @NotNull
    private long requesterId;
    @NotNull
    private LocalDateTime createTime;
    @NotNull
    private LocalDateTime endTime; // 这个一定有
    @NotNull
    private LocalDateTime ddl; // 这个是标注截止，上面的endTime评审也截止。

    @NotNull
    private WorkerRequirement workerRequirement;
    private double requiredExperience;
    @Basic
    @Column(length=50000)
    private ArrayList<Long> nominees;

    @NotNull
    private TaskCategory taskCategory;
    @Basic
    @Column(length=50000)
    private ArrayList<String> questions;

    @NotNull
    private RewardStrategy rewardStrategy;
    private double totalReward;

    //    private double rewardPerPerson; // 这个属性不要了，就是total/capacity
    private int capacity; // 人数限制，一定有，如果没有就是2147483648

    @Basic
    @NotNull
    @Column(length=100000)
    private ArrayList<String> imgNames; // 需要先用taskCategory来判断什么类型到什么数库里取

    @NotNull
    private TaskStatus taskStatus;

    public Task() {
    }

    public Task(CTask cTask) {
        setSameFields(this, cTask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return (int) (taskId ^ (taskId >>> 32));
    }

//    public double getRewardPerPerson() {
//        if (rewardStrategy == RewardStrategy.INDIVIDUAL) {
//            return totalReward / capacity;
//        }
//        throw new UnsupportedOperationException();
//    }

    /**
     * getters and setters
     */

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(long requesterId) {
        this.requesterId = requesterId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public WorkerRequirement getWorkerRequirement() {
        return workerRequirement;
    }

    public void setWorkerRequirement(WorkerRequirement workerRequirement) {
        this.workerRequirement = workerRequirement;
    }

    public double getRequiredExperience() {
        return requiredExperience;
    }

    public void setRequiredExperience(double requiredExperience) {
        this.requiredExperience = requiredExperience;
    }

    public ArrayList<Long> getNominees() {
        return nominees;
    }

    public void setNominees(ArrayList<Long> nominees) {
        this.nominees = nominees;
    }

    public TaskCategory getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(TaskCategory taskCategory) {
        this.taskCategory = taskCategory;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }

    public RewardStrategy getRewardStrategy() {
        return rewardStrategy;
    }

    public void setRewardStrategy(RewardStrategy rewardStrategy) {
        this.rewardStrategy = rewardStrategy;
    }

    public double getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(double totalReward) {
        this.totalReward = totalReward;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<String> getImgNames() {
        return imgNames;
    }

    public void setImgNames(ArrayList<String> imgNames) {
        this.imgNames = imgNames;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public LocalDateTime getDdl() {
        return ddl;
    }

    public void setDdl(LocalDateTime ddl) {
        this.ddl = ddl;
    }
}
