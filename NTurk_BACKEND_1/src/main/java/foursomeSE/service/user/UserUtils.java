package foursomeSE.service.user;

import foursomeSE.entity.user.*;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.user.UserJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.user.upper.UpperUserService;

import java.util.ArrayList;
import java.util.stream.Collectors;

// 真的很想全部都用static，我也是挺喜欢这个class的…
public class UserUtils {
//    public static CWorker workerToC(UpperUserService<Worker> workerService, Worker worker) {
//
//        ArrayList<Worker> sortedWorkers = workerService.getLotBy(p -> true).stream()
//                .sorted((w1, w2) -> w2.getExperiencePoint() - w1.getExperiencePoint())
//                .collect(Collectors.toCollection(ArrayList::new));
//
//        return new CWorker(worker, sortedWorkers.indexOf(worker) + 1);
//    }
//
//    public static CRequester requesterToC(UpperUserService<Requester> requesterService, Requester requester) {
//        ArrayList<Requester> sortedRequesters = requesterService.getLotBy(p -> true).stream()
//                .sorted((w1, w2) -> w2.getExperiencePoint() - w1.getExperiencePoint())
//                .collect(Collectors.toCollection(ArrayList::new));
//
//        return new CRequester(requester, sortedRequesters.indexOf(requester) + 1);
//    }

    public static <T extends MyUser> T userById(UserJPA<T> userJPA, long id) {
        return userJPA.findById(id)
                .orElseThrow(() -> new MyObjectNotFoundException("user with id " + id + " is not found"));
    }

    public static <T extends MyUser> T userByUsername(UserJPA<T> userJPA, String username) {
        return userJPA.findByEmailAddress(username)
                .orElseThrow(() -> new MyObjectNotFoundException("user with username " + username + " is not found"));
    }
}
