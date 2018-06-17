package foursomeSE.service.user.upper;

import foursomeSE.entity.task.Task;
import foursomeSE.entity.communicate.ExchangeRequest;
import foursomeSE.entity.communicate.SimpleCWorker;
import foursomeSE.entity.message.Message;
import foursomeSE.entity.message.MessageType;
import foursomeSE.entity.statistics.UserActivity;
import foursomeSE.entity.user.CWorker;
import foursomeSE.entity.user.UserType;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyNotValidException;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.tag.TagAndWorkerJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.UserJPA;
import foursomeSE.util.ConvenientFunctions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.task.TaskUtils.taskById;
import static foursomeSE.service.user.UserUtils.userById;
import static foursomeSE.service.user.UserUtils.userByUsername;
import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static foursomeSE.util.ConvenientFunctions.iterableToStream;
import static foursomeSE.util.ConvenientFunctions.listConvert;

@Service
public class UpperWorkerServiceImpl extends AbstractUpperUserServiceImpl<Worker, CWorker> implements UpperWorkerService {
//    private LowerContractService contractService;
    private ContractJPA contractJPA;
    //private LowerTaskService lowerTaskService;
    private TaskJPA taskJPA;
    private TagAndWorkerJPA tagAndWorkerJPA;

    public UpperWorkerServiceImpl(UserJPA<Worker> userJPA, MessageJPA messageJPA, ContractJPA contractJPA, TaskJPA taskJPA, TagAndWorkerJPA tagAndWorkerJPA) {
        super(userJPA, messageJPA);
        this.contractJPA = contractJPA;
        this.taskJPA = taskJPA;
        this.tagAndWorkerJPA = tagAndWorkerJPA;
    }

    @Override
    public CWorker getById(long id) {
        Worker worker = userById(userJPA, id);
        ArrayList<Worker> sortedWorkers = iterableToStream(userJPA.findAll())
                .sorted(ConvenientFunctions::ExperiementPointDescend)
                .collect(Collectors.toCollection(ArrayList::new));

        CWorker result = new CWorker(worker, sortedWorkers.indexOf(worker) + 1);
        double newExp = Double.parseDouble(String.format("%.2f", result.getExperiencePoint()));
        double newCrdt = Double.parseDouble(String.format("%.2f", result.getCredit()));
        result.setExperiencePoint(newExp);
        result.setCredit(newCrdt);

        worker.userTags = tagAndWorkerJPA.getWorkerTags(worker.getEmailAddress());

        return result;
    }

    @Override
    public List<SimpleCWorker> getAllSimple() {
        return listConvert(iterableToList(userJPA.findAll()), SimpleCWorker::new);
    }

    @Override
    public List<UserActivity> getUserActivity() {
        // 对于一个Worker
        // 先通过contract，找到所有参与过的任务。
        // 然后通过task的种类(groupingBy以后的size)判断这个worker属于哪个类型0还是1还是2还累3
        // 这就是在写数据库嘛
        List<UserActivity> result = Arrays.asList(
                new UserActivity(0, 0),
                new UserActivity(1, 0),
                new UserActivity(2, 0),
                new UserActivity(3, 0)
        );

        // 这个参与目前只要参与了就行，也不管有没有完成
        iterableToStream(userJPA.findAll()).forEach(w -> {

//            int name = contractService.getLotBy(c -> c.getWorkerId() == w.getId()).stream()
            int name = contractJPA.findByWorkerId(w.getId()).stream()
                    .map(c -> taskById(taskJPA, c.getTaskId()))
                    .collect(Collectors.groupingBy(Task::getTaskCategory, Collectors.counting())).size();
            result.stream()
                    .filter(u -> u.getName() == name)
                    .forEach(u -> u.setValue(u.getValue() + 1));
        });
        return result;
    }

    @Override
    public void add(Worker user) {
        super.add(user);
        messageJPA.save(Message.createMessage(user.getEmailAddress(), MessageType.CREAT_WORKER));
    }

    @Override
    public void exchange(ExchangeRequest exchangeRequest, String username) {
        Worker worker = userByUsername(userJPA, username);
        if (worker.getCredit() < exchangeRequest.getPoint()) {
            throw new MyNotValidException();
        }
        worker.setCredit(worker.getCredit() - exchangeRequest.getPoint());
        userJPA.save(worker);

        messageJPA.save(Message.createMessage(username, MessageType.WORKER_EXCHANGE, new String[]{
                String.format("%.2f", exchangeRequest.getPoint()),
                String.format("%.2f", exchangeRequest.getMoney())
        }));
    }

    /**
     * Implement
     */

    @Override
    protected UserType getUserType() {
        return UserType.WORKER;
    }
}
