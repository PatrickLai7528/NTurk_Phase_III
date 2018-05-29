package foursomeSE.entity.communicate;

import foursomeSE.entity.inspection.Inspection;
import foursomeSE.entity.inspection.InspectionContract;

import java.util.List;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class CInspectionContract extends InspectionContract {
    private List<Inspection> inspections;

    public CInspectionContract(InspectionContract inspectionContract) {
        setSameFields(this, inspectionContract);
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public void setInspections(List<Inspection> inspections) {
        this.inspections = inspections;
    }
}
