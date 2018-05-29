package foursomeSE;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.TaskCategory;
import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.entity.statistics.*;
import foursomeSE.entity.user.CRequester;
import foursomeSE.entity.user.CWorker;
import foursomeSE.entity.user.UserType;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.annotation.UpperAnnotationService;
import foursomeSE.service.common.CommonCongruentService;
import foursomeSE.service.task.UpperTaskService;
import foursomeSE.service.user.upper.UpperRequesterService;
import foursomeSE.service.user.upper.UpperWorkerService;
import foursomeSE.util.DBDataKeeper;
import foursomeSE.util.DataSupplier;
import foursomeSE.util.JsonDataKeeper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static foursomeSE.service.user.UserUtils.userByUsername;
import static org.assertj.core.api.Assertions.assertThat;

import static foursomeSE.util.DataSupplier.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnitTest { // 虽然叫UnitTest，但是先都写在一起了
    @Autowired
    private UpperAnnotationService<GeneralAnnotation> generalAnnotationService;

    @Autowired
    private UpperAnnotationService<FrameAnnotation> frameAnnotationService;

    @Autowired
    private UpperAnnotationService<SegmentAnnotation> segmentAnnotationService;

//    @Autowired
//    private CommonCongruentService<Contract> lowerContractService;
    @Autowired
    private ContractJPA contractJPA;

    @Autowired
    private UpperTaskService upperTaskService;

    @Autowired
//    private LowerTaskService lowerTaskService;
    private TaskJPA taskJPA;

    @Autowired
    private UpperWorkerService workerService;

    @Autowired
//    private LowerUserService<Worker> lowerWorkerService;
    private WorkerJPA workerJPA;

    @Autowired
    private UpperRequesterService requesterService;

    @Autowired
//    private LowerUserService<Requester> lowerRequesterService;
    private RequesterJPA requesterJPA;


    // keeper and supplier
    @Autowired
    private DBDataKeeper dbDataKeeper;

    @Autowired
    private DataSupplier dataSupplier;


    @Before
    public void doBeforeClass() {
        JsonDataKeeper.stashAll();
        dbDataKeeper.stashAll();

        workerJPA.saveAll(dataSupplier.mockWorkers());
        requesterJPA.saveAll(dataSupplier.mockRequesters());
        taskJPA.saveAll(dataSupplier.mockTasks());
        contractJPA.saveAll(dataSupplier.mockContract());

//        dataSupplier.mockContract().forEach(c -> lowerContractService.add(c));
    }

    @After
    public void doAfterClass() {
        JsonDataKeeper.reclaimAll();
        dbDataKeeper.reclaimAll();
    }

    @Test
    public void testUserNum() {
        assertEquals(workerService.getUserNum(), new UserNum(UserType.WORKER, 3));
        assertThat(requesterService.getUserNum()).isEqualTo(new UserNum(UserType.REQUESTER, 2));
    }

    @Test
    public void testTaskNum() {
        List<TaskNum> actual = upperTaskService.getTaskNums();
        List<TaskNum> expected = Arrays.asList(
                new TaskNum(TaskCategory.GENERAL, 1),
                new TaskNum(TaskCategory.SEGMENT, 0),
                new TaskNum(TaskCategory.FRAME, 2)
        );
        System.out.println(actual);
        assertTrue(actual.size() == expected.size() && actual.containsAll(expected));
    }

    @Test
    public void testWorkerGrowth() { // 这个不过可能是时间的问题，已经过了一天了，等把数据库的还原也做了以后再说吧…
        List<UserGrowth> actual = workerService.getUserGrowth();
        List<UserGrowth> expected = Arrays.asList(
                new UserGrowth(LocalDateTime.now().minusDays(50).toLocalDate(), 2),
                new UserGrowth(LocalDateTime.now().minusDays(100).toLocalDate(), 1)
        );
        System.out.println(actual);
        assertTrue(actual.size() == expected.size() && actual.containsAll(expected));
    }

    @Test
    public void testWorkerDistribution() {
        List<UserDistribution> actual = workerService.getUserDistribution();
        List<UserDistribution> expected = Arrays.asList(
                new UserDistribution("江苏", 1),
                new UserDistribution("山东", 2)
        );

        System.out.println(actual);
        assertTrue(actual.size() == expected.size() && actual.containsAll(expected));
    }

    @Test
    public void testWorkerActivity() { // 兩个activity不过也是因为id的问题，activity涉及任务了。可以把supplier里的getTask接受一个userJPA参数，用于转username成id
        List<UserActivity> actual = workerService.getUserActivity();
        List<UserActivity> expected = Arrays.asList(
                new UserActivity(0, 1),
                new UserActivity(1, 1),
                new UserActivity(2, 1),
                new UserActivity(3, 0)
        );
        System.out.println("pre print");
        System.out.println(actual);
        assertTrue(actual.size() == expected.size() && actual.containsAll(expected));
    }

    @Test
    public void testRequesterActivity() {
        List<UserActivity> actual = requesterService.getUserActivity();
        List<UserActivity> expected = Arrays.asList(
                new UserActivity(0, 0),
                new UserActivity(1, 2),
                new UserActivity(2, 0),
                new UserActivity(3, 0)
        );
        System.out.println("pre print");
        System.out.println(actual);
        assertTrue(actual.size() == expected.size() && actual.containsAll(expected));
    }

    @Test
    public void testTaskGrowth() {
        List<TaskGrowth> actual = upperTaskService.getTaskGrowth();
        List<TaskGrowth> expected = Arrays.asList(
                new TaskGrowth(LocalDate.now().minusDays(10), 1, 1, 0),
                new TaskGrowth(LocalDate.now().minusDays(20), 0, 1, 0)
        );

        System.out.println(actual);
        assertTrue(actual.size() == expected.size() && actual.containsAll(expected));
    }

    @Test
    public void testTaskStatus() {
        List<TaskStatusData> actual = upperTaskService.getTaskStatus();
        List<TaskStatusData> expected = Arrays.asList(
                new TaskStatusData(TaskCategory.GENERAL, 1, 0, 0),
                new TaskStatusData(TaskCategory.FRAME, 2, 0, 0),
                new TaskStatusData(TaskCategory.SEGMENT, 0, 0, 0)
        );

        System.out.println(actual);
        assertTrue(actual.size() == expected.size() && actual.containsAll(expected));
    }

    @Test
    public void testUserRank() { // 这个不过是因为用了getById，不过活该
        CWorker cw = workerService.getById(userByUsername(workerJPA, "worker1@ex.com").getId());
        assertEquals(1, cw.getRank());
        assertEquals("worker1@ex.com", cw.getEmailAddress());

        CRequester cr = requesterService.getById(userByUsername(requesterJPA, "requester1@ex.com").getId());
        assertEquals(2, cr.getRank());
    }

    @Test
    public void testGetTasks() {
        System.out.println("pre print");
        System.out.println(upperTaskService.getWorkerTasks("worker2@ex.com"));

        assertEquals(3, upperTaskService.getWorkerTasks("worker2@ex.com").size());
        assertEquals(0, upperTaskService.getNewTasks("worker2@ex.com").size());
    }
}
