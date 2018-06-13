package foursomeSE.util;

import foursomeSE.entity.Gold;
import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.entity.verification.Verification;
import org.springframework.stereotype.Service;

import java.util.List;

import static foursomeSE.util.ConvenientFunctions.iterableToList;

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

    private List<Verification> verifications;
    private List<Gold> golds;


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

        verifications = iterableToList(verificationJPA.findAll());
        golds = iterableToList(goldJPA.findAll());


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

        verificationJPA.saveAll(verifications);
        goldJPA.saveAll(golds);

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

        verificationJPA.deleteAll();
        goldJPA.deleteAll();


        // 别忘这个...
        CriticalSection.clearAll();
    }
}
