package foursomeSE.entity.statistics;

import foursomeSE.entity.task.TaskCategory;

public class TaskParticipation {
    private TaskCategory name;
    private int value;

    public TaskParticipation() {
    }

    public TaskParticipation(TaskCategory name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public TaskCategory getName() {
        return name;
    }

    public void setName(TaskCategory name) {
        this.name = name;
    }
}
