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
import foursomeSE.service.contract.LowerContractService;
import foursomeSE.service.message.LowerMessageService;
import foursomeSE.service.user.lower.LowerUserService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FinishTaskServiceImpl implements FinishTaskService {
    private LowerUserService<Worker> lowerWorkerService;
    private LowerTaskService lowerTaskService;
    private LowerUserService<Requester> lowerRequesterService;
    private LowerContractService lowerContractService;
    private LowerMessageService lowerMessageService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public FinishTaskServiceImpl(LowerUserService<Worker> lowerWorkerService,
                                 LowerTaskService lowerTaskService,
                                 LowerContractService lowerContractService,
                                 LowerUserService<Requester> lowerReqeusterService,
                                 LowerMessageService lowerMessageService) {
        this.lowerWorkerService = lowerWorkerService;
        this.lowerTaskService = lowerTaskService;
        this.lowerContractService = lowerContractService;
        this.lowerRequesterService = lowerReqeusterService;
        this.lowerMessageService = lowerMessageService;
    }

    @Override
    public void finishTask(Task task) {
        task.setTaskStatus(TaskStatus.FINISHED);
        lowerTaskService.update(task);

        Requester requester = lowerRequesterService.getById(task.getRequesterId());
        List<Contract> completedContracts = lowerContractService
                .getLotBy(c -> c.getTaskId() == task.getTaskId()
                        && c.getContractStatus() == ContractStatus.COMPLETED);
        List<Contract> unfinishedContracts = lowerContractService
                .getLotBy(c -> c.getTaskId() == task.getTaskId()
                        && c.getContractStatus() == ContractStatus.IN_PROGRESS);

        if (completedContracts.size() == 0 && unfinishedContracts.size() == 0) {
            lowerMessageService.add(Message.createMessage(requester.getEmailAddress(), MessageType.FINISH_TASK, new String[]{
                    task.getTaskName(),
                    "无人参与"
            }));

            requester.setCredit(requester.getCredit() + task.getTotalReward());
            lowerRequesterService.update(requester);

            lowerMessageService.add(Message.createMessage(requester.getEmailAddress(), MessageType.REIMBURSE, new String[]{
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
            Worker worker = lowerWorkerService.getById(c.getWorkerId());
            worker.setExperiencePoint(worker.getExperiencePoint() + individalReward);
            worker.setCredit(worker.getCredit() + individalReward);
            lowerWorkerService.update(worker);

            lowerMessageService.add(Message.createMessage(worker.getEmailAddress(), MessageType.GET_REWARD, new String[]{
                    task.getTaskName(),
                    String.format("%.2f", individalReward)
            }));
        });

        unfinishedContracts.forEach(c -> {
            c.setContractStatus(ContractStatus.ABORT);
            c.setLastEditTime(LocalDateTime.now());
            lowerContractService.update(c);

            Worker worker = lowerWorkerService.getById(c.getWorkerId());
            lowerMessageService.add(Message.createMessage(worker.getEmailAddress(), MessageType.ABORT_CONTRACT, new String[]{
                    task.getTaskName(),
                    "任务结束"
            }));
        });

        lowerMessageService.add(Message.createMessage(requester.getEmailAddress(), MessageType.FINISH_TASK, new String[]{
                task.getTaskName(),
                ""
        }));
        if (completedContracts.size() < task.getCapacity()) {
            double delta = task.getTotalReward() - completedContracts.size() * task.getTotalReward() / task.getCapacity();
            requester.setCredit(requester.getCredit() + delta);
            lowerRequesterService.update(requester);

            lowerMessageService.add(Message.createMessage(requester.getEmailAddress(), MessageType.REIMBURSE, new String[]{
                    task.getTaskName(),
                    String.format("%.2f", delta)
            }));
        }
    }

    @Override
    public void checkTask() {
        System.out.println("现在时间：" + dateFormat.format(new Date()));
        lowerTaskService
                .getLotBy(t -> {
//                    System.out.println("searchTasks");
                    return t.getTaskStatus() == TaskStatus.ONGOING
                            && t.getEndTime().isBefore(LocalDateTime.now());
                })
                .forEach((t) -> {
                    System.out.println("finish task: id: " + t.getTaskId() + "; name: " + t.getTaskName());
                    finishTask(t);
                });
    }

    public static void main(String[] args) {
//        System.out.println();
    }
}
