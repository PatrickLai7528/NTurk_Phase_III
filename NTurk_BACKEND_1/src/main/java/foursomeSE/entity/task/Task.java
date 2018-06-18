package foursomeSE.entity.task;

//import foursomeSE.util.LocalDateTimeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private long requesterId; // NN
    @NotNull
//    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createTime; // NN

    @NotNull
    private WorkerRequirement workerRequirement;
    private double requiredExperience;
    @Basic
    @Column(length=50000)
    private ArrayList<Long> nominees; // 先放这，名存实亡

    @NotNull
    private TaskCategory taskCategory;

    @Basic
    @Column(length=50000)
    private ArrayList<String> tagsForAnnotation;

    @NotNull
    private double rewardPerMicrotask;

//    @OneToMany(orphanRemoval = true)
//    @JoinColumn(name = "task_id")
    @Transient
    private List<String> taskTags = new ArrayList<>();

    @Transient
    private List<String> imgNames;

    @NotNull
    private TaskStatus taskStatus; // NN

    @NotNull
    private int isCollecting; // NN

    public Task() {
    }

    public Task(Task task) {
        setSameFields(this, task);
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

    public ArrayList<String> getTagsForAnnotation() {
        return tagsForAnnotation;
    }

    public void setTagsForAnnotation(ArrayList<String> tagsForAnnotation) {
        this.tagsForAnnotation = tagsForAnnotation;
    }

    public double getRewardPerMicrotask() {
        return rewardPerMicrotask;
    }

    public void setRewardPerMicrotask(double rewardPerMicrotask) {
        this.rewardPerMicrotask = rewardPerMicrotask;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getIsCollecting() {
        return isCollecting;
    }

    public void setIsCollecting(int isCollecting) {
        this.isCollecting = isCollecting;
    }

    public List<String> getTaskTags() {
        return taskTags;
    }

    public void setTaskTags(List<String> taskTags) {
        this.taskTags = taskTags;
    }

    public List<String> getImgNames() {
        return imgNames;
    }

    public void setImgNames(List<String> imgNames) {
        this.imgNames = imgNames;
    }
}
