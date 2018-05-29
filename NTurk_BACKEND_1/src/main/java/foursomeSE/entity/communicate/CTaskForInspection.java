package foursomeSE.entity.communicate;

import foursomeSE.entity.task.Task;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class CTaskForInspection extends Task {
    private int mandatoryTime; // 表示有几次是必须参加的。

    public CTaskForInspection(Task task) {
        setSameFields(this, task);
    }

    public int getMandatoryTime() {
        return mandatoryTime;
    }

    public void setMandatoryTime(int mandatoryTime) {
        this.mandatoryTime = mandatoryTime;
    }
}
