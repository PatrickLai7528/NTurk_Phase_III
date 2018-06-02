package foursomeSE;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.communicate.CInspection;
import foursomeSE.entity.communicate.CInspectionContract;
import foursomeSE.entity.communicate.CTaskForInspection;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.inspection.Inspection;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.task.TaskStatus;
import foursomeSE.jpa.annotation.FrameAnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.contract.UpperContractService;
import foursomeSE.service.inspection.UpperInspectionService;
import foursomeSE.service.task.UpperTaskService;
import foursomeSE.util.ConstsForT2;
import foursomeSE.util.DBDataKeeper;
import foursomeSE.util.DataSupplierForT2;
import foursomeSE.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.user.UserUtils.userByUsername;
import static org.junit.Assert.*;

import static foursomeSE.service.annotation.AnnotationUtils.annotationByContractIdAndImgName;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnitTest2 implements ConstsForT2 {
    @Autowired
    private DBDataKeeper dbDataKeeper;
    @Autowired
    private DataSupplierForT2 dataSupplier;
    @Autowired
    private Utils utils;

    @Autowired
    private WorkerJPA workerJPA;
    @Autowired
    private RequesterJPA requesterJPA;
    @Autowired
    private TaskJPA taskJPA;
    @Autowired
    private ContractJPA contractJPA;
    @Autowired
    private FrameAnnotationJPA frameAnnotationJPA;


    @Autowired
    private UpperInspectionService inspectionService;
    @Autowired
    private UpperTaskService taskService;
    @Autowired
    private UpperContractService contractService;

    @Before
    public void doBeforeClass() {
        dbDataKeeper.stashAll();

        workerJPA.saveAll(dataSupplier.mockWorkers());
        requesterJPA.saveAll(dataSupplier.mockRequesters());
        taskJPA.saveAll(dataSupplier.mockTasks());
        contractJPA.saveAll(dataSupplier.mockContract());
        List<FrameAnnotation> frameAnnotations = dataSupplier.mockFrameAnnotations();
        frameAnnotationJPA.saveAll(frameAnnotations);
    }

    @After
    public void doAfterClass() {
//        // dbDataKeeper.clearAll();
//        dbDataKeeper.reclaimAll();
    }

    @Test
    public void t1() {
        int[][] rates = new int[][]{
                {3, 4, 5},
                {1, 2, 5},
                {5, 2, 4}
        };

        // 测add
        for (int i = 3; i <= 4; i++) {
            CInspectionContract ci1 = new CInspectionContract();
            long cid1 = utils.getContractIdForTask1ByI(i);
            ci1.setContractId(cid1);

            for (int j = 0; j < task1ImageName.length; j++) {
                Inspection i1 = new Inspection();
                i1.setRate(rates[i-2][j]);
                long aid1 = annotationByContractIdAndImgName(frameAnnotationJPA, cid1, task1ImageName[j]).getAnnotationId();
                i1.setAnnotationId(aid1);

                ci1.getInspections().add(i1);
            }

            inspectionService.add(ci1, "worker1@ex.com");
        }

        long t1id = taskJPA.findByTaskName("task1").getTaskId();

        long[] wid = new long[13];
        for (int i = 1; i <= 12; i++) {
            wid[i] = userByUsername(workerJPA, "worker" + i + "@ex.com").getId();
        }

        long[] cid = new long[7];
        for (int i = 1; i <= 6; i++) {
            cid[i] = contractJPA.findByTaskIdAndWorkerId(t1id, wid[i]).get().getContractId();
        }

        long[] aid = new long[7];
        for (int i = 2; i <= 5; i++) {
            aid[i] = frameAnnotationJPA.findByContractIdAndImgName(cid[i], task1ImageName[0]).get().getAnnotationId();
        }


        // 测getNewInspectionTasks和getWorkerInspectionTasks
        List<CTaskForInspection> w2ti = taskService.getWorkerInspectionTasks("worker2@ex.com");
        assertEquals(1, w2ti.size());
        assertEquals(3, w2ti.get(0).getMandatoryTime());
        List<Task> newInspectionTasks1 = taskService.getNewInspectionTasks("worker2@ex.com");
        assertEquals(1, newInspectionTasks1.size());
        assertEquals("task2", newInspectionTasks1.get(0).getTaskName());

        assertEquals(0, taskJPA.findWorkerInspectionTasks("worker1@ex.com").size());
        assertEquals(2, taskJPA.findByTaskStatus(TaskStatus.UNDER_REVIEW.ordinal()).size());
        List<Task> newInspectionTasks2 = taskService.getNewInspectionTasks("worker1@ex.com");
        assertEquals(2, newInspectionTasks2.size());
        ArrayList<String> names = newInspectionTasks2.stream().map(Task::getTaskName).collect(Collectors.toCollection(ArrayList::new));
        assertTrue(names.contains("task1"));
        assertTrue(names.contains("task2"));

        // 测GetTaskByIdForInspection;
//        contractService.getByTaskIdForInspection(t1id, "worker2@ex.com");
        List<Contract> w2ci = contractJPA.findByTaskIdForInspection(t1id, wid[2]);
        assertEquals(1, w2ci.size());
        assertEquals(cid[5], w2ci.get(0).getContractId());
        List<Contract> qaz = contractJPA.findByTaskIdForInspection(t1id, wid[1]);
        assertEquals(2, qaz.size());
        ArrayList<Long> cidlist = qaz.stream().map(Contract::getContractId).collect(Collectors.toCollection(ArrayList::new));
        assertTrue(cidlist.contains(cid[2]));
        assertTrue(cidlist.contains(cid[5]));


        // 再加一波annotation
        int[][] addRates = new int[][]{
                {3, 5, 3},
                {2, 3, 1},
                {5, 2, 3}
        };

        for (int i = 2; i <= 4; i++) {
            CInspectionContract ci1 = new CInspectionContract();
            long cid1 = utils.getContractIdForTask1ByI(i);
            ci1.setContractId(cid1);

            for (int j = 0; j < task1ImageName.length; j++) {
                Inspection i1 = new Inspection();
                i1.setRate(addRates[i-2][j]);
                long aid1 = annotationByContractIdAndImgName(frameAnnotationJPA, cid1, task1ImageName[j]).getAnnotationId();
                i1.setAnnotationId(aid1);

                ci1.getInspections().add(i1);
            }

            inspectionService.add(ci1, i == 2 || i == 3 ? "worker5@ex.com" : "worker2@ex.com");
        }

        // 测getBestKth
        List<CInspection> bestKth = inspectionService.getBestKth(task1ImageName[0], "");
        List<CInspection> target = new ArrayList<>(Arrays.asList(
                new CInspection(aid[4], 5),
                new CInspection(aid[2], 3),
                new CInspection(aid[3], 1.5),
                new CInspection(aid[5], 1.4)
        ));
        assertTrue(target.containsAll(bestKth) && bestKth.containsAll(target));

        // 再测mandatoryTime
        assertEquals(2, taskService.getWorkerInspectionTasks("worker2@ex.com").get(0).getMandatoryTime());
    }
}
/*
select * from tasks
where task_status = 1 and task_id in (
    select task_id from contracts
    where contract_status = 0 and worker_id in (
        select id from workers
        where email_address = 'worker2@ex.com'
    )
);
 */
