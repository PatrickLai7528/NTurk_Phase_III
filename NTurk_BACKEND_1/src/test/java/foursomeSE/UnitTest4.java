package foursomeSE;

import foursomeSE.entity.BlacklistItem;
import foursomeSE.entity.Frame;
import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.RAnnotations;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.task.CTask;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.Verification;
import foursomeSE.entity.verification.VerificationType;
import foursomeSE.error.MyFailTestException;
import foursomeSE.service.verification.QualityVerificationServiceImpl;
import foursomeSE.service.verification.VerificationService;
import foursomeSE.util.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static foursomeSE.service.task.TaskUtils.mtByImg;
import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnitTest4 extends WithTheAutowired implements MyConstants {
    @Autowired
    private DBDataKeeper dbDataKeeper;
    @Autowired
    private DataSupplier dataSupplier;

    private long tid;

    @Before
    public void before() {
        dbDataKeeper.stashAll();

        dataSupplier.mockRequesters();
        dataSupplier.mockWorkers();
        dataSupplier.mockTasks();

        tid = taskJPA.findByTaskName("task2").getTaskId();
    }

    @After
    public void after() {
//        dbDataKeeper.reclaimAll();
//        dbDataKeeper.clearAll();
    }

    @Test
    public void test1() { // 基本就是上一个版本的测试改一改
        assertEquals(29 + 40, iterableToList(microtaskJPA.findAll()).size());


        EnterResponse etr1 = taskService.enterTask(tid, "worker1@ex.com");
        EnterResponse etr2 = taskService.enterTask(tid, "worker2@ex.com");

        assertEquals(in(IntStream.rangeClosed(1, 5)), etr1.getImgNames());
        assertEquals(in(IntStream.rangeClosed(6, 10)), etr2.getImgNames());

        // worker2完成
        frameAnnotationService.saveAnnotations(rats(IntStream.rangeClosed(6, 10)), "worker2@ex.com");

        // worker1没做完
        failDraw(IntStream.rangeClosed(1, 5), "worker1@ex.com");

        EnterResponse etr3 = qualityVerificationService.enterVerification(tid, "worker2@ex.com");
        assertTrue(etr3.getImgNames().isEmpty());

        EnterResponse etr4 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertEquals(in(IntStream.rangeClosed(6, 10)), etr4.getImgNames());
        RVerifications vr1 = rvfs(etr4.getImgNames(), IntStream.of(0, 0, 1, 1, 1), VerificationType.QUALITY, "worker1@ex.com");
        qualityVerificationService.saveVerifications(vr1, "worker1@ex.com");


        EnterResponse etr5 = taskService.enterTask(tid, "worker1@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr5.getImgNames());
        frameAnnotationService.saveAnnotations(rats(IntStream.rangeClosed(1, 5)), "worker1@ex.com");


        EnterResponse etr6 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertTrue(etr6.getImgNames().isEmpty());


        EnterResponse etr7 = qualityVerificationService.enterVerification(tid, "worker3@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr7.getImgNames());

        EnterResponse etr8 = qualityVerificationService.enterVerification(tid, "worker4@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr8.getImgNames());

        EnterResponse etr9 = qualityVerificationService.enterVerification(tid, "worker5@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr9.getImgNames());


        EnterResponse etr10 = qualityVerificationService.enterVerification(tid, "worker6@ex.com");
        assertEquals(in(IntStream.rangeClosed(6, 10)), etr10.getImgNames());

        EnterResponse etr11 = qualityVerificationService.enterVerification(tid, "worker7@ex.com");
        assertEquals(in(IntStream.rangeClosed(6, 10)), etr11.getImgNames());

        EnterResponse etr12 = qualityVerificationService.enterVerification(tid, "worker8@ex.com");
        assertTrue(etr12.getImgNames().isEmpty());


        qualityVerificationService.saveVerifications(rvfs(etr7.getImgNames(), VerificationType.QUALITY, "worker3@ex.com"), "worker3@ex.com");
        qualityVerificationService.saveVerifications(rvfs(etr8.getImgNames(), VerificationType.QUALITY, "worker4@ex.com"), "worker4@ex.com");
        qualityVerificationService.saveVerifications(rvfs(etr9.getImgNames(), VerificationType.QUALITY, "worker5@ex.com"), "worker5@ex.com");


        RVerifications vr2 = rvfs(etr10.getImgNames(), IntStream.of(1, 0, 1, 1, 1), VerificationType.QUALITY, "worker6@ex.com");
        qualityVerificationService.saveVerifications(vr2, "worker6@ex.com");

        RVerifications vr3 = rvfs(etr11.getImgNames(), IntStream.of(1, 0, 1, 1, 1), VerificationType.QUALITY, "worker7@ex.com");
        qualityVerificationService.saveVerifications(vr3, "worker7@ex.com");

        EnterResponse etr13 = taskService.enterTask(tid, "worker3@ex.com");
        assertEquals(in(IntStream.of(7, 11, 12, 13, 14)), etr13.getImgNames());
        RAnnotations<FrameAnnotation> rats = rats(IntStream.of(7, 11, 12, 13, 14));
        frameAnnotationService.saveAnnotations(rats, "worker3@ex.com");

        EnterResponse etr14 = coverageVerificationService.enterVerification(tid, "worker2@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr14.getImgNames());
        coverageVerificationService.saveVerifications(rvfs(etr14.getImgNames(), IntStream.of(1, 1, 0, 0, 1), VerificationType.COVERAGE, "worker2@ex.com"), "worker2@ex.com");

        EnterResponse etr15 = coverageVerificationService.enterVerification(tid, "worker3@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr15.getImgNames());
        coverageVerificationService.saveVerifications(rvfs(etr15.getImgNames(), IntStream.of(1, 1, 0, 0, 0), VerificationType.COVERAGE, "worker3@ex.com"), "worker3@ex.com");

        EnterResponse etr16 = coverageVerificationService.enterVerification(tid, "worker4@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr15.getImgNames());
        coverageVerificationService.saveVerifications(rvfs(etr16.getImgNames(), IntStream.of(1, 0, 0, 0, 0), VerificationType.COVERAGE, "worker4@ex.com"), "worker4@ex.com");


        EnterResponse etr17 = taskService.enterTask(tid, "worker1@ex.com");
        assertEquals(in(IntStream.of(3, 4, 5, 15, 16)), etr17.getImgNames());
        frameAnnotationService.saveAnnotations(rats(IntStream.of(3, 4, 5, 15, 16)), "worker1@ex.com");
        // 注意到draw的时候是都可以draw的。

        int[] immu_i = {10};

        fill(qualityVerificationService, immu_i);
        fill(coverageVerificationService, immu_i);

        Iterator<BlacklistItem> iterator = blacklistJPA.findAll().iterator();
        iterator.next();
        assertTrue(!iterator.hasNext());

        List<String> qGoldStrs = new ArrayList<>();
        List<String> cGoldStrs = new ArrayList<>();
        for (int i = 0; i < GOLD_NUM; i++) {
            String qGoldStr = goldJPA.findByTaskIdAndOrdAndVerificationType(tid, i, VerificationType.QUALITY.ordinal());
            String cGoldStr = goldJPA.findByTaskIdAndOrdAndVerificationType(tid, i, VerificationType.COVERAGE.ordinal());

            qGoldStrs.add(qGoldStr);
            cGoldStrs.add(cGoldStr);
        }
        assertEquals(in(IntStream.of(1, 2, 3, 4, 5, 7, 8, 9, 10, 11)), qGoldStrs);
        assertEquals(in(IntStream.of(1, 3, 4, 5, 6, 7, 8, 9, 10, 11)), cGoldStrs);

        FrameAnnotation fantt1 = frameAnnotationService.getByImgName("7.jpg", 1, "worker11@ex.com");
        assertTrue(fantt1.getFrame() != null);
        assertTrue(fantt1.getFrames().isEmpty());

        FrameAnnotation fantt2 = frameAnnotationService.getByImgName("7.jpg", 2, "worker11@ex.com");
        assertTrue(fantt2.getFrames().isEmpty()); // 因为quality没过的就和没有存在过差不多。

        FrameAnnotation fantt3 = frameAnnotationService.getByImgName("3.jpg", 1, "worker1@ex.com");
        assertEquals(1, fantt3.getFrames().size());
        FrameAnnotation fantt4 = frameAnnotationService.getByImgName("3.jpg", 2, "worker1@ex.com");
        assertTrue(fantt4.getFrames().isEmpty());
        // 这两个一样
        FrameAnnotation fantt5 = frameAnnotationService.getByImgName("4.jpg", 1, "worker1@ex.com");
        assertEquals(1, fantt5.getFrames().size());
        FrameAnnotation fantt6 = frameAnnotationService.getByImgName("4.jpg", 2, "worker1@ex.com");
        assertTrue(fantt6.getFrames().isEmpty());

        FrameAnnotation fantt7 = frameAnnotationService.getByImgName("5.jpg", 2, "worker1@ex.com");
        assertEquals(1, fantt7.getFrames().size());
        fantt7 = frameAnnotationService.getByImgName("5.jpg", 1, "worker1@ex.com");
        assertEquals(1, fantt7.getFrames().size());
        assertEquals(1, fantt7.getIteration());

        System.out.println("pre print: " + immu_i[0]);


        enterDrawAndFinish("worker1@ex.com");
        enterDrawAndFinish("worker2@ex.com");
        enterDrawAndFinish("worker3@ex.com");
        // 这之后应当做到31.jpg


        // 突然意识到反正这个也没有用，直接覆盖就可以了。。
        etr1 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertEquals(in(IntStream.of(22, 23, 1, 2, 3)), etr1.getImgNames());

        etr2 = qualityVerificationService.enterVerification(tid, "worker2@ex.com");
        assertEquals(in(IntStream.of(16, 17, 1, 2, 3)), etr2.getImgNames());

        RVerifications rvfs = rvfs(etr1.getImgNames(), IntStream.of(1, 1, 1, 0, 0), VerificationType.QUALITY, "worker1@ex.com");
        testFallQV(rvfs, IntStream.of(2, 3), "worker1@ex.com");

        FQV(etr2.getImgNames(), IntStream.of(1, 1, 1, 1, 1), "worker2@ex.com");

        etr2 = qualityVerificationService.enterVerification(tid, "worker2@ex.com");
        assertEquals(in(IntStream.of(18, 19, 20, 4, 5)), etr2.getImgNames());
        failQV(etr2.getImgNames(), "worker2@ex.com");

        etr2 = coverageVerificationService.enterVerification(tid, "worker2@ex.com");
        assertEquals(in(IntStream.of(16, 17, 1, 3, 4)), etr2.getImgNames());
        FCV(etr2.getImgNames(), IntStream.of(0, 0, 1, 0, 0), "worker2@ex.com");

        etr1 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertEquals(in(IntStream.of(24, 25, 26, 4, 5)), etr1.getImgNames());
        rvfs = rvfs(etr1.getImgNames(), IntStream.of(0, 0, 0, 1, 0), VerificationType.QUALITY, "worker1@ex.com");
        testFallQV(rvfs, IntStream.of(5), "worker1@ex.com" );

        etr2 = taskService.enterTask(tid, "worker2@ex.com");
        assertEquals(in(IntStream.of(16, 17, 32, 33, 34)), etr2.getImgNames());
        frameAnnotationService.saveAnnotations(rats(etr2.getImgNames()), "worker2@ex.com");

        enterDrawAndFinish("worker3@ex.com");

        etr1 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertEquals(in(IntStream.of(16, 17, 27, 28, 7)), etr1.getImgNames());
        FQV(etr1.getImgNames(), IntStream.of(1, 1, 1, 1, 0), "worker1@ex.com");

        FrameAnnotation fa = frameAnnotationService.getByImgName("16.jpg", 2, "worker15@ex.com");// username瞎叫的
        assertEquals(1, fa.getIteration());

        etr1 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertEquals(in(IntStream.of(29, 30, 31, 32, 33)), etr1.getImgNames());
        FQV(etr1.getImgNames(), IntStream.of(0, 0, 0, 0, 0), "worker1@ex.com");

        etr1 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertEquals(in(IntStream.of(34, 35, 36, 37, 8)), etr1.getImgNames());
        rvfs = rvfs(etr1.getImgNames(), IntStream.of(0, 0, 0, 0, 0), VerificationType.QUALITY, "worker1@ex.com");
        testForbidQV(rvfs, IntStream.of(8), "worker1@ex.com");

        etr2 = qualityVerificationService.enterVerification(tid, "worker2@ex.com");
        assertEquals(in(IntStream.of(18, 19, 20, 4, 5)), etr2.getImgNames());

        assertTrue(CriticalSection.drawRecords.isEmpty());
        assertEquals(3, CriticalSection.qualityVerificationRecords.size());
        assertTrue(CriticalSection.coverageVerificationRecords.isEmpty());

        failQV(etr2.getImgNames(), "worker2@ex.com");
    }

    private void fill(VerificationService verificationService, int[] i) {
        while (true) {
            String username = "worker" + i[0] + "@ex.com";
            EnterResponse etr = verificationService.enterVerification(tid, username);
            assertTrue(in(IntStream.rangeClosed(1, 15)).containsAll(etr.getImgNames()));

            if (etr.getImgNames().isEmpty()) {
                break;
            }

            RVerifications rvfs = rvfs(
                    etr.getImgNames(),
                    verificationService instanceof QualityVerificationServiceImpl
                            ? VerificationType.QUALITY
                            : VerificationType.COVERAGE,
                    username
            );
            verificationService.saveVerifications(rvfs, username);

            i[0]++;
        }
    }

    private void enterDrawAndFinish(String username) {
        EnterResponse etr = taskService.enterTask(tid, username);
        frameAnnotationService.saveAnnotations(rats(etr.getImgNames()), username);
    }

    private void failDraw(IntStream intStream, String username) {
        int[] ints = intStream.toArray();
        for (int i : ints) {
            Microtask m = mtByImg(microtaskJPA, i + ".jpg");
            m.setParallel(0);
            microtaskJPA.save(m);

            CriticalSection.drawRecords
                    .removeIf(ii -> ii.username.equals(username)
                            && ii.microtaskId == m.getMicrotaskId());
        }
    }

    private void failQV(ArrayList<String> imgs, String username) {
        for (String img : imgs) {
            Microtask m = mtByImg(microtaskJPA, img);
            m.setParallel(m.getParallel() - 1);
            microtaskJPA.save(m);

            CriticalSection.qualityVerificationRecords
                    .removeIf(ii -> ii.username.equals(username)
                            && ii.microtaskId == m.getMicrotaskId());
        }
    }

    private void FQV(ArrayList<String> imgNames, IntStream intStream, String username) {
        FV(VerificationType.QUALITY, imgNames, intStream, username);
    }

    private void FCV(ArrayList<String> imgNames, IntStream intStream, String username) {
        FV(VerificationType.COVERAGE, imgNames, intStream, username);
    }

    private void FV(VerificationType vt, ArrayList<String> imgNames, IntStream intStream, String username) {
        VerificationService verificationService = vt == VerificationType.QUALITY ?
                qualityVerificationService : coverageVerificationService;

        RVerifications rvfs = rvfs(imgNames, intStream, vt, username);
        verificationService.saveVerifications(rvfs, username);
    }

    private void testFailFQV(ArrayList<String> imgs, IntStream input, IntStream traps, String username) {
        boolean exception = false;
        try {
            FQV(imgs, input, username);
        } catch (MyFailTestException e) {
            assertEquals(in(traps), e.getWarning().getFailedImgNames());
            exception = true;
        }

        assertTrue(exception);
    }

    private void testFallQV(RVerifications rvfs, IntStream ints, String username) {
        boolean ex = false;
        try {
            qualityVerificationService.saveVerifications(rvfs, username);
        } catch (MyFailTestException e) {
            assertEquals(in(ints), e.getWarning().getFailedImgNames());
            ex = true;
        }
        assertTrue(ex);
    }

    private void testForbidQV(RVerifications rvfs, IntStream ints, String username) {
        boolean ex = false;
        try {
            qualityVerificationService.saveVerifications(rvfs, username);
        } catch (MyFailTestException e) {
            assertEquals(in(ints), e.getWarning().getFailedImgNames());
            assertTrue(e.getWarning().isForbidden());
            ex = true;
        }
        assertTrue(ex);

        assertEquals(null, qualityVerificationService.enterVerification(tid, username).getImgNames());
    }

    private RAnnotations<FrameAnnotation> rats(IntStream intStream) {
        RAnnotations<FrameAnnotation> result = new RAnnotations<>();
        result.setAnnotations(intStream.mapToObj(i -> {
            FrameAnnotation g = new FrameAnnotation();
            g.setImgName(i + ".jpg");
            g.setFrame(new Frame());
            return g;
        }).collect(Collectors.toCollection(ArrayList::new)));
        return result;
    }

    private RAnnotations<FrameAnnotation> rats(ArrayList<String> imgs) {
        RAnnotations<FrameAnnotation> result = new RAnnotations<>();
        result.setAnnotations(imgs.stream().map(i -> {
            FrameAnnotation g = new FrameAnnotation();
            g.setImgName(i);
            g.setFrame(new Frame());
            return g;
        }).collect(Collectors.toCollection(ArrayList::new)));
        return result;
    }

    private RVerifications rvfs(ArrayList<String> imgNames, VerificationType vt, String username) {
        return rvfs(imgNames, IntStream.of(1, 1, 1, 1, 1), vt, username);
    }

    private RVerifications rvfs(ArrayList<String> imgNames, IntStream intStream, VerificationType vt, String username) {
        RVerifications rv = new RVerifications();
        rv.setVerifications(new ArrayList<>());

        int[] ints = intStream.toArray();
        for (int i = 0; i < imgNames.size(); i++) {
            String imgName = imgNames.get(i);
            int in = ints[i];
            FrameAnnotation fa = frameAnnotationService.getByImgName(imgName, vt.ordinal() + 1, username);
            Verification v = new Verification();
            v.setAnnotationId(fa.getAnnotationId());
            v.setRate(in);

            rv.getVerifications().add(v);
        }
        return rv;
    }

    private ArrayList<String> in(IntStream intStream) {
        return intStream.mapToObj(i -> i + ".jpg").collect(Collectors.toCollection(ArrayList::new));
    }

    // 这个就是测一下getWorkerTasks和getNewTasks
    @Test
    public void test2() throws InterruptedException {
        EnterResponse response = taskService.enterTask(tid, "worker1@ex.com");
        List<CTask> workerTasks = taskService.getWorkerTasks("worker1@ex.com");
        List<CTask> newTasks = taskService.getNewTasks("worker1@ex.com");
        assertEquals(1, workerTasks.size());
        assertEquals(1, newTasks.size());

        frameAnnotationService.saveAnnotations(rats(IntStream.rangeClosed(1, 5)), "worker1@ex.com");

        Thread.sleep(2000);

        Microtask mt = mtByImg(microtaskJPA, "1.jpg");
        Object[] oos = annotationJPA.findLatestBefore(mt.getMicrotaskId(), LocalDateTime.now());;
        Object[] oo = (Object[]) oos[0];
        BigInteger aid = (BigInteger) oo[0];
        int aStt = Integer.parseInt(oo[1].toString());

//        System.out.println("ogbu");
    }

    @Test
    public void test3() {
        // 一个简单测试，没想到之前竟然没
        for (int i = 0; i < 4; i++) {
            enterDrawAndFinish("worker1@ex.com");
        }

        for (int i = 2; i <= 4; i++) {
            String username = "worker" + i + "@ex.com";
            for (int j = 0; j < 3; j++) {
                EnterResponse etr = qualityVerificationService.enterVerification(tid, username);
                FQV(etr.getImgNames(), IntStream.of(1, 1, 1, 1, 1), username);
            }
        }

        for (int i = 2; i <= 4; i++) {
            String username = "worker" + i + "@ex.com";

            for (int j = 0; j < 3; j++) {
                EnterResponse etr = coverageVerificationService.enterVerification(tid, username);
                FCV(etr.getImgNames(), IntStream.of(1, 1, 1, 1, 1), username);
            }
        }

        String username = "worker2@ex.com";
        EnterResponse etr = qualityVerificationService.enterVerification(tid, username);
        assertEquals(in(IntStream.of(16, 17, 1, 2, 3)), etr.getImgNames());
        testFailFQV(etr.getImgNames(), IntStream.of(0, 0, 0, 0, 0), IntStream.of(1,2,3), username);

        etr = qualityVerificationService.enterVerification(tid, username);
        assertEquals(in(IntStream.of(16, 17, 18, 4, 5)), etr.getImgNames());
        testFailFQV(etr.getImgNames(), IntStream.of(0, 0, 0, 0, 0), IntStream.of(4, 5), username);

        username = "worker3@ex.com";
        etr = qualityVerificationService.enterVerification(tid, username);
        assertEquals(in(IntStream.of(16, 17, 1, 2, 3)), etr.getImgNames());

    }
}