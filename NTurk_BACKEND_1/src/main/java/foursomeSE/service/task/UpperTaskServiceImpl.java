package foursomeSE.service.task;

import foursomeSE.entity.communicate.report.Reports;
import foursomeSE.entity.task.CTask;
import foursomeSE.entity.communicate.CTaskForInspection;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.statistics.TaskGrowth;
import foursomeSE.entity.statistics.TaskNum;
import foursomeSE.entity.statistics.TaskParticipation;
import foursomeSE.entity.statistics.TaskStatusData;
import foursomeSE.entity.task.*;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static foursomeSE.service.task.TaskUtils.taskById;
import static foursomeSE.service.user.UserUtils.userById;
import static foursomeSE.service.user.UserUtils.userByUsername;
import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static foursomeSE.util.ConvenientFunctions.iterableToStream;
import static foursomeSE.util.ConvenientFunctions.listConvert;

@Service
@Qualifier("i2TaskServiceImpl")
public class UpperTaskServiceImpl implements UpperTaskService, MyConstants {
    private WorkerJPA workerJPA;
    private RequesterJPA requesterJPA;
    //    private LowerContractService lowerContractService;
    private ContractJPA contractJPA;
    private TaskJPA taskJPA;
    private MessageJPA messageJPA;

    private MicrotaskJPA microtaskJPA;

    private String username;

    public UpperTaskServiceImpl(WorkerJPA workerJPA,
                                RequesterJPA requesterJPA,
                                ContractJPA contractJPA,
                                TaskJPA taskJPA,
                                MessageJPA messageJPA,
                                MicrotaskJPA microtaskJPA) {
        this.workerJPA = workerJPA;
        this.requesterJPA = requesterJPA;
        this.contractJPA = contractJPA;
        this.taskJPA = taskJPA;
        this.messageJPA = messageJPA;
        this.microtaskJPA = microtaskJPA;
    }

    @Override
    public CTask getById(long id) {
        return sToD(taskById(taskJPA, id));
    }

    @Override
    public void add(Task task, String username) {
        Requester requester = userByUsername(requesterJPA, username);
//        double totalCost = task.getRewardPerMicrotask() * task.getImgNames().size();
//        if (requester.getCredit() - totalCost < 0) {
//            throw new MyNotValidException();
//        }
//        requester.setCredit(requester.getCredit() - totalCost);


        task.setRequesterId(requester.getId());
        task.setCreateTime(LocalDateTime.now());
        task.setTaskStatus(TaskStatus.ONGOING);
        task.setIsCollecting(1);
        taskJPA.save(new Task(task));

        long temp = taskJPA.temp().get(0).longValue();
        Task savedTask = taskById(taskJPA, temp - 1);
        for (int i = 0; i < task.getImgNames().size(); i++) {
            String s = task.getImgNames().get(i);
            Microtask m = new Microtask();
            m.setTaskId(savedTask.getTaskId());
            m.setImgName(s);
            m.setMicrotaskStatus(MicrotaskStatus.YET_TO_DRAW);
            m.setOrd(i);
            m.setParallel(0);
            m.setIteration(0);
            m.setIsSample(i < SAMPLE_SIZE ? 1 : 0);

            microtaskJPA.save(m);

        }

//        requesterJPA.save(requester);

//        messageJPA.save(Message.createMessage(username, MessageType.ISSUE_TASK, new String[]{
//                task.getTaskName(),
//                String.format("%.2f", task.getTotalReward())
//        }));
    }

