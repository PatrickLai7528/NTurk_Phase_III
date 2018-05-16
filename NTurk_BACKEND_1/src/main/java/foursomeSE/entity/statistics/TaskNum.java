package foursomeSE.entity.statistics;

import foursomeSE.entity.task.TaskCategory;

public class TaskNum {
    private TaskCategory name;
    private int value;

    public TaskNum() {
    }

    public TaskNum(TaskCategory name, int value) {
        this.name = name;
        this.value = value;
    }

    public TaskCategory getName() {
        return name;
    }

    public void setName(TaskCategory name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskNum)) return false;

        TaskNum taskNum = (TaskNum) o;

        if (value != taskNum.value) return false;
        return name == taskNum.name;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return "TaskNum{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}
