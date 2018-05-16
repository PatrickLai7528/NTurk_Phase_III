package foursomeSE.entity.statistics;

import foursomeSE.entity.task.TaskCategory;

public class TaskStatusData {
    private TaskCategory taskCategory;
    private int inProgress, completed, abort;

    public TaskStatusData() {
    }

    public TaskStatusData(TaskCategory taskCategory, int inProgress, int completed, int abort) {
        this.taskCategory = taskCategory;
        this.inProgress = inProgress;
        this.completed = completed;
        this.abort = abort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskStatusData)) return false;

        TaskStatusData that = (TaskStatusData) o;

        if (inProgress != that.inProgress) return false;
        if (completed != that.completed) return false;
        if (abort != that.abort) return false;
        return taskCategory == that.taskCategory;
    }

    @Override
    public int hashCode() {
        int result = taskCategory != null ? taskCategory.hashCode() : 0;
        result = 31 * result + inProgress;
        result = 31 * result + completed;
        result = 31 * result + abort;
        return result;
    }

    @Override
    public String toString() {
        return "TaskStatusData{" +
                "taskCategory=" + taskCategory +
                ", inProgress=" + inProgress +
                ", completed=" + completed +
                ", abort=" + abort +
                '}';
    }

    public TaskCategory getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(TaskCategory taskCategory) {
        this.taskCategory = taskCategory;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getAbort() {
        return abort;
    }

    public void setAbort(int abort) {
        this.abort = abort;
    }
}
