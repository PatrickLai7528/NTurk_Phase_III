package foursomeSE.service.task;

import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.communicate.report.Reports;
import foursomeSE.entity.statistics.*;
import foursomeSE.entity.task.CTask;
import foursomeSE.entity.communicate.CTaskForInspection;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.*;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.entity.verification.Verification;
import foursomeSE.entity.verification.VerificationType;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.annotation.GeneralAnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.jpa.verification.VerificationJPA;
import foursomeSE.service.contract.LowerContractService;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static foursomeSE.service.task.TaskUtils.mtByImg;
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
    private AnnotationJPA annotationJPA;
    private GeneralAnnotationJPA generalAnnotationJPA;
    private VerificationJPA verificationJPA;

    private MicrotaskJPA microtaskJPA;
    private LowerContractService lowerContractService;

    private String username;

    public UpperTaskServiceImpl(WorkerJPA workerJPA, RequesterJPA requesterJPA, ContractJPA contractJPA, TaskJPA taskJPA, AnnotationJPA annotationJPA, GeneralAnnotationJPA generalAnnotationJPA, VerificationJPA verificationJPA, MicrotaskJPA microtaskJPA, LowerContractService lowerContractService) {
        this.workerJPA = workerJPA;
        this.requesterJPA = requesterJPA;
        this.contractJPA = contractJPA;
        this.taskJPA = taskJPA;
        this.annotationJPA = annotationJPA;
        this.generalAnnotationJPA = generalAnnotationJPA;
        this.verificationJPA = verificationJPA;
        this.microtaskJPA = microtaskJPA;
        this.lowerContractService = lowerContractService;
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
        EnterResponse result = new EnterResponse();

        List<Microtask> results = microtaskJPA.getMicroTasks(taskId);
        if (results.isEmpty()) {
            result.setImgNames(new ArrayList<>());
            return result;
        }

        lowerContractService.recordEnterTask(taskId, username);

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


        result.setImgNames(results.stream().map(Microtask::getImgName).collect(Collectors.toCollection(ArrayList::new)));
        return result;
    }


    @Override
    public List<PHItem> PHChart(long taskId, String username) {
        List<PHItem> result = new ArrayList<>();
        Task task = taskById(taskJPA, taskId);


        for (LocalDate date = task.getCreateTime().toLocalDate();
             date.isBefore(LocalDate.now().plusDays(1));
             date = date.plusDays(1)) {

            PHItem phItem = new PHItem();
            phItem.date = date;


            for (Microtask mt : microtaskJPA.findByTaskId(taskId)) {
                Object[] oos = annotationJPA
                        .findLatestBefore(mt.getMicrotaskId(), LocalDateTime.of(date, LocalTime.MIN));

                if (oos.length == 0) {
                    phItem.ongoing++;
                } else {
                    Object[] oo = (Object[]) oos[0];
                    BigInteger aidB = (BigInteger) oo[0];
                    long aid = aidB.longValue();
                    int aStt = Integer.parseInt(oo[1].toString());

                    if (aStt == 0) {
                        phItem.underReview++;
                    } else if (aStt == 2) {
                        phItem.ongoing++;
                    } else  // 接下来就一定是passed的了
                        if (generalAnnotationJPA.findById(aid).isPresent()) {
                            phItem.finished++;
                        } else {
                            List<Verification> cvs = verificationJPA
                                    .findByAnnotationIdAndVerificationType(
                                            aid, VerificationType.COVERAGE);

                            if (cvs.isEmpty()) {
                                phItem.underReview++;
                            } else {
                                Verification cv1 = cvs.get(0);
                                if (cv1.getIsMajorityVoting() == 0) {
                                    if (cv1.getRate() == 1) {
                                        phItem.finished++;
                                    } else {
                                        phItem.underReview++;
                                    }
                                } else {
                                    int sum = cvs.stream().mapToInt(Verification::getRate).sum();
                                    if (cvs.size() != 3) {
                                        phItem.underReview++;
                                    } else if (sum >= 2) {
                                        phItem.finished++;
                                    } else {
                                        phItem.underReview++;
                                    }
                                }
                            }
                        }
                }
            }

            result.add(phItem);
        }

        return result;
    }

    @Override
    public List<CommitItem> commitChart(long taskId, String username) {
        List<CommitItem> result = new ArrayList<>();
        Task task = taskById(taskJPA, taskId);

        for (LocalDate date = task.getCreateTime().toLocalDate();
             date.isBefore(LocalDate.now().plusDays(1));
             date = date.plusDays(1)) {

            CommitItem ci = new CommitItem();
            ci.date = date;
            ci.DCommit = annotationJPA.findAidsBetween(
                    taskId,
                    LocalDateTime.of(date.minusDays(1), LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MIN)
            ).size();
            ci.QVCommit = verificationJPA.findBetween(
                    taskId,
                    VerificationType.QUALITY.ordinal(),
                    LocalDateTime.of(date.minusDays(1), LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MIN)
            ).size();
            ci.CVCommit = verificationJPA.findBetween(
                    taskId,
                    VerificationType.COVERAGE.ordinal(),
                    LocalDateTime.of(date.minusDays(1), LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MIN)
            ).size();


            result.add(ci);
        }

        return result;
    }

    @Override
    public Accuracy accuraccyChart(String username) {
        Accuracy result = new Accuracy();

        long countPass = annotationJPA.countPass();
        long countFail = annotationJPA.countFail();

        if (countPass + countFail == 0) {
            result.average = 0;
        } else {
            result.average = (countPass / (countPass + countFail));
        }


        Worker worker = userByUsername(workerJPA, username);
        double currentAccuracy = 0;
        for (LocalDate date = worker.getCreateTime().toLocalDate();
             date.isBefore(LocalDate.now().plusDays(1));
             date = date.plusDays(1)) {

            AccuracyItem ai = new AccuracyItem();
            ai.date = date;

            long countUserPass = annotationJPA.countUserPassBetween(
                    username,
                    LocalDateTime.of(date.minusDays(1), LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MIN)
            );

            long countUserFail = annotationJPA.countUserFailBetween(
                    username,
                    LocalDateTime.of(date.minusDays(1), LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MIN)
            );

            if (countUserFail + countUserPass == 0) {
                ai.point = currentAccuracy;
            } else {
                ai.point = currentAccuracy
                        = (countUserPass / (countUserPass + countUserFail));
            }

            result.items.add(ai);
        }

        return result;
    }

    @Override
    public List<Heat> heatChart(String username) {
        List<Heat> result = new ArrayList<>();

        Worker worker = userByUsername(workerJPA, username);
        for (LocalDate date = worker.getCreateTime().toLocalDate();
             date.isBefore(LocalDate.now().plusDays(1));
             date = date.plusDays(1)) {

            Heat heat = new Heat();
            heat.date = date;

            long ac = annotationJPA.countUserDidBetween(
                    username,
                    LocalDateTime.of(date.minusDays(1), LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MIN)
            );

            long vc = verificationJPA.countUserDidBetween(
                    username,
                    LocalDateTime.of(date.minusDays(1), LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MIN)
            );
            heat.activity = (int)(ac + vc);

            result.add(heat);
        }

        return result;
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
}
