package foursomeSE.util;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.UserJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.stereotype.Service;

import java.util.List;

import static foursomeSE.util.ConvenientFunctions.iterableToList;

@Service
public class DBDataKeeper {
    private List<Contract> contracts;
    private List<Task> tasks;

    private List<FrameAnnotation> frameAnnotations;
    private List<GeneralAnnotation> generalAnnotations;
    private List<SegmentAnnotation> segmentAnnotations;

    private List<Worker> workers;
    private List<Requester> requesters;

    // jpas
    private WorkerJPA workerJPA;
    private RequesterJPA requesterJPA;
    private TaskJPA taskJPA;
    private ContractJPA contractJPA;

    public DBDataKeeper(WorkerJPA workerJPA,
                        RequesterJPA requesterJPA,
                        TaskJPA taskJPA,
                        ContractJPA contractJPA) {
        this.workerJPA = workerJPA;
        this.requesterJPA = requesterJPA;
        this.taskJPA = taskJPA;
        this.contractJPA = contractJPA;
    }

    public void stashAll() {
        workers = iterableToList(workerJPA.findAll());
        requesters = iterableToList(requesterJPA.findAll());
        tasks = iterableToList(taskJPA.findAll());
        contracts = iterableToList(contractJPA.findAll());

        clearAll(); // 这个是删老的
    }

    public void reclaimAll() {
        clearAll(); // 这个是删测试用的新的

        workerJPA.saveAll(workers);
        requesterJPA.saveAll(requesters);
        taskJPA.saveAll(tasks);
        contractJPA.saveAll(contracts);
    }

    /**
     * private methods
     * */
    private void clearAll() {
        workerJPA.deleteAll();
        requesterJPA.deleteAll();
        taskJPA.deleteAll();
        contractJPA.deleteAll();
    }
}
