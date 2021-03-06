package foursomeSE.entity.communicate;

import foursomeSE.entity.task.CTask;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class CTaskForInspection extends CTask {
    private int mandatoryTime; // 表示有几次是必须参加的。

    public CTaskForInspection(CTask task) {
        setSameFields(this, task);
    }

    public int getMandatoryTime() {
        return mandatoryTime;
    }

    public void setMandatoryTime(int mandatoryTime) {
        this.mandatoryTime = mandatoryTime;
    }
}
