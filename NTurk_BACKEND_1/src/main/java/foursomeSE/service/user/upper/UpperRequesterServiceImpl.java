package foursomeSE.service.user.upper;

import foursomeSE.entity.task.Task;
import foursomeSE.entity.communicate.ExchangeRequest;
import foursomeSE.entity.message.Message;
import foursomeSE.entity.message.MessageType;
import foursomeSE.entity.statistics.UserActivity;
import foursomeSE.entity.user.CRequester;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.UserType;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.UserJPA;
import foursomeSE.util.ConvenientFunctions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.user.UserUtils.userById;
import static foursomeSE.service.user.UserUtils.userByUsername;
import static foursomeSE.util.ConvenientFunctions.iterableToStream;


@Service
public class UpperRequesterServiceImpl extends AbstractUpperUserServiceImpl<Requester, CRequester> implements UpperRequesterService {
    private TaskJPA taskJPA;

    public UpperRequesterServiceImpl(UserJPA<Requester> userJPA,
                                     MessageJPA messageJPA,
                                     TaskJPA taskJPA) {
        super(userJPA, messageJPA);
        this.taskJPA = taskJPA;
    }

    @Override
    public void exchange(ExchangeRequest exchangeRequest, String username) {
        Requester requester = userByUsername(userJPA, username);
        requester.setCredit(requester.getCredit() + exchangeRequest.getPoint());
        requester.setExperiencePoint(requester.getExperiencePoint() + exchangeRequest.getPoint());

        userJPA.save(requester);

        messageJPA.save(Message.createMessage(username, MessageType.REQUESTER_EXCHANGE, new String[]{
                String.format("%.2f", exchangeRequest.getMoney()),
                String.format("%.2f", exchangeRequest.getPoint())
        }));
    }

    @Override
    public CRequester getById(long id) {
        Requester requester = userById(userJPA, id);
        ArrayList<Requester> sortedRequesters = iterableToStream(userJPA.findAll())
                .sorted(ConvenientFunctions::ExperiementPointDescend)
                .collect(Collectors.toCollection(ArrayList::new));
        CRequester result = new CRequester(requester, sortedRequesters.indexOf(requester) + 1);
        double newExp = Double.parseDouble(String.format("%.2f", result.getExperiencePoint()));
        double newCrdt = Double.parseDouble(String.format("%.2f", result.getCredit()));
        result.setExperiencePoint(newExp);
        result.setCredit(newCrdt);
        return result;
    }

    @Override
    public List<UserActivity> getUserActivity() {
        List<UserActivity> result = Arrays.asList(
                new UserActivity(0, 0),
                new UserActivity(1, 0),
                new UserActivity(2, 0),
                new UserActivity(3, 0)
        );

        iterableToStream(userJPA.findAll()).forEach(w -> {
            int name = taskJPA.findByRequesterId(w.getId()).stream()
                    .collect(Collectors.groupingBy(Task::getTaskCategory, Collectors.counting())).size();
            result.stream().filter(u -> u.getName() == name).forEach(u -> u.setValue(u.getValue() + 1));
        });
        return result;
    }

    @Override
    public void add(Requester user) {
        super.add(user);
        messageJPA.save(Message.createMessage(user.getEmailAddress(), MessageType.CREAT_REQUESTER));
    }


    /**
     * Implements
     */
    @Override
    protected UserType getUserType() {
        return UserType.REQUESTER;
    }

}
