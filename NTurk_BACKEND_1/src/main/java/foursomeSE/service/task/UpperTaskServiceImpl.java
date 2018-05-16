package foursomeSE.service.task;

import foursomeSE.entity.communicate.CTask;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.contract.ContractStatus;
import foursomeSE.entity.message.Message;
import foursomeSE.entity.message.MessageType;
import foursomeSE.entity.statistics.TaskGrowth;
import foursomeSE.entity.statistics.TaskNum;
import foursomeSE.entity.statistics.TaskParticipation;
import foursomeSE.entity.statistics.TaskStatusData;
import foursomeSE.entity.task.*;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyNotValidException;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.service.contract.LowerContractService;
import foursomeSE.service.message.LowerMessageService;
import foursomeSE.service.user.lower.LowerUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static foursomeSE.util.ConvenientFunctions.listConvert;

@Service
@Qualifier("i2TaskServiceImpl")
public class UpperTaskServiceImpl implements UpperTaskService {
    private LowerUserService<Worker> workerService;
    private LowerUserService<Requester> requesterService;
    private LowerContractService lowerContractService;
    private LowerTaskService service;
    private LowerMessageService lowerMessageService;

    private String username;

    public UpperTaskServiceImpl(LowerUserService<Worker> workerService,
                                LowerUserService<Requester> requesterService,
                                LowerContractService lowerContractService,
                                LowerTaskService service,
                                LowerMessageService lowerMessageService) {
        this.workerService = workerService;
        this.requesterService = requesterService;
        this.lowerContractService = lowerContractService;
        this.service = service;
        this.lowerMessageService = lowerMessageService;
    }

    @Override
    public CTask getById(long id) {
        return sToD(service.getById(id));
    }

    @Override
    public void add(CTask task, String username) {
        Requester requester = requesterService.getById(task.getRequesterId());
        if (requester.getCredit() - task.getTotalReward() < 0) {
            throw new MyNotValidException();
        }
        requester.setCredit(requester.getCredit() - task.getTotalReward());
        requesterService.update(requester);

        if (task.getWorkerRequirement() == WorkerRequirement.APPOINT) {
            task.setCapacity(task.getNominees().length);
            task.setRewardStrategy(RewardStrategy.INDIVIDUAL);
        }
        task.setTaskStatus(TaskStatus.ONGOING);
        task.setCreateTime(LocalDateTime.now());
        service.add(new Task(task));

        lowerMessageService.add(Message.createMessage(username, MessageType.ISSUE_TASK, new String[]{
                task.getTaskName(),
                String.format("%.2f", task.getTotalReward())
        }));
    }

    @Override
    public List<CTask> getNewTasks(String username) {
        this.username = username;
        long userId = workerService.usernameToId(username);
        Worker worker = workerService.getById(userId);

        List<Task> result = service.getLotBy(t -> {
//            if (t.getEndTime().isBefore(LocalDateTime.now())) {
//                return false;
//            }
//
//            int haveFinished = lowerContractService.getLotBy(c -> c.getTaskId() == t.getTaskId() && c.getContractStatus() == ContractStatus.COMPLETED).size();
//            if (t.getCapacity() <= haveFinished) {
//                return false;
//            }
            if (t.getTaskStatus() == TaskStatus.FINISHED) {
                return false;
            }

            if (t.getWorkerRequirement() == WorkerRequirement.APPOINT) {
                return Arrays.stream(t.getNominees()).anyMatch(n -> n == userId);
            }
            if (t.getWorkerRequirement() == WorkerRequirement.EXPERIENCE) {
                return t.getRequiredExperience() <= worker.getExperiencePoint();
            }
            return true;
        });
        result.removeAll(getWorkerTasks(username));
        return listConvert(result, this::sToD);
    }

