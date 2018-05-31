package foursomeSE.service.task;

import foursomeSE.entity.communicate.CTask;
import foursomeSE.entity.communicate.CTaskForInspection;
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
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.inspection.InspectionContractJPA;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.util.InspectionConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static foursomeSE.service.contract.ContractUtils.contractByTaskIdAndUsername;
import static foursomeSE.service.task.TaskUtils.taskById;
import static foursomeSE.service.user.UserUtils.userById;
import static foursomeSE.service.user.UserUtils.userByUsername;
import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static foursomeSE.util.ConvenientFunctions.iterableToStream;
import static foursomeSE.util.ConvenientFunctions.listConvert;

@Service
@Qualifier("i2TaskServiceImpl")
public class UpperTaskServiceImpl implements UpperTaskService, InspectionConstants {
    private WorkerJPA workerJPA;
    private RequesterJPA requesterJPA;
    //    private LowerContractService lowerContractService;
    private ContractJPA contractJPA;
    private TaskJPA taskJPA;
    private MessageJPA messageJPA;
    private InspectionContractJPA inspectionContractJPA;

    private String username;

    public UpperTaskServiceImpl(WorkerJPA workerJPA,
                                RequesterJPA requesterJPA,
                                ContractJPA contractJPA,
                                TaskJPA taskJPA,
                                MessageJPA messageJPA,
                                InspectionContractJPA inspectionContractJPA) {
        this.workerJPA = workerJPA;
        this.requesterJPA = requesterJPA;
        this.contractJPA = contractJPA;
        this.taskJPA = taskJPA;
        this.messageJPA = messageJPA;
        this.inspectionContractJPA = inspectionContractJPA;
    }

    @Override
    public CTask getById(long id) {
        return sToD(taskById(taskJPA, id));
    }

    @Override
    public void add(CTask task, String username) {
        Requester requester = userByUsername(requesterJPA, username);
        if (requester.getCredit() - task.getTotalReward() < 0) {
            throw new MyNotValidException();
        }
        requester.setCredit(requester.getCredit() - task.getTotalReward());

        if (task.getWorkerRequirement() == WorkerRequirement.APPOINT) {
            task.setCapacity(task.getNominees().size());
            task.setRewardStrategy(RewardStrategy.INDIVIDUAL);
        }
        task.setTaskStatus(TaskStatus.ONGOING);
        task.setCreateTime(LocalDateTime.now());
        taskJPA.save(new Task(task));
        requesterJPA.save(requester);

        messageJPA.save(Message.createMessage(username, MessageType.ISSUE_TASK, new String[]{
                task.getTaskName(),
                String.format("%.2f", task.getTotalReward())
        }));
    }

    @Override
    public List<CTask> getNewTasks(String username) {
        this.username = username;
        Worker worker = userByUsername(workerJPA, username);

        // TODO
        List<Task> result = iterableToStream(taskJPA.findAll()).filter(t -> {
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
                return t.getNominees().stream().anyMatch(n -> n == worker.getId());
            }
            if (t.getWorkerRequirement() == WorkerRequirement.EXPERIENCE) {
                return t.getRequiredExperience() <= worker.getExperiencePoint();
            }
            return true;
        }).collect(Collectors.toCollection(ArrayList::new));

        result.removeAll(getWorkerTasks(username));
        return listConvert(result, this::sToD);
    }

    @Override
    public List<CTask> getWorkerTasks(String username) {
        this.username = username;

        return contractJPA.findByWorkerId(userByUsername(workerJPA, username).getId())
                .stream()
                .mapToLong(Contract::getTaskId)
                .mapToObj(this::getById)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<CTask> getRequesterTasks(String username) {
        this.username = null;

        long id = userByUsername(requesterJPA, username).getId();
        return listConvert(taskJPA.findByRequesterId(id), this::sToD);
    }

    /**
     * inspection
     */
    @Override
    public List<Task> getNewInspectionTasks(String username) {
        this.username = username;

        List<Task> result = taskJPA.findByTaskStatus(TaskStatus.UNDER_REVIEW.ordinal());
        result.removeAll(getWorkerInspectionTasks(username));

        return result;
    }

    @Override
    public List<CTaskForInspection> getWorkerInspectionTasks(String username) {
        this.username = username;

        return taskJPA.findWorkerInspectionTasks(username)
                .stream()
                .map(this::sToD2)
                .collect(Collectors.toCollection(ArrayList::new));

//        return getWorkerTasks(username).stream()
//                .filter(t -> t.getTaskStatus() == TaskStatus.UNDER_REVIEW
//                        && t.get)
//                .map(this::sToD2)
//                .collect(Collectors.toCollection(ArrayList::new));
    }


    /**
     * statistic
     */
    @Override
    public List<TaskNum> getTaskNums() {
        List<Task> ls = iterableToList(taskJPA.findAll());
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

        Map<LocalDate, List<Task>> collect = iterableToStream(taskJPA.findAll())
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


        Map<TaskCategory, List<Task>> collect = iterableToStream(taskJPA.findAll())
                .collect(Collectors.groupingBy(Task::getTaskCategory));

        collect.forEach((t, cts) -> {
            TaskStatusData toBeAdded = new TaskStatusData(t, 0, 0, 0);

            Map<TaskStatus, Long> collect1 = cts.stream().collect(Collectors.groupingBy(Task::getTaskStatus, Collectors.counting()));

            collect1.forEach((ts, l) -> {
                switch (ts) {
                    case FINISHED:
                        toBeAdded.setCompleted(toBeAdded.getCompleted() + (int) l.longValue());
                        break;
                    case ONGOING:
                        toBeAdded.setInProgress(toBeAdded.getInProgress() + (int) l.longValue());
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
        contractJPA.findAll().forEach(c -> {
            switch (taskById(taskJPA, (c.getTaskId())).getTaskCategory()) {
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
        int attendance = (int) contractJPA.countByTaskIdAndContractStatus(task.getTaskId(), ContractStatus.COMPLETED);
//                lowerContractService.getLotBy(
//                        c -> c.getTaskId() == task.getTaskId()
//                                && c.getContractStatus() == ContractStatus.COMPLETED
//                ).size();

        String requesterName = userById(requesterJPA, task.getRequesterId()).getNickname();

        if (username == null) {
            return new CTask(task, attendance, requesterName);
        }
        try {
            ContractStatus contractStatus = contractByTaskIdAndUsername(contractJPA, workerJPA, task.getTaskId(), username).getContractStatus();
//                    lowerContractService
//                    .getOneBy(c -> c.getTaskId() == task.getTaskId()
//                            && c.getWorkerId() == userByUsername(workerJPA, username).getId())
//                    .getContractStatus(); // 这里用到了getByTaskIdByUsername，用软件工程我解决不了，
            return new CTask(task, attendance, requesterName, contractStatus);
        } catch (MyObjectNotFoundException e) {
            return new CTask(task, attendance, requesterName, ContractStatus.VIRGIN);
        }
    }

    private Task dToS(CTask cTask) {
        return new Task(cTask);
    }


    private CTaskForInspection sToD2(Task task) {
        CTaskForInspection result = new CTaskForInspection(task);
        // 所有这个worker做过的关于这个task的inspection
        result.setMandatoryTime((int) inspectionContractJPA.countByWorkerUsernameAndTaskId(username, task.getTaskId()));
        return result;
    }
//    private List<Task> _getWorkerTasks(String username) {
//
//    }
}
