package foursomeSE.entity.contract;

import java.time.LocalDateTime;

public class Contract {
    private long contractId;
    private long taskId;
    private long workerId;
    private ContractStatus contractStatus;
    private LocalDateTime lastEditTime;

    public Contract() {
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }

    public LocalDateTime getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(LocalDateTime lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "contractId=" + contractId +
                ", taskId=" + taskId +
                ", workerId=" + workerId +
                ", contractStatus=" + contractStatus +
                ", lastEditTime=" + lastEditTime +
                '}';
    }
}
