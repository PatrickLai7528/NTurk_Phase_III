//package foursomeSE.util;
//
//import foursomeSE.entity.annotation.FrameAnnotation;
//import foursomeSE.entity.contract.Contract;
//import foursomeSE.entity.contract.ContractStatus;
//import foursomeSE.entity.task.*;
//import foursomeSE.entity.user.Requester;
//import foursomeSE.entity.user.Worker;
//import foursomeSE.jpa.contract.ContractJPA;
//import foursomeSE.jpa.task.TaskJPA;
//import foursomeSE.jpa.user.RequesterJPA;
//import foursomeSE.jpa.user.WorkerJPA;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//// draft
//// annotation_id 5035， => 5028 => 5011 是worker3的
//// 对5035的两个inspection对应inspection_contract_id是5056, 5044
//// 对应的人是5013, 5009，5013是worker5（问题就在这），
//import static foursomeSE.service.user.UserUtils.userByUsername;
//
///** taskid 7623 workerid 7610
// * 有些假数据写到UnitTest2这个类里了
// * <p>
// * 有task1一个任务（其中包括三个小任务），12个workers
// * 有task2 under review，task3 ongoing，这俩打酱油的。
// * 然后worker2345是做了task1的，各有contract和3个frame annotation，56是abort，1是in_progress
// * <p>
// * worker1打了worker3(1, 2, 5)，worker4(5, 2, 4)。 // 这些都用inspectionService里的add
// * <p>
// * worker2拿GetNewInspectionTask是只有task2，worker1有task1, task2。// 相当于测了getNewInspectionTasks和getWorkerInspectionTasks
// * <p>
// * <p> // 测GetByTaskIdForInspection
// * worker2开始打，这时应该拿到的是worker5的contract。
// * 之后再让worker5对worker2打(3, 5, 3), worker3(2, 3, 1), worker2对worker4打(5, 2, 3)。
// * <p>
// * <p> // 测getBestKth
// * 这时requester如果要看task1的第一个小任务，那么得到的BestKth的顺序应该是：worker4的那个annotationId, rate = 5
// * worker2的annotation，rate = 3，worker3的annotation, rate = 1.5，然后是worker5的annotation
// * 没人打分的默认分现在写在sql里。
// */
//
//@Service
//public class DataSupplierForT2 implements ConstsForT2 {
//    @Autowired
//    private RequesterJPA requesterJPA;
//    @Autowired
//    private WorkerJPA workerJPA;
//    @Autowired
//    private TaskJPA taskJPA;
//    @Autowired
//    private ContractJPA contractJPA;
//    @Autowired
//    private Utils utils;
//
//    public List<Worker> mockWorkers() {
//        List<Worker> result = new ArrayList<>();
//        for (int i = 1; i <= 12; i++) {
//            Worker w = new Worker(); // 没iconName
//            w.setCreateTime(LocalDateTime.now().minusDays(100));
//            w.setEmailAddress("worker" + i + "@ex.com");
//            w.setNickname("三刀" + i);
//            w.setPassword(new BCryptPasswordEncoder().encode("worker" + i));
//            w.setIconName("hamburger1.png");
//            w.setCredit(14);
//            w.setExperiencePoint(101);
//            w.setProvince("北京");
//
//            result.add(w);
//        }
//        return result;
//    }
//
//    public List<Requester> mockRequesters() {
//        List<Requester> result = new ArrayList<>();
//        for (int i = 1; i <= 2; i++) {
//            Requester r = new Requester();
//            r.setCreateTime(LocalDateTime.now());
//            r.setEmailAddress("requester" + i + "@ex.com");
//            r.setNickname("岩雪");
//            r.setPassword(new BCryptPasswordEncoder().encode("requester" + i));
//            r.setIconName("hamburger1.png");
//            r.setCredit(10000);
//            r.setExperiencePoint(13500);
//            r.setProvince("山西");
//
//            result.add(r);
//        }
//        return result;
//    }
//
//    public List<Task> mockTasks() {
//        ArrayList<Task> result = new ArrayList<>();
//
////        Task t1 = new Task();
////        t1.setTaskName("task1");
////        t1.setTaskDescription("task1-巡址寻址: 请在图中框出你认为有价值的物体，并作出描述");
////        t1.setRequesterId(userByUsername(requesterJPA, "requester1@ex.com").getId());
//////        t1.setTaskTags(new HashSet<>(Arrays.asList(new Tag("f"), new Tag("f"))));
////
////        t1.setCreateTime(LocalDateTime.now().minusDays(10));
////        t1.setEndTime(LocalDateTime.now().plusDays(10));
////        t1.setDdl(LocalDateTime.now().plusDays(8));
////
////        t1.setWorkerRequirement(WorkerRequirement.NONE);
////
////        t1.setTaskCategory(TaskCategory.FRAME);
////
////        t1.setRewardStrategy(RewardStrategy.INDIVIDUAL);
////        t1.setTotalReward(200);
////        t1.setCapacity(20);
////        t1.setImgNames(new ArrayList<>(Arrays.asList(task1ImageName)));
////        t1.setTaskStatus(TaskStatus.UNDER_REVIEW);
////
////
////        Task t2 = new Task();
////        t2.setTaskName("task2");
////        t2.setTaskDescription("task2-相面: 请框出你认识的名人脸");
////        t2.setRequesterId(userByUsername(requesterJPA, "requester1@ex.com").getId());
//////        t2.setTaskTags(new HashSet<>(Arrays.asList(new Tag("a"), new Tag("f"))));
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
////        t2.setTaskStatus(TaskStatus.UNDER_REVIEW);
////
////
////        Task t3 = new Task();
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
////
////        result.add(t1);
////        result.add(t2);
////        result.add(t3);
//        return result;
//    }
//
//    public List<Contract> mockContract() {
//        List<Contract> result = new ArrayList<>();
//
//        ContractStatus[] cs = new ContractStatus[]{
//                ContractStatus.VIRGIN, // 这个是不用的
//                ContractStatus.IN_PROGRESS,
//                ContractStatus.COMPLETED,
//                ContractStatus.COMPLETED,
//                ContractStatus.COMPLETED,
//                ContractStatus.COMPLETED,
//                ContractStatus.ABORT
//        };
//
//        for (int i = 1; i <= 6; i++) {
//            Contract c = new Contract();
//            c.setTaskId(taskJPA.findByTaskName("task1").getTaskId());
////            System.out.println("pre print");
////            System.out.println("id:" + taskJPA.findByTaskName("task1").getTaskId());
//            c.setWorkerId(userByUsername(workerJPA, "worker" + i + "@ex.com").getId());
////            c.setContractStatus(cs[i]);
//            c.setLastEditTime(LocalDateTime.now());
//
//            result.add(c);
//        }
//
//        return result;
//    }
//
//    public List<FrameAnnotation> mockFrameAnnotations() {
//        List<FrameAnnotation> result = new ArrayList<>();
//
//        for (int i = 2; i <= 5; i++) {
//            long cid = utils.getContractIdForTask1ByI(i);
//            System.out.println("pre print");
//            System.out.println("cid:" + cid);
//
//            for (String imgname : task1ImageName) {
//                FrameAnnotation fa = new FrameAnnotation();
////                fa.setContractId(cid);
//                fa.setImgName(imgname);
//
//                result.add(fa);
//            }
//        }
//        return result;
//    }
//}
