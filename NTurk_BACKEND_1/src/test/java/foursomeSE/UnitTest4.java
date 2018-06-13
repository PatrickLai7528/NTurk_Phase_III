package foursomeSE;

import foursomeSE.entity.Frame;
import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.RAnnotations;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.task.Microtask;
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

        long tid = taskJPA.findByTaskName("task2").getTaskId();

        EnterResponse etr1 = taskService.enterTask(tid, "worker1@ex.com");
        EnterResponse etr2 = taskService.enterTask(tid, "worker2@ex.com");

        assertEquals(in(IntStream.rangeClosed(1, 5)), etr1.getImgNames());
        assertEquals(in(IntStream.rangeClosed(6, 10)), etr2.getImgNames());

        // worker2完成
        frameAnnotationService.saveAnnotations(rats(IntStream.rangeClosed(6, 10)), "worker2@ex.com");

        // worker1没做完
        // 算了，反正那个15分钟一定不会到，因此应该也不会有冲突
        for (int i = 1; i <= 5; i++) {
            Microtask m = mtByImg(microtaskJPA, i + ".jpg");
            m.setParallel(0);
            microtaskJPA.save(m);
        }

        EnterResponse etr3 = qualityVerificationService.enterVerification(tid, "worker2@ex.com");
        assertTrue(etr3.getImgNames().isEmpty());

        EnterResponse etr4 = qualityVerificationService.enterVerification(tid, "worker1@ex.com");
        assertEquals(in(IntStream.rangeClosed(6, 10)), etr4.getImgNames());
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

    private ArrayList<String> in(IntStream intStream) {
        return intStream.mapToObj(i -> i + ".jpg").collect(Collectors.toCollection(ArrayList::new));
    }
}