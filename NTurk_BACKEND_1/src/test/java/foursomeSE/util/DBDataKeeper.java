package foursomeSE.util;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.inspection.Inspection;
import foursomeSE.entity.inspection.InspectionContract;
import foursomeSE.entity.task.Microtask;
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
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.UserJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.stereotype.Service;

import java.util.List;

import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static foursomeSE.util.ConvenientFunctions.iterableToStream;

@Service
public class DBDataKeeper extends WithTheAutowired {
    private List<Worker> workers;
    private List<Requester> requesters;

    private List<Task> tasks;
    private List<Contract> contracts;
    private List<Microtask> microtasks;

    private List<FrameAnnotation> frameAnnotations;
    private List<GeneralAnnotation> generalAnnotations;
    private List<SegmentAnnotation> segmentAnnotations;

    private List<Inspection> inspections;
//    private List<InspectionContract> inspectionContracts;

    // jpas messages就不测了吧

    public void stashAll() {
        workers = iterableToList(workerJPA.findAll());
        requesters = iterableToList(requesterJPA.findAll());

        tasks = iterableToList(taskJPA.findAll());
        contracts = iterableToList(contractJPA.findAll());
        microtasks = iterableToList(microtaskJPA.findAll());

        frameAnnotations = iterableToList(frameAnnotationJPA.findAll());
        generalAnnotations = iterableToList(generalAnnotationJPA.findAll());
        segmentAnnotations = iterableToList(segmentAnnotationJPA.findAll());

        inspections = iterableToList(inspectionJPA.findAll());
//        inspectionContracts = iterableToList(inspectionContractJPA.findAll());

        clearAll(); // 这个是删老的
    }

    public void reclaimAll() {
        clearAll(); // 这个是删测试用的新的

        workerJPA.saveAll(workers);
        requesterJPA.saveAll(requesters);

        taskJPA.saveAll(tasks);
        contractJPA.saveAll(contracts);
        microtaskJPA.saveAll(microtasks);

        frameAnnotationJPA.saveAll(frameAnnotations);
        generalAnnotationJPA.saveAll(generalAnnotations);
        segmentAnnotationJPA.saveAll(segmentAnnotations);

        inspectionJPA.saveAll(inspections);
//        inspectionContractJPA.saveAll(inspectionContracts);
    }

    public void clearAll() {
        workerJPA.deleteAll();
        requesterJPA.deleteAll();

        taskJPA.deleteAll();
        contractJPA.deleteAll();
        microtaskJPA.deleteAll();

        annotationJPA.deleteAll();
        frameAnnotationJPA.deleteAll();
        generalAnnotationJPA.deleteAll();
        segmentAnnotationJPA.deleteAll();

        inspectionJPA.deleteAll();
        inspectionContractJPA.deleteAll();

        // 别忘这个...
        CriticalSection.clearAll();
    }
}
