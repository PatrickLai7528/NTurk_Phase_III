//package foursomeSE;
//
//import foursomeSE.entity.annotation.Annotation;
//import foursomeSE.entity.annotation.AnnotationStatus;
//import foursomeSE.entity.annotation.GeneralAnnotation;
//import foursomeSE.entity.annotation.RAnnotations;
//import foursomeSE.entity.communicate.EnterInspectionResponse;
//import foursomeSE.entity.communicate.EnterResponse;
//import foursomeSE.entity.task.Microtask;
//import foursomeSE.entity.task.MicrotaskStatus;
//import foursomeSE.entity.user.Worker;
//import foursomeSE.util.CriticalSection;
//import foursomeSE.util.DBDataKeeper;
//import foursomeSE.util.DataSupplierForT3;
//import foursomeSE.util.WithTheAutowired;
//import org.junit.*;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static foursomeSE.service.task.TaskUtils.mtByImg;
//import static foursomeSE.service.user.UserUtils.userByUsername;
//import static foursomeSE.util.ConvenientFunctions.iterableToList;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class UnitTest3 extends WithTheAutowired {
//    @Autowired
//    private DBDataKeeper dbDataKeeper;
//    @Autowired
//    private DataSupplierForT3 dataSupplier;
//
//
//    @Before
//    public void before() {
//        dbDataKeeper.stashAll();
//
//        dataSupplier.mockRequesters();
//        dataSupplier.mockWorkers();
//        dataSupplier.mockTasks();
//    }
//
//    @After
//    public void after() {
////        dbDataKeeper.clearAll();
////        dbDataKeeper.reclaimAll();
//    }
//
//
//    @Test
//    public void test1() {
//        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
//
//        long tid = taskJPA.findByTaskName("task1").getTaskId();
//        EnterResponse etr1 = taskService.enterTask(tid, "worker1@ex.com");
//        EnterResponse etr2 = taskService.enterTask(tid, "worker2@ex.com");
//
//        assertEquals(new ArrayList<>(Arrays.asList("1.jpg,2.jpg,3.jpg,4.jpg,5.jpg".split(","))), etr1.getImgNames());
//        assertEquals(new ArrayList<>(Arrays.asList("6.jpg,7.jpg,8.jpg,9.jpg,10.jpg".split(","))), etr2.getImgNames());
//
//        // worker2做完了
//        generalAnnotationService.saveAnnotations(rats(IntStream.rangeClosed(6, 10)), "worker2@ex.com");
//
//        // worker1没做完
//        for (int i = 1; i <= 5; i++) {
//            Microtask m = mtByImg(microtaskJPA, i + ".jpg");
////            m.setMicrotaskStatus(MicrotaskStatus.FAILED);
//            microtaskJPA.save(m);
//        }
//
//        EnterInspectionResponse etr3 = inspectionService.enterInspection(tid, "worker2@ex.com");
//        assertTrue(etr3.getAnnotationIds().isEmpty());
//
//        EnterInspectionResponse etr4 = inspectionService.enterInspection(tid, "worker1@ex.com");
//        assertEquals(aidsForInspectionFromTo(6, 10), etr4.getAnnotationIds());
//
//
//        RInspections rInspections = new RInspections();
//        ArrayList<Inspection> ris = etr4.getAnnotationIds().stream().map(l -> {
//            Inspection i = new Inspection();
//            i.setAnnotationId(l);
//            i.setRate(5);
//            return i;
//        }).collect(Collectors.toCollection(ArrayList::new));
//        rInspections.setInspections(ris);
//        inspectionService.saveInspections(rInspections, "worker1@ex.com");
//
//        // worker1又来了
//        EnterResponse etr5 = taskService.enterTask(tid, "worker1@ex.com");
//        assertEquals(new ArrayList<>(Arrays.asList("1.jpg,2.jpg,3.jpg,4.jpg,5.jpg".split(","))), etr5.getImgNames());
//
//        generalAnnotationService.saveAnnotations(rats(IntStream.rangeClosed(1, 5)), "worker1@ex.com");
//
//        // worker1来评分
//        EnterInspectionResponse etr6 = inspectionService.enterInspection(tid, "worker1@ex.com");
//        assertTrue(etr6.getAnnotationIds().isEmpty());
//
//        // worker3来评分
//        EnterInspectionResponse eir = inspectionService.enterInspection(tid, "worker3@ex.com");
//        assertEquals(aidsForInspectionFromTo(6, 10), eir.getAnnotationIds());
//
//        RInspections ri = new RInspections();
//        ArrayList<Inspection> ris2 = eir.getAnnotationIds().stream().map(l -> {
//            Inspection i = new Inspection();
//            i.setAnnotationId(l);
//            Annotation a = null;// generalAnnotationService.getById(l);
//            if (a.getImgName().startsWith("7")) {
//                i.setRate(3);
//            } else {
//                i.setRate(4);
//            }
//            return i;
//        }).collect(Collectors.toCollection(ArrayList::new));
//        ri.setInspections(ris2);
//        inspectionService.saveInspections(ri, "worker3@ex.com");
//
//        // worker3来做
//        EnterResponse etr8 = taskService.enterTask(tid, "worker3@ex.com");
//        assertEquals(new ArrayList<>(Arrays.asList("7.jpg,11.jpg,12.jpg,13.jpg,14.jpg".split(","))), etr8.getImgNames());
//
//        // 测钱
//        Worker worker2 = userByUsername(workerJPA, "worker2@ex.com");
//        assertEquals(4 * 0.6 * 10, worker2.getCredit(), 0.000001);
//
//        Worker worker1 = userByUsername(workerJPA, "worker1@ex.com");
//        assertEquals(5 * 0.05 * 10, worker1.getCredit(), 0.000001);
//
//        // 再测一点enterInspection
//        EnterInspectionResponse eir1 = inspectionService.enterInspection(tid, "worker3@ex.com");
//        assertEquals(aidsForInspectionFromTo(1, 5), eir1.getAnnotationIds());
//        EnterInspectionResponse eir2 = inspectionService.enterInspection(tid, "worker2@ex.com");
//        assertEquals(aidsForInspectionFromTo(1, 5), eir2.getAnnotationIds());
//        EnterInspectionResponse eir3 = inspectionService.enterInspection(tid, "worker4@ex.com");
//        assertTrue(eir3.getAnnotationIds().isEmpty());
//
//        generalAnnotationService.saveAnnotations(rats(IntStream.of(7, 11, 12, 13, 14)), "worker3@ex.com");
//
//        EnterInspectionResponse eir4 = inspectionService.enterInspection(tid, "worker4@ex.com");
//        assertEquals(aidsForInspectionByIntStream(IntStream.of(7, 11, 12, 13, 14)), eir4.getAnnotationIds());
//    }
//
//    // 测schedule里对于microtask的check
//    @Test
//    public void test2() throws InterruptedException {
//        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
//        long tid = taskJPA.findByTaskName("task1").getTaskId();
//
//        EnterResponse etr1 = taskService.enterTask(tid, "worker1@ex.com");
//
//        Microtask m = mtByImg(microtaskJPA, "1.jpg");
//        m.setLastRequestTime(m.getLastRequestTime().minusMinutes(15));
//        microtaskJPA.save(m);
//
//        Thread.sleep(10000);
//
//        m = mtByImg(microtaskJPA, "1.jpg");
////        assertEquals(MicrotaskStatus.FAILED, m.getMicrotaskStatus());
//    }
//
//    // 测schedule里对于inspection的check
//    @Test
//    public void test3() throws InterruptedException {
//        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
//        long tid = taskJPA.findByTaskName("task1").getTaskId();
//
//        EnterResponse etr1 = taskService.enterTask(tid, "worker1@ex.com");
//
////        RAnnotations<GeneralAnnotation> gara1 = new RAnnotations<>();
////        gara1.setAnnotations(IntStream.rangeClosed(1, 5).mapToObj(i -> {
////            GeneralAnnotation g = new GeneralAnnotation();
////            g.setImgName(i + ".jpg");
////            return g;
////        }).collect(Collectors.toCollection(ArrayList::new)));
//        generalAnnotationService.saveAnnotations(rats(IntStream.rangeClosed(1, 5)), "worker1@ex.com");
//
//
////        EnterInspectionResponse ei1 = inspectionService.enterInspection(tid, "worker2@ex.com");
////        EnterInspectionResponse ei2 = inspectionService.enterInspection(tid, "worker3@ex.com");
////
////        CriticalSection.inspectRecords.forEach(i -> {
////            if (i.username.equals("worker2@ex.com")) {
////                i.requestTime = i.requestTime.minusMinutes(15);
////            }
////        });
////
////        GeneralAnnotation a0 = generalAnnotationJPA.findByMicrotaskId(mtByImg(microtaskJPA, "1.jpg").getMicrotaskId()).get(0);
//////        assertEquals(2, a0.getParallel());
////
////        Thread.sleep(13000);
////
////        assertEquals(5, CriticalSection.inspectRecords.size());
////        assertEquals("worker3@ex.com", CriticalSection.inspectRecords.get(0).username);
////        assertTrue(CriticalSection.inspectRecords.stream().noneMatch(i -> i.username.equals("worker2@ex.com")));
////
////        a0 = generalAnnotationJPA.findByMicrotaskId(mtByImg(microtaskJPA, "1.jpg").getMicrotaskId()).get(0);
//////        assertEquals(1, a0.getParallel());
////        // assertTrue();
//    }
//
//    // 现在还有inspection没有提方法
//
//    private RAnnotations<GeneralAnnotation> rats(IntStream intStream) {
//        RAnnotations<GeneralAnnotation> result = new RAnnotations<>();
//        result.setAnnotations(intStream.mapToObj(i -> {
//            GeneralAnnotation g = new GeneralAnnotation();
//            g.setImgName(i + ".jpg");
//            return g;
//        }).collect(Collectors.toCollection(ArrayList::new)));
//        return result;
//    }
//
//    private List<Long> aidsForInspectionFromTo(int fromImg, int toImg) {
//        return aidsForInspectionByIntStream(IntStream.rangeClosed(fromImg, toImg));
//    }
//
//    private List<Long> aidsForInspectionByIntStream(IntStream intStream) {
//        return intStream.mapToObj(i -> {
//            Microtask m = mtByImg(microtaskJPA, i + ".jpg");
//            return generalAnnotationJPA.findByMicrotaskId(m.getMicrotaskId()).stream()
//                    .filter(g -> g.getAnnotationStatus() == AnnotationStatus.REVIEWABLE)
//                    .collect(Collectors.toCollection(ArrayList::new)).get(0)
//                    .getAnnotationId();
//        }).collect(Collectors.toCollection(ArrayList::new));
//    }
//
////    @Test
////    public void test4() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test5() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test6() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test7() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test8() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test9() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test10() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test11() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test12() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test13() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test14() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test15() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test16() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
////    @Test
////    public void test17() {
////        assertEquals(29, iterableToList(microtaskJPA.findAll()).size());
////
////    }
//}
