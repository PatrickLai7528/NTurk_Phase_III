package foursomeSE.service.contract;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.contract.ContractStatus;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.message.Message;
import foursomeSE.entity.message.MessageType;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyAccessDeniedException;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.task.FinishTaskService;
import foursomeSE.service.task.FinishTaskServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static foursomeSE.service.contract.ContractUtils.contractByTaskIdAndUsername;
import static foursomeSE.service.task.TaskUtils.taskById;
import static foursomeSE.service.user.UserUtils.userByUsername;

@Service
public class UpperContractServiceImpl implements UpperContractService {
    // private LowerContractService service;
    private ContractJPA contractJPA;
    //    private LowerUserService<Worker> workerService;
    private WorkerJPA workerJPA;
    //    private LowerUserService<Requester> requesterService;
    private RequesterJPA requesterJPA;

    //    private LowerTaskService taskService;
    private TaskJPA taskJPA;

    private MessageJPA messageJPA;

    private FinishTaskService finishTaskService;

    public UpperContractServiceImpl(ContractJPA contractJPA,
                                    WorkerJPA workerJPA,
                                    RequesterJPA requesterJPA,
                                    TaskJPA taskJPA,
                                    MessageJPA messageJPA,
                                    FinishTaskService finishTaskService) {
        this.contractJPA = contractJPA;
        this.workerJPA = workerJPA;
        this.requesterJPA = requesterJPA;
        this.taskJPA = taskJPA;
        this.messageJPA = messageJPA;
        this.finishTaskService = finishTaskService;
    }

    @Override
    public Contract getByTaskIdByWorkerUsername(long taskId, String username) {
        return contractByTaskIdAndUsername(contractJPA, workerJPA, taskId, username);
    }

    @Override
    public List<Contract> getByTaskIdByRequesterUsername(long taskId, String username) {
        long requesterId = userByUsername(requesterJPA, username).getId();
        if (taskById(taskJPA, taskId).getRequesterId() == requesterId) {
            return contractJPA.findByTaskId(taskId);
        }
        throw new MyAccessDeniedException();

    }

    @Override
    public void add(Contract contract, String username) {
        contract.setWorkerId(userByUsername(workerJPA, username).getId());
        contract.setContractStatus(ContractStatus.IN_PROGRESS);
        contract.setLastEditTime(LocalDateTime.now());
        contractJPA.save(contract);

        String taskName = taskById(taskJPA, contract.getTaskId()).getTaskName();
        messageJPA.save(Message.createMessage(username, MessageType.MAKE_CONTRACT, new String[]{taskName}));
    }

    @Override
    public void fulfilContract(long taskId, String username) {
        Contract toBeCompleted = contractByTaskIdAndUsername(contractJPA, workerJPA, taskId, username);

        toBeCompleted.setContractStatus(ContractStatus.COMPLETED);
        toBeCompleted.setLastEditTime(LocalDateTime.now());
        contractJPA.save(toBeCompleted);

        Task task = taskById(taskJPA, taskId);

        long size = contractJPA.countByTaskIdAndContractStatus(taskId, ContractStatus.COMPLETED);

        if (task.getCapacity() <= size) {
            finishTaskService.enterReview(task);
        }
        messageJPA.save(Message.createMessage(username, MessageType.FULFIL_CONTRACT, new String[]{task.getTaskName()}));
    }

    @Override
    public Contract getByTaskIdForInspection(long taskId, String username) {
        List<Contract> cddts = contractJPA.findByTaskIdForInspection(taskId, userByUsername(workerJPA, username).getId());
        if (cddts.isEmpty()) {
            return null; // 这样可能不好？
        }
        return cddts.get(0);
    }

    //    /**
//     * private methods
//     */
//    private Contract contractByTaskIdAndUsername(long taskId, String username) {
//        return contractJPA
//                .findByTaskIdAndWorkerId(taskId, userByUsername(workerJPA, username).getId())
//                .orElseThrow(() -> new MyObjectNotFoundException("contract with taskId " + taskId + " and worker username " + username + " is not found"));
//    }
}