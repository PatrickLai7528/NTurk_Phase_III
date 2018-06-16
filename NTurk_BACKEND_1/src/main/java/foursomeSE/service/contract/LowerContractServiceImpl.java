package foursomeSE.service.contract;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.message.Message;
import foursomeSE.entity.message.MessageType;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyAccessDeniedException;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.task.FinishTaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static foursomeSE.service.contract.ContractUtils.contractByTaskIdAndUsername;
import static foursomeSE.service.task.TaskUtils.taskById;
import static foursomeSE.service.user.UserUtils.userByUsername;

@Service
public class LowerContractServiceImpl implements LowerContractService {
    private ContractJPA contractJPA;
    private WorkerJPA workerJPA;

    public LowerContractServiceImpl(ContractJPA contractJPA, WorkerJPA workerJPA) {
        this.contractJPA = contractJPA;
        this.workerJPA = workerJPA;
    }

    @Override
    public void recordEnterTask(long taskId, String username) {
        Worker worker = userByUsername(workerJPA, username);

        Contract translucent = contractJPA.findByTaskIdByUsername(taskId, username);
        if (translucent == null) {
            Contract c = new Contract();
            c.setTaskId(taskId);
            c.setWorkerId(worker.getId());
            c.setLastEditTime(LocalDateTime.now());
            contractJPA.save(c);
        } else {
            translucent.setLastEditTime(LocalDateTime.now());
            contractJPA.save(translucent);
        }
    }
}