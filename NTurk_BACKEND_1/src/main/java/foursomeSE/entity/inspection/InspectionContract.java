package foursomeSE.entity.inspection;

import foursomeSE.entity.communicate.CInspectionContract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

@Entity
@Table(name = "inspection_contracts")
public class InspectionContract {
    @Id
    @GeneratedValue
    private long inspectionContractId;

    @NotNull
    private long contractId;

    @NotNull
    private long workerId;

    private InspectionContractStatus inspectionContractStatus;

    public InspectionContract() {
    }

    public InspectionContract(CInspectionContract cInspectionContract) {
        setSameFields(this, cInspectionContract);
    }

    public long getInspectionContractId() {
        return inspectionContractId;
    }

    public void setInspectionContractId(long inspectionContractId) {
        this.inspectionContractId = inspectionContractId;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public InspectionContractStatus getInspectionContractStatus() {
        return inspectionContractStatus;
    }

    public void setInspectionContractStatus(InspectionContractStatus inspectionContractStatus) {
        this.inspectionContractStatus = inspectionContractStatus;
    }
}
