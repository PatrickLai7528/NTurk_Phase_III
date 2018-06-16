//package foursomeSE.util;
//
//import foursomeSE.entity.contract.Contract;
//import foursomeSE.entity.contract.ContractStatus;
//import foursomeSE.entity.task.*;
//import foursomeSE.entity.user.Requester;
//import foursomeSE.entity.user.Worker;
//import foursomeSE.jpa.task.TaskJPA;
//import foursomeSE.jpa.user.RequesterJPA;
//import foursomeSE.jpa.user.WorkerJPA;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static foursomeSE.service.user.UserUtils.userByUsername;
//
//// 一开始有三个任务，两个frame，一个general。
//// 有两个Requester，三个worker。
//
//// 第一个哥们做完了第二个requester的general任务。
//// 第二个哥们做完了第
//
///*
//* 有三个worker1,2,3，两个requester1,2
//* requester1发布了2个画框的task1 2，第一个是20人 * 10元/人，第二个是appoint 3人 * 30元/人
//* requester2发布了1个问答任务task3，要求ep >= 20，总奖励300元
//* worker1没有参加任务
//* worker2参加了3个任务，但只完成了task1和task3
//* worker3参加了task1，然而没有完成
//* */
//
//
///*
//* 这个类写越写越受限了。只能按顺序来。worker/requester, task, contract。
//* 这样的话就应该在接口上限制，比如和dataKeeper合并，或者再创一个类专门负责doBefore, doAfter
//* 不过这种软件工程的东西还是算了。
//* */
//@Service
//public class DataSupplierForT1 {
//    private WorkerJPA workerJPA;
//    private RequesterJPA requesterJPA;
//    private TaskJPA taskJPA;
//
//    public DataSupplierForT1(WorkerJPA workerJPA,
//                             RequesterJPA requesterJPA,
//                             TaskJPA taskJPA) {
//        this.workerJPA = workerJPA;
//        this.requesterJPA = requesterJPA;
//        this.taskJPA = taskJPA;
//    }
//
//    public List<Worker> mockWorkers() {
//        List<Worker> result = new ArrayList<>();
//
//        Worker w1 = new Worker(); // 没iconName
//        w1.setCreateTime(LocalDateTime.now().minusDays(100));
//        w1.setEmailAddress("worker1@ex.com");
//        w1.setIconName("hamburger1.png");
//        w1.setNickname("三刀");
//        w1.setPassword(new BCryptPasswordEncoder().encode("worker1"));
//        w1.setCredit(14);
//        w1.setExperiencePoint(101);
//        w1.setProvince("江苏");
//
//        Worker w2 = new Worker();
//        w2.setCreateTime(LocalDateTime.now().minusDays(50));
//        w2.setEmailAddress("worker2@ex.com");
//        w2.setIconName("hamburger1.png");
//        w2.setNickname("今夕何夕");
//        w2.setPassword(new BCryptPasswordEncoder().encode("worker2"));
//        w2.setCredit(0);
//        w2.setExperiencePoint(2);
//        w2.setProvince("山东");
//
//        Worker w3 = new Worker();
//        w3.setCreateTime(LocalDateTime.now().minusDays(50));
//        w3.setEmailAddress("worker3@ex.com");
//        w3.setIconName("hamburger1.png");
//        w3.setNickname("铁冰");
//        w3.setPassword(new BCryptPasswordEncoder().encode("worker3"));
//        w3.setCredit(3);
//        w3.setExperiencePoint(0);
//        w3.setProvince("山东");
//
//        result.add(w1);
//        result.add(w2);
//        result.add(w3);
//
//        return result;
//    }
//
//    public List<Requester> mockRequesters() {
//        List<Requester> result = new ArrayList<>();
//
//        Requester r1 = new Requester();
//        r1.setId(0);
//        r1.setCreateTime(LocalDateTime.now());
//        r1.setNickname("岩雪");
//        r1.setEmailAddress("requester1@ex.com");
//        r1.setIconName("hamburger1.png");
//        r1.setPassword(new BCryptPasswordEncoder().encode("requester1"));
//        r1.setCredit(10000);
//        r1.setExperiencePoint(13500);
//
//        r1.setProvince("山西");
//
//        Requester r2 = new Requester();
//        r2.setId(0);
//        r2.setCreateTime(LocalDateTime.now().minusDays(100));
//        r2.setNickname("庄康");
//        r2.setEmailAddress("requester2@ex.com");
//        r2.setIconName("hamburger1.png");
//        r2.setPassword(new BCryptPasswordEncoder().encode("requester2"));
//        r2.setCredit(789);
//        r2.setExperiencePoint(15155);
//        r2.setProvince("陕西");
//
//        result.add(r1);
//        result.add(r2);
//        return result;
//    }
//
//    public List<Task> mockTasks() {
//        List<Task> result = new ArrayList<>();
////
////        // t1
////        Task t1 = new Task();
//////        t1.setTaskId(0);
////        t1.setTaskName("task1");
////        t1.setTaskDescription("task1-巡址寻址: 请在图中框出你认为有价值的物体，并作出描述");
////        t1.setRequesterId(userByUsername(requesterJPA, "requester1@ex.com").getId());
////
////        t1.setCreateTime(LocalDateTime.now().minusDays(10));
////        t1.setEndTime(LocalDateTime.now().plusDays(10));
////        t1.setDdl(LocalDateTime.now().plusDays(8));
//////        t1.setEndTime(LocalDateTime.now().minusDays(10));
////
////        t1.setWorkerRequirement(WorkerRequirement.NONE);
////
////        t1.setTaskCategory(TaskCategory.FRAME);
////
////        t1.setRewardStrategy(RewardStrategy.INDIVIDUAL);
////        t1.setCapacity(20);
////        t1.setTotalReward(200);
////        t1.setImgNames(new ArrayList<>(Arrays.asList(
////                "COCO_train2014_000000000009.jpg",
////                "COCO_train2014_000000000025.jpg",
////                "COCO_train2014_000000000030.jpg"
////        )));
////        t1.setTaskStatus(TaskStatus.ONGOING);
////
////
////        // t2
////        Task t2 = new Task();
//////        t2.setTaskId(1);
////        t2.setTaskName("task2");
////        t2.setTaskDescription("task2-相面: 请框出你认识的名人脸");
////        t2.setRequesterId(userByUsername(requesterJPA, "requester1@ex.com").getId());
////
////        t2.setCreateTime(LocalDateTime.now().minusDays(20));
////        t2.setEndTime(LocalDateTime.now().plusDays(3));
////        t2.setDdl(LocalDateTime.now().plusDays(2));
////
////        t2.setWorkerRequirement(WorkerRequirement.APPOINT);
////        // 呃
////        t2.setNominees(new ArrayList<>(Arrays.asList(
////                userByUsername(workerJPA, "worker1@ex.com").getId(),
////                userByUsername(workerJPA, "worker3@ex.com").getId()
////        )));
////
////        t2.setTaskCategory(TaskCategory.FRAME);
////
////        t2.setRewardStrategy(RewardStrategy.INDIVIDUAL);
////        t2.setTotalReward(90);
////
////        t2.setImgNames(new ArrayList<>(Arrays.asList(
////                "COCO_train2014_000000000034.jpg",
////                "COCO_train2014_000000000036.jpg",
////                "COCO_train2014_000000000049.jpg"
////        )));
////        t2.setTaskStatus(TaskStatus.ONGOING);
////
////
////        // t3
////        Task t3 = new Task();
//////        t3.setTaskId(2);
////        t3.setTaskName("task3");
////        t3.setTaskDescription("task3-伤痕累累: 请根据图中隐秘的情感，回答下列隐秘的问题");
////        t3.setRequesterId(userByUsername(requesterJPA, "requester2@ex.com").getId());
////
////        t3.setCreateTime(LocalDateTime.now().minusDays(10).minusMinutes(3));
////        t3.setEndTime(LocalDateTime.now().plusDays(8));
////        t3.setDdl(LocalDateTime.now().plusDays(4));
////
////        t3.setWorkerRequirement(WorkerRequirement.EXPERIENCE);
////        t3.setRequiredExperience(20);
////
////        t3.setTaskCategory(TaskCategory.GENERAL);
////        t3.setQuestions(new ArrayList<>(Arrays.asList("红袖添香夜读书", "那时年少春衫冷", "梦里不知身是客", "笑渐不闻声渐悄")));
////
////        t3.setRewardStrategy(RewardStrategy.TOTAL);
////        t3.setTotalReward(300);
////
////        t3.setImgNames(new ArrayList<>(Arrays.asList(
////                "COCO_train2014_000000000061.jpg",
////                "COCO_train2014_000000000064.jpg",
////                "COCO_train2014_000000000071.jpg"
////        )));
////        t3.setTaskStatus(TaskStatus.ONGOING);
////
////        result.add(t1);
////        result.add(t2);
////        result.add(t3);
//        return result;
//    }
//
//    public List<Contract> mockContract() {
//        List<Contract> result = new ArrayList<>();
//        Contract c1 = new Contract();
//        c1.setContractId(0);
////        c1.setTaskId(2);
//        c1.setTaskId(taskJPA.findByTaskName("task3").getTaskId());
////        c1.setWorkerId(1);
//        c1.setWorkerId(userByUsername(workerJPA, "worker2@ex.com").getId());
////        c1.setContractStatus(ContractStatus.COMPLETED);
//        c1.setLastEditTime(LocalDateTime.now().minusDays(3));
//
//        Contract c2 = new Contract();
//        c2.setContractId(1);
////        c2.setTaskId(0);
//        c2.setTaskId(taskJPA.findByTaskName("task1").getTaskId());
////        c2.setWorkerId(1);
//        c2.setWorkerId(userByUsername(workerJPA, "worker2@ex.com").getId());
////        c2.setContractStatus(ContractStatus.COMPLETED);
//        c2.setLastEditTime(LocalDateTime.now());
//
//        Contract c3 = new Contract();
//        c3.setContractId(2);
////        c3.setTaskId(1);
//        c3.setTaskId(taskJPA.findByTaskName("task2").getTaskId());
////        c3.setWorkerId(1);
//        c3.setWorkerId(userByUsername(workerJPA, "worker2@ex.com").getId());
////        c3.setContractStatus(ContractStatus.IN_PROGRESS);
//        c3.setLastEditTime(LocalDateTime.now());
//
//        Contract c4 = new Contract();
//        c4.setContractId(3);
////        c4.setTaskId(0);
//        c4.setTaskId(taskJPA.findByTaskName("task1").getTaskId());
////        c4.setWorkerId(2);
//        c4.setWorkerId(userByUsername(workerJPA, "worker3@ex.com").getId());
////        c4.setContractStatus(ContractStatus.IN_PROGRESS);
//        c4.setLastEditTime(LocalDateTime.now());
//
//        result.add(c1);
//        result.add(c2);
//        result.add(c3);
//        result.add(c4);
//        return result;
//    }
//
//    // annotation都不重要，所以就不mock了，这在现实中是不可能的
////    public static List<FrameAnnotation> mockFrameAnnotation() {
////        List<FrameAnnotation> result = new ArrayList<>();
////        FrameAnnotation f1 = new FrameAnnotation();
////        f1.setAnnotationId(0);
////        f1.setContractId(0);
////        // setImgName
////        // setFrames
////
////        FrameAnnotation f2 = new FrameAnnotation();
////        f2.setAnnotationId(1);
////        f2.setContractId(2);
////
////
////        return result;
////    }
//}
