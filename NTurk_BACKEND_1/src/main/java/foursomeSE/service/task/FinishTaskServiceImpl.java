package foursomeSE.service.task;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.contract.ContractStatus;
import foursomeSE.entity.task.RewardStrategy;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.message.Message;
import foursomeSE.entity.message.MessageType;
import foursomeSE.entity.task.TaskStatus;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static foursomeSE.service.user.UserUtils.userById;

@Service
public class FinishTaskServiceImpl implements FinishTaskService {
//    private LowerUserService<Worker> lowerWorkerService;
    private WorkerJPA workerJPA;
//    private LowerTaskService lowerTaskService;
    private TaskJPA taskJPA;
//    private LowerUserService<Requester> lowerRequesterService;
    private RequesterJPA requesterJPA;
    //    private LowerContractService lowerContractService;
    private ContractJPA contractJPA;

    private MessageJPA messageJPA;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public FinishTaskServiceImpl(WorkerJPA workerJPA,
                                 TaskJPA taskJPA,
                                 RequesterJPA requesterJPA,
                                 ContractJPA contractJPA,
                                 MessageJPA messageJPA) {
        this.workerJPA = workerJPA;
        this.taskJPA = taskJPA;
        this.requesterJPA = requesterJPA;
        this.contractJPA = contractJPA;
        this.messageJPA = messageJPA;
    }

    @Override
    public void finishTask(Task task) {
        task.setTaskStatus(TaskStatus.FINISHED);
//        lowerTaskService.update(task);
        taskJPA.save(task);

        Requester requester = userById(requesterJPA, task.getRequesterId());
//                lowerRequesterService.getById(task.getRequesterId());
        List<Contract> completedContracts = contractJPA.findByTaskIdAndContractStatus(task.getTaskId(), ContractStatus.COMPLETED);
//                lowerContractService
//                        .getLotBy(c -> c.getTaskId() == task.getTaskId()
//                                && c.getContractStatus() == ContractStatus.COMPLETED);
        List<Contract> unfinishedContracts = contractJPA.findByTaskIdAndContractStatus(task.getTaskId(), ContractStatus.IN_PROGRESS);
//                lowerContractService
//                        .getLotBy(c -> c.getTaskId() == task.getTaskId()
//                                && c.getContractStatus() == ContractStatus.IN_PROGRESS);

        if (completedContracts.size() == 0 && unfinishedContracts.size() == 0) {
            messageJPA.save(Message.createMessage(requester.getEmailAddress(), MessageType.FINISH_TASK, new String[]{
                    task.getTaskName(),
                    "无人参与"
            }));

            requester.setCredit(requester.getCredit() + task.getTotalReward());
//            lowerRequesterService.update(requester);
            requesterJPA.save(requester);

            messageJPA.save(Message.createMessage(requester.getEmailAddress(), MessageType.REIMBURSE, new String[]{
                    task.getTaskName(),
                    String.format("%.2f", task.getTotalReward())
            }));
            return;
        }

        double individalReward;
        if (task.getRewardStrategy() == RewardStrategy.TOTAL) {
            individalReward = task.getTotalReward() / completedContracts.size();
        } else {
            individalReward = task.getTotalReward() / task.getCapacity();
        }


        completedContracts.forEach(c -> {
            Worker worker = userById(workerJPA, c.getWorkerId());
//                    lowerWorkerService.getById(c.getWorkerId());
            worker.setExperiencePoint(worker.getExperiencePoint() + individalReward);
            worker.setCredit(worker.getCredit() + individalReward);
//            lowerWorkerService.update(worker);
            workerJPA.save(worker);

            messageJPA.save(Message.createMessage(worker.getEmailAddress(), MessageType.GET_REWARD, new String[]{
                    task.getTaskName(),
                    String.format("%.2f", individalReward)
            }));
        });

        unfinishedContracts.forEach(c -> {
            c.setContractStatus(ContractStatus.ABORT);
            c.setLastEditTime(LocalDateTime.now());
//            lowerContractService.update(c);
            contractJPA.save(c);

            Worker worker = userById(workerJPA, c.getWorkerId());
//                    lowerWorkerService.getById(c.getWorkerId());
            messageJPA.save(Message.createMessage(worker.getEmailAddress(), MessageType.ABORT_CONTRACT, new String[]{
                    task.getTaskName(),
                    "任务结束"
            }));
        });

        messageJPA.save(Message.createMessage(requester.getEmailAddress(), MessageType.FINISH_TASK, new String[]{
                task.getTaskName(),
                ""
        }));
        if (completedContracts.size() < task.getCapacity()) {
            double delta = task.getTotalReward() - completedContracts.size() * task.getTotalReward() / task.getCapacity();
            requester.setCredit(requester.getCredit() + delta);
//            lowerRequesterService.update(requester);
            requesterJPA.save(requester);

            messageJPA.save(Message.createMessage(requester.getEmailAddress(), MessageType.REIMBURSE, new String[]{
                    task.getTaskName(),
                    String.format("%.2f", delta)
            }));
        }
    }

    @Override
    public void checkTask() {
        System.out.println("现在时间：" + dateFormat.format(new Date()));
//        lowerTaskService
//                .getLotBy(t -> {
//                    return t.getTaskStatus() == TaskStatus.ONGOING
//                            && t.getEndTime().isBefore(LocalDateTime.now());
//                })
            taskJPA.findByTaskStatusAndEndTimeBefore(TaskStatus.ONGOING, LocalDateTime.now())
                .forEach((t) -> {
                    System.out.println("finish task: id: " + t.getTaskId() + "; name: " + t.getTaskName());
                    finishTask(t);
                });
    }

    public static void main(String[] args) {
//        System.out.println();
    }
}
