package foursomeSE.entity.task;

import foursomeSE.entity.contract.ContractStatus;
import foursomeSE.entity.task.Task;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class CTask extends Task {
//    private int attendance; // 已完成的人数
//    private ContractStatus contractStatus;
    private String requesterName;

    // TODO
    // 对于这个任务而言
    private int draw; // 还需要做的
    private int verifyQuality; // 做了待审核的
    private int verifyCoverage; // 现在正在做的

//    private int haveDone; // 具体这个工人做了多少
//    private int haveReviewed; // 具体这个工人审了多少

//    public CTask(Task task, int attendance, String requesterName, ContractStatus contractStatus) {
//        this(task, attendance, requesterName);
//        this.contractStatus = contractStatus;
//    }

    public CTask(){
    }

    public CTask(Task task) {
        super(task);
    }

    //    public CTask(Task task, int attendance, String requesterName) {
//        this.attendance = attendance;
//        this.requesterName = requesterName;
//        setSameFields(this, task);
//    }

    /**
     * getters and setters
     * */

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getVerifyQuality() {
        return verifyQuality;
    }

    public void setVerifyQuality(int verifyQuality) {
        this.verifyQuality = verifyQuality;
    }

    public int getVerifyCoverage() {
        return verifyCoverage;
    }

    public void setVerifyCoverage(int verifyCoverage) {
        this.verifyCoverage = verifyCoverage;
    }
}