    @Override
    public List<CTask> getNewTasks(String username) {
        this.username = username;
        Worker worker = userByUsername(workerJPA, username);

        // TODO 不想用findAll
        List<Task> result = iterableToStream(taskJPA.findAll()).filter(t -> {
//            if (t.getEndTime().isBefore(LocalDateTime.now())) {
//                return false;
//            }
//
//            int haveFinished = lowerContractService.getLotBy(c -> c.getTaskId() == t.getTaskId() && c.getContractStatus() == ContractStatus.COMPLETED).size();
//            if (t.getCapacity() <= haveFinished) {
//                return false;
//            }
            if (t.getTaskStatus() != TaskStatus.ONGOING) {
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
                .mapToObj(id -> sToD(taskById(taskJPA, id)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<CTask> getRequesterTasks(String username) {
        this.username = null;

        long id = userByUsername(requesterJPA, username).getId();
        return listConvert(taskJPA.findByRequesterId(id), this::sToD);
    }


    @Override
    public EnterResponse enterTask(long taskId, String username) {
        List<Microtask> results = microtaskJPA.getMicroTasks(taskId);
        if (results.size() > NUM_OF_MICROTASK_PER_REQUEST) {
            results = results.subList(0, NUM_OF_MICROTASK_PER_REQUEST);
        }

        results.forEach(r -> {
            r.setLastRequestTime(LocalDateTime.now());
            r.setParallel(1); // 反正得到的一定是0的
            microtaskJPA.save(r);

            // 下面的不需要，因为反正parallel只能是0或者1，parallel就足够取代原来unfinished的状态
            // 本来加入下面的就是为了并行。但是实际上没有并行
            // 但是但是因为太懒（还要用另一种模式来写，比如之前的sql语句）
            // 还是这样写了
            CriticalSection.Item item = new CriticalSection.Item();
            item.username = username;
            item.requestTime = LocalDateTime.now();
            item.microtaskId = r.getMicrotaskId();
            CriticalSection.drawRecords.add(item);
        });

        EnterResponse result = new EnterResponse();
        result.setImgNames(results.stream().map(Microtask::getImgName).collect(Collectors.toCollection(ArrayList::new)));
        return result;
    }

//    /**
//     * inspection
//     */
//    @Override
//    public List<CTask> getNewInspectionTasks(String username) {
//        this.username = username;
//
//        List<Task> result = taskJPA.findByTaskStatus(TaskStatus.UNDER_REVIEW.ordinal());
//        result.removeAll(getWorkerInspectionTasks(username));
//
//        return listConvert(result, this::sToD);
//    }
//
//    @Override
//    public List<CTaskForInspection> getWorkerInspectionTasks(String username) {
//        this.username = username;
//
//        return taskJPA.findWorkerInspectionTasks(username)
//                .stream()
//                .map(this::sToD2)
//                .collect(Collectors.toCollection(ArrayList::new));
//
////        return getWorkerTasks(username).stream()
////                .filter(t -> t.getTaskStatus() == TaskStatus.UNDER_REVIEW
////                        && t.get)
////                .map(this::sToD2)
////                .collect(Collectors.toCollection(ArrayList::new));
//    }


    @Override
    public Reports getJson(long taskId, String username) {
        List<String> imgs = microtaskJPA.retrieveImgNames(taskId);
        return null;
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
//        int attendance = (int) contractJPA.countByTaskIdAndContractStatus(task.getTaskId(), ContractStatus.COMPLETED);
//                lowerContractService.getLotBy(
//                        c -> c.getTaskId() == task.getTaskId()
//                                && c.getContractStatus() == ContractStatus.COMPLETED
//                ).size();

        String requesterName = userById(requesterJPA, task.getRequesterId()).getNickname();
        CTask result = new CTask(task);
        result.setRequesterName(requesterName);

        if (username == null) {
            List<String> imgs = microtaskJPA.retrieveImgNames(task.getTaskId());
            result.setImgNames(new ArrayList<>(imgs));
        }

        if (username != null) {
            result.setDraw(microtaskJPA.getMicroTasks(task.getTaskId()).size());
            int yetToVerifyQuality = microtaskJPA.getForVerification(task.getTaskId(), username, MicrotaskStatus.YET_TO_VERIFY_QUALITY.ordinal()).size();
            int yetToVerifyCoverage = microtaskJPA.getForVerification(task.getTaskId(), username, MicrotaskStatus.YET_TO_VERIFY_COVERAGE.ordinal()).size();
            result.setVerifyQuality(yetToVerifyQuality);
            result.setVerifyCoverage(yetToVerifyCoverage);
        }

        return result;
//        if (username == null) {
//            return new CTask(task, attendance, requesterName);
//        }
//        try {
//            ContractStatus contractStatus = contractByTaskIdAndUsername(contractJPA, workerJPA, task.getTaskId(), username).getContractStatus();
////                    lowerContractService
////                    .getOneBy(c -> c.getTaskId() == task.getTaskId()
////                            && c.getWorkerId() == userByUsername(workerJPA, username).getId())
////                    .getContractStatus(); // 这里用到了getByTaskIdByUsername，用软件工程我解决不了，
//            return new CTask(task, attendance, requesterName, contractStatus);
//        } catch (MyObjectNotFoundException e) {
//            return new CTask(task, attendance, requesterName, ContractStatus.VIRGIN);
//        }
    }

    private Task dToS(CTask cTask) {
        return new Task(cTask);
    }


//    private CTaskForInspection sToD2(Task task) {
//        CTaskForInspection result = new CTaskForInspection(sToD(task));
//        // 所有这个worker做过的关于这个task的inspection
//        int alreadyDone = (int) inspectionContractJPA.countByWorkerUsernameAndTaskId(username, task.getTaskId());
//        if (alreadyDone >= MANDATORY_TIME) {
//            result.setMandatoryTime(0);
//        } else { // 还要要求mandatoryTime不能大于目前他还可以做的inpection数量。
//            int maxCanDo = contractJPA.findByTaskIdForInspection(task.getTaskId(), userByUsername(workerJPA, username).getId()).size();
//            result.setMandatoryTime(Math.min(MANDATORY_TIME - alreadyDone, maxCanDo));
//        }
//
//        return result;
//    }
////    private List<Task> _getWorkerTasks(String username) {
//
//    }
}
