package foursomeSE.entity.communicate;

import foursomeSE.entity.contract.ContractStatus;
import foursomeSE.entity.task.Task;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class CTask extends Task {
    private int attendance; // 已完成的人数
    private ContractStatus contractStatus;
    private String requesterName;

    public CTask(Task task, int attendance, String requesterName, ContractStatus contractStatus) {
        this(task, attendance, requesterName);
        this.contractStatus = contractStatus;
    }

    public CTask(){

    }

    public CTask(Task task) {
        setSameFields(this, task);
    }

    public CTask(Task task, int attendance, String requesterName) {
        this.attendance = attendance;
        this.requesterName = requesterName;
        setSameFields(this, task);
    }

    /**
     * getters and setters
     * */
    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }
}