package foursomeSE.service.contract;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.contract.ContractStatus;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.message.Message;
import foursomeSE.entity.message.MessageType;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyAccessDeniedException;
import foursomeSE.service.message.LowerMessageService;
import foursomeSE.service.task.FinishTaskService;
import foursomeSE.service.task.FinishTaskServiceImpl;
import foursomeSE.service.task.LowerTaskService;
import foursomeSE.service.user.lower.LowerUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UpperContractServiceImpl implements UpperContractService {
    private LowerContractService service;
    private LowerUserService<Worker> workerService;
    private LowerUserService<Requester> requesterService;
    private LowerTaskService taskService;
    private LowerMessageService lowerMessageService;

    private FinishTaskService finishTaskService;

    public UpperContractServiceImpl(LowerContractService service,
                                    LowerUserService<Worker> workerService,
                                    LowerUserService<Requester> requesterService,
                                    LowerTaskService taskService,
                                    LowerMessageService lowerMessageService,
                                    FinishTaskService finishTaskServicd) {
        this.service = service;
        this.workerService = workerService;
        this.requesterService = requesterService;
        this.taskService = taskService;
        this.lowerMessageService = lowerMessageService;
        this.finishTaskService = finishTaskServicd;
    }

    @Override
    public Contract getByTaskIdByWorkerUsername(long taskId, String username) {
        return service.getOneBy(c -> c.getTaskId() == taskId
                && c.getWorkerId() == workerService.usernameToId(username));
    }

    @Override
    public List<Contract> getByTaskIdByRequesterUsername(long taskId, String username) {
        long requesterId = requesterService.usernameToId(username);
        if (taskService.isTaskBelongTo(taskId, requesterId)) {
            return service.getLotBy(c -> c.getTaskId() == taskId);
        }
        throw new MyAccessDeniedException();

    }

    @Override
    public void add(Contract contract, String username) {
        contract.setWorkerId(workerService.usernameToId(username));
        contract.setContractStatus(ContractStatus.IN_PROGRESS);
        contract.setLastEditTime(LocalDateTime.now());
        service.add(contract);

        String taskName = taskService.getById(contract.getTaskId()).getTaskName();
        lowerMessageService.add(Message.createMessage(username, MessageType.MAKE_CONTRACT, new String[]{taskName}));
    }

    @Override
    public void fulfilContract(long taskId, String username) {
        long workerId = workerService.usernameToId(username);
        Contract toBeCompleted = service.getOneBy(c -> c.getTaskId() == taskId && c.getWorkerId() == workerId);

        if (workerId == toBeCompleted.getWorkerId()) {
            toBeCompleted.setContractStatus(ContractStatus.COMPLETED);
            toBeCompleted.setLastEditTime(LocalDateTime.now());
            service.update(toBeCompleted);

            Task task = taskService.getById(taskId);
            List<Contract> contracts =
                    service.getLotBy(c -> c.getTaskId() == taskId
                            && c.getContractStatus() == ContractStatus.COMPLETED);

            if (task.getCapacity() <= contracts.size()) {
                finishTaskService.finishTask(task);
            }
            lowerMessageService.add(Message.createMessage(username, MessageType.FULFIL_CONTRACT, new String[]{task.getTaskName()}));
        } else {
            throw new MyAccessDeniedException();
        }
    }
}