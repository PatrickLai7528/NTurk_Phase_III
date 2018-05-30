package foursomeSE.service.inspection;

import foursomeSE.entity.communicate.CInspectionContract;
import foursomeSE.entity.inspection.Inspection;
import foursomeSE.entity.inspection.InspectionContract;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.inspection.InspectionContractJPA;
import foursomeSE.jpa.inspection.InspectionJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.stereotype.Service;

import java.util.List;

import static foursomeSE.service.user.UserUtils.userByUsername;

@Service
public class UpperInspectionServiceImpl implements UpperInspectionService {
    private InspectionJPA inspectionJPA;
    private InspectionContractJPA inspectionContractJPA;
    private WorkerJPA workerJPA;

    public UpperInspectionServiceImpl(InspectionJPA inspectionJPA,
                                      InspectionContractJPA inspectionContractJPA,
                                      WorkerJPA workerJPA) {
        this.inspectionJPA = inspectionJPA;
        this.inspectionContractJPA = inspectionContractJPA;
        this.workerJPA = workerJPA;
    }

    @Override
    public void add(CInspectionContract cInspectionContract, String username) {
        InspectionContract toBeAdded = new InspectionContract();
        toBeAdded.setContractId(cInspectionContract.getContractId());
        toBeAdded.setWorkerId(userByUsername(workerJPA, username).getId());
        inspectionContractJPA.save(toBeAdded);

        InspectionContract withId = inspectionContractJPA
                .findByContractIdAndWorkerId(toBeAdded.getContractId(), toBeAdded.getWorkerId())
                .orElseThrow(() -> new MyObjectNotFoundException("not possible?"));

        cInspectionContract.getInspections().forEach(i -> {
            i.setInspectionContractId(withId.getInspectionContractId());
        });
        inspectionJPA.saveAll(cInspectionContract.getInspections());
    }

    @Override
    public List<Inspection> getBestKth(String imgName, String username) {
        // TODO 真的todo
        return null;
    }
}
