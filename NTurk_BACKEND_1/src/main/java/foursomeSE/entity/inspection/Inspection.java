package foursomeSE.entity.inspection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "inspections")
public class Inspection implements Serializable {
    @Id
    @GeneratedValue
    private long inspectionId;

    @NotNull
    private long inspectionContractId;

    @NotNull
    private long annotationId;

    @NotNull
    private double rate;

    public long getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(long inspectionId) {
        this.inspectionId = inspectionId;
    }

    public long getInspectionContractId() {
        return inspectionContractId;
    }

    public void setInspectionContractId(long inspectionContractId) {
        this.inspectionContractId = inspectionContractId;
    }

    public long getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(long annotationId) {
        this.annotationId = annotationId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