    @Override
    public List<CTask> getWorkerTasks(String username) {
        this.username = username;

        return lowerContractService
                .getLotBy(c -> c.getWorkerId() == workerService.usernameToId(username))
                .stream()
                .mapToLong(Contract::getTaskId)
                .mapToObj(this::getById)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<CTask> getRequesterTasks(String username) {
        this.username = null;
        return listConvert(service.getLotBy(t -> t.getRequesterId() == requesterService.usernameToId(username)),
                this::sToD);
    }

    /**
     * statistic
     */
    @Override
    public List<TaskNum> getTaskNums() {
        List<Task> ls = service.getLotBy(p -> true);
        // 算了，这里不用stream的groupingBy了，本来也没什么好用stream的，
        // 不过这里是因为task的分类知道，不然还是groupingBy成了map以后再自己操作吧
        TaskNum tnf = new TaskNum(TaskCategory.FRAME, 0);
        TaskNum tng = new TaskNum(TaskCategory.GENERAL, 0);
        TaskNum tns = new TaskNum(TaskCategory.SEGMENT, 0);

        ls.forEach(t -> {
            switch (t.getTaskCategory()) {
                case FRAME:
                    tnf.setValue(tnf.getValue() + 1);
                    break;
                case GENERAL:
                    tng.setValue(tng.getValue() + 1);
                    break;
                case SEGMENT:
                    tns.setValue(tns.getValue() + 1);
                    break;
            }
        });

        return Arrays.asList(tnf, tng, tns);
    }

    @Override
    public List<TaskGrowth> getTaskGrowth() {
        List<TaskGrowth> result = new ArrayList<>();

        Map<LocalDate, List<Task>> collect = service.getLotBy(p -> true).stream()
                .collect(Collectors.groupingBy(t -> t.getCreateTime().toLocalDate()));
        collect.forEach((d, cts) -> {
            TaskGrowth toBeAdded = new TaskGrowth(d, 0, 0, 0);

            cts.forEach(t -> {
                switch (t.getTaskCategory()) {
                    case FRAME:
                        toBeAdded.setFrame(toBeAdded.getFrame() + 1);
                        break;
                    case GENERAL:
                        toBeAdded.setGeneral(toBeAdded.getGeneral() + 1);
                        break;
                    case SEGMENT:
                        toBeAdded.setSegment(toBeAdded.getSegment() + 1);
                        break;
                }
            });

            result.add(toBeAdded);
        });
        return result;
    }

    @Override
    public List<TaskStatusData> getTaskStatus() {
        List<TaskStatusData> result = new ArrayList<>();


        Map<TaskCategory, List<Task>> collect = service.getLotBy(p -> true).stream()
                .collect(Collectors.groupingBy(Task::getTaskCategory));

        collect.forEach((t, cts) -> {
            TaskStatusData toBeAdded = new TaskStatusData(t, 0, 0, 0);

            Map<TaskStatus, Long> collect1 = cts.stream().collect(Collectors.groupingBy(Task::getTaskStatus, Collectors.counting()));

            collect1.forEach((ts, l) -> {
                switch (ts){
                    case FINISHED:
                        toBeAdded.setCompleted(toBeAdded.getCompleted() + (int)l.longValue());
                        break;
                    case ONGOING:
                        toBeAdded.setInProgress(toBeAdded.getInProgress() + (int)l.longValue());
                        break;
                }
            });

            result.add(toBeAdded);
        });

        // 这个是理解错了，这个是task的status，不是contract的
//        collect.forEach((t, cts) -> {
//            TaskStatusData toBeAdded = new TaskStatusData(t, 0, 0, 0);
//            cts.stream()
//                    .flatMap(ct -> lowerContractService.getLotBy(c -> c.getTaskId() == ct.getTaskId()).stream())
//                    .forEach(c -> {
//                        switch (c.getContractStatus()) {
//                            case COMPLETED:
//                                toBeAdded.setCompleted(toBeAdded.getCompleted() + 1);
//                                break;
//                            case IN_PROGRESS:
//                                toBeAdded.setInProgress(toBeAdded.getInProgress() + 1);
//                                break;
//                            case ABORT:
//                                toBeAdded.setAbort(toBeAdded.getAbort() + 1);
//                                break;
//                        }
//                    });
//            result.add(toBeAdded);
//
//        });
        // 也可以放到上面if size == 0
        if (result.stream().noneMatch(ts -> ts.getTaskCategory() == TaskCategory.SEGMENT)) {
            result.add(new TaskStatusData(TaskCategory.SEGMENT, 0, 0, 0));
        }
        if (result.stream().noneMatch(ts -> ts.getTaskCategory() == TaskCategory.GENERAL)) {
            result.add(new TaskStatusData(TaskCategory.GENERAL, 0, 0, 0));
        }
        if (result.stream().noneMatch(ts -> ts.getTaskCategory() == TaskCategory.FRAME)) {
            result.add(new TaskStatusData(TaskCategory.FRAME, 0, 0, 0));
        }
        return result;
    }

    @Override
    public List<TaskParticipation> getTaskParticipation() {
        TaskParticipation tnf = new TaskParticipation(TaskCategory.FRAME, 0);
        TaskParticipation tng = new TaskParticipation(TaskCategory.GENERAL, 0);
        TaskParticipation tns = new TaskParticipation(TaskCategory.SEGMENT, 0);


        // 不要用task来找，真接从contract里找
        lowerContractService.getLotBy(p -> true).forEach(c -> {
            switch (service.getById(c.getTaskId()).getTaskCategory()) {
                case FRAME:
                    tnf.setValue(tnf.getValue() + 1);
                    break;
                case GENERAL:
                    tng.setValue(tng.getValue() + 1);
                    break;
                case SEGMENT:
                    tns.setValue(tns.getValue() + 1);
                    break;
            }
        });

        return Arrays.asList(tnf, tng, tns);
    }

    /**
     * private
     */
    private CTask sToD(Task task) {
        int attendance = lowerContractService.getLotBy(
                c -> c.getTaskId() == task.getTaskId()
                        && c.getContractStatus() == ContractStatus.COMPLETED
        ).size();

        String requesterName = requesterService.getById(task.getRequesterId()).getNickname();

        if (username == null) {
            return new CTask(task, attendance, requesterName);
        }
        try {
            ContractStatus contractStatus = lowerContractService
                    .getOneBy(c -> c.getTaskId() == task.getTaskId()
                            && c.getWorkerId() == workerService.usernameToId(username))
                    .getContractStatus(); // TODO 这里用到了getByTaskIdByUsername，用软件工程我解决不了，
            return new CTask(task, attendance, requesterName, contractStatus);
        } catch (MyObjectNotFoundException e) {
            return new CTask(task, attendance, requesterName, ContractStatus.VIRGIN);
        }
    }

    private Task dToS(CTask cTask) {
        return new Task(cTask);
    }
}
