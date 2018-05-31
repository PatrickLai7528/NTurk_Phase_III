package foursomeSE.util;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.inspection.Inspection;
import foursomeSE.entity.inspection.InspectionContract;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.annotation.FrameAnnotationJPA;
import foursomeSE.jpa.annotation.GeneralAnnotationJPA;
import foursomeSE.jpa.annotation.SegmentAnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.inspection.InspectionContractJPA;
import foursomeSE.jpa.inspection.InspectionJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.UserJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.stereotype.Service;

import java.util.List;

import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static foursomeSE.util.ConvenientFunctions.iterableToStream;

@Service
public class DBDataKeeper {
    private List<Contract> contracts;
    private List<Task> tasks;

    private List<FrameAnnotation> frameAnnotations;
    private List<GeneralAnnotation> generalAnnotations;
    private List<SegmentAnnotation> segmentAnnotations;

    private List<Worker> workers;
    private List<Requester> requesters;

    private List<Inspection> inspections;
    private List<InspectionContract> inspectionContracts;

    // jpas messages就不测了吧
    private WorkerJPA workerJPA;
    private RequesterJPA requesterJPA;

    private TaskJPA taskJPA;
    private ContractJPA contractJPA;

    private FrameAnnotationJPA frameAnnotationJPA;
    private GeneralAnnotationJPA generalAnnotationJPA;
    private SegmentAnnotationJPA segmentAnnotationJPA;

    private InspectionJPA inspectionJPA;
    private InspectionContractJPA inspectionContractJPA;

    // 这个也行？
    private AnnotationJPA annotationJPA;


    public DBDataKeeper(WorkerJPA workerJPA,
                        RequesterJPA requesterJPA,
                        TaskJPA taskJPA,
                        ContractJPA contractJPA,
                        FrameAnnotationJPA frameAnnotationJPA,
                        GeneralAnnotationJPA generalAnnotationJPA,
                        SegmentAnnotationJPA segmentAnnotationJPA,
                        InspectionJPA inspectionJPA,
                        InspectionContractJPA inspectionContractJPA,
                        AnnotationJPA annotationJPA) {
        this.workerJPA = workerJPA;
        this.requesterJPA = requesterJPA;
        this.taskJPA = taskJPA;
        this.contractJPA = contractJPA;
        this.frameAnnotationJPA = frameAnnotationJPA;
        this.generalAnnotationJPA = generalAnnotationJPA;
        this.segmentAnnotationJPA = segmentAnnotationJPA;
        this.inspectionJPA = inspectionJPA;
        this.inspectionContractJPA = inspectionContractJPA;
        this.annotationJPA = annotationJPA;
    }

    public void stashAll() {
        workers = iterableToList(workerJPA.findAll());
        requesters = iterableToList(requesterJPA.findAll());

        tasks = iterableToList(taskJPA.findAll());
        contracts = iterableToList(contractJPA.findAll());

        frameAnnotations = iterableToList(frameAnnotationJPA.findAll());
        generalAnnotations = iterableToList(generalAnnotationJPA.findAll());
        segmentAnnotations = iterableToList(segmentAnnotationJPA.findAll());

        inspections = iterableToList(inspectionJPA.findAll());
        inspectionContracts = iterableToList(inspectionContractJPA.findAll());

        clearAll(); // 这个是删老的
    }

    public void reclaimAll() {
        clearAll(); // 这个是删测试用的新的

        workerJPA.saveAll(workers);
        requesterJPA.saveAll(requesters);

        taskJPA.saveAll(tasks);
        contractJPA.saveAll(contracts);

        frameAnnotationJPA.saveAll(frameAnnotations);
        generalAnnotationJPA.saveAll(generalAnnotations);
        segmentAnnotationJPA.saveAll(segmentAnnotations);

        inspectionJPA.saveAll(inspections);
        inspectionContractJPA.saveAll(inspectionContracts);
    }

    public void clearAll() {
        workerJPA.deleteAll();
        requesterJPA.deleteAll();

        taskJPA.deleteAll();
        contractJPA.deleteAll();

        frameAnnotationJPA.deleteAll();
        generalAnnotationJPA.deleteAll();
        segmentAnnotationJPA.deleteAll();

        inspectionJPA.deleteAll();
        inspectionContractJPA.deleteAll();

        annotationJPA.deleteAll();
    }
}
