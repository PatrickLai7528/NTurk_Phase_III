package foursomeSE;

import foursomeSE.entity.Frame;
import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.RAnnotations;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.Verification;
import foursomeSE.service.verification.VerificationService;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.DBDataKeeper;
import foursomeSE.util.DataSupplier;
import foursomeSE.util.WithTheAutowired;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static foursomeSE.service.task.TaskUtils.mtByImg;
import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnitTest4 extends WithTheAutowired {
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
    }

    @After
    public void after() {
//        dbDataKeeper.reclaimAll();
//        dbDataKeeper.clearAll();
    }

    @Test
    public void test1() { // 基本就是上一个版本的测试改一改
        assertEquals(29 + 40, iterableToList(microtaskJPA.findAll()).size());

        tid = taskJPA.findByTaskName("task2").getTaskId();

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
        RVerifications vr1 = rvfs(etr4.getImgNames(), IntStream.of(0, 0, 1, 1, 1), "worker1@ex.com");
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


        qualityVerificationService.saveVerifications(rvfs(etr7.getImgNames(), "worker3@ex.com"), "worker3@ex.com");
        qualityVerificationService.saveVerifications(rvfs(etr8.getImgNames(), "worker4@ex.com"), "worker4@ex.com");
        qualityVerificationService.saveVerifications(rvfs(etr9.getImgNames(), "worker5@ex.com"), "worker5@ex.com");


        RVerifications vr2 = rvfs(etr10.getImgNames(), IntStream.of(1, 0, 1, 1, 1), "worker6@ex.com");
        qualityVerificationService.saveVerifications(vr2, "worker6@ex.com");

        RVerifications vr3 = rvfs(etr11.getImgNames(), IntStream.of(1, 0, 1, 1, 1), "worker7@ex.com");
        qualityVerificationService.saveVerifications(vr3, "worker7@ex.com");

        EnterResponse etr13 = taskService.enterTask(tid, "worker3@ex.com");
        assertEquals(in(IntStream.of(7, 11, 12, 13, 14)), etr13.getImgNames());
        RAnnotations<FrameAnnotation> rats = rats(IntStream.of(7, 11, 12, 13, 14));
        frameAnnotationService.saveAnnotations(rats, "worker3@ex.com");

        EnterResponse etr14 = coverageVerificationService.enterVerification(tid, "worker2@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr14.getImgNames());
        coverageVerificationService.saveVerifications(rvfs(etr14.getImgNames(), IntStream.of(1, 1, 0, 0, 1), "worker2@ex.com"), "worker2@ex.com");

        EnterResponse etr15 = coverageVerificationService.enterVerification(tid, "worker3@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr15.getImgNames());
        coverageVerificationService.saveVerifications(rvfs(etr15.getImgNames(), IntStream.of(1, 1, 0, 0, 0), "worker3@ex.com"), "worker3@ex.com");

        EnterResponse etr16 = coverageVerificationService.enterVerification(tid, "worker4@ex.com");
        assertEquals(in(IntStream.rangeClosed(1, 5)), etr15.getImgNames());
        coverageVerificationService.saveVerifications(rvfs(etr16.getImgNames(), IntStream.of(1, 0, 0, 0, 0), "worker4@ex.com"), "worker4@ex.com");


        EnterResponse etr17 = taskService.enterTask(tid, "worker1@ex.com");
        assertEquals(in(IntStream.of(3, 4, 5, 15, 16)), etr17.getImgNames());
        frameAnnotationService.saveAnnotations(rats(IntStream.of(3, 4, 5, 15, 16)), "worker1@ex.com");
        // 注意到draw的时候是都可以draw的。

        int[] i = {10};

        fill(qualityVerificationService, i);
        fill(coverageVerificationService, i);

    }

    private void fill(VerificationService verificationService, int[] i) {
        while (true) {
            String username = "worker" + i[0] + "@ex.com";
            EnterResponse etr = qualityVerificationService.enterVerification(tid, username);
            assertTrue(in(IntStream.rangeClosed(1, 15)).containsAll(etr.getImgNames()));

            if (etr.getImgNames().isEmpty()) {
                break;
            }

            RVerifications rvfs = rvfs(etr.getImgNames(), username);
            qualityVerificationService.saveVerifications(rvfs, username);

            i[0]++;
        }
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

    private RVerifications rvfs(ArrayList<String> imgNames, String username) {
        return rvfs(imgNames, IntStream.of(1, 1, 1, 1, 1), username);
    }

    private RVerifications rvfs(ArrayList<String> imgNames, IntStream intStream, String username) {
        RVerifications rv = new RVerifications();
        rv.setVerifications(new ArrayList<>());

        int[] ints = intStream.toArray();
        for (int i = 0; i < imgNames.size(); i++) {
            String imgName = imgNames.get(i);
            int in = ints[i];
            FrameAnnotation fa = frameAnnotationService.getByImgName(imgName, username);
            Verification v = new Verification();
            v.setAnnotationId(fa.getAnnotationId());
            v.setRate(in);

            rv.getVerifications().add(v);
        }
        return rv;
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

    private ArrayList<String> in(IntStream intStream) {
        return intStream.mapToObj(i -> i + ".jpg").collect(Collectors.toCollection(ArrayList::new));
    }
}