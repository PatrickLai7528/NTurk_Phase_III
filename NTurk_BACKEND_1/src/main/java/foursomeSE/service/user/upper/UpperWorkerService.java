package foursomeSE.service.user.upper;

import foursomeSE.entity.communicate.SimpleCWorker;
import foursomeSE.entity.user.CWorker;
import foursomeSE.entity.user.Worker;

import java.util.List;

public interface UpperWorkerService extends UpperUserService<Worker, CWorker> {
    List<SimpleCWorker> getAllSimple();
}
