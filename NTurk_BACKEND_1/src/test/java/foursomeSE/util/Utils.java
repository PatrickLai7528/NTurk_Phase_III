package foursomeSE.util;

import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static foursomeSE.service.contract.ContractUtils.contractByTaskIdAndUsername;

@Service
public class Utils {
    @Autowired
    private ContractJPA contractJPA;
    @Autowired
    private WorkerJPA workerJPA;
    @Autowired
    private TaskJPA taskJPA;

    public long getContractIdForTask1ByI(int i) {
        return contractByTaskIdAndUsername(
                contractJPA,
                workerJPA,
                taskJPA.findByTaskName("task1").getTaskId(),
                "worker" + i + "@ex.com"
        ).getContractId();
    }
}
