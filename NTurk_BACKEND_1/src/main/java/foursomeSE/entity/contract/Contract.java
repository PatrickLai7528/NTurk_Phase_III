package foursomeSE.entity.contract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue
    private long contractId;

    @NotNull
    private long taskId;

    @NotNull
    private long workerId;

//    @NotNull
//    private ContractStatus contractStatus;

    @NotNull
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
                ", lastEditTime=" + lastEditTime +
                '}';
    }
}
