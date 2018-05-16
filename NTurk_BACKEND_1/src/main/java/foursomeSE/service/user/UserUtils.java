//package foursomeSE.service.user;
//
//import foursomeSE.entity.user.CRequester;
//import foursomeSE.entity.user.CWorker;
//import foursomeSE.entity.user.Requester;
//import foursomeSE.entity.user.Worker;
//import foursomeSE.service.user.upper.UpperUserService;
//
//import java.util.ArrayList;
//import java.util.stream.Collectors;
//
//// 真的很想全部都用static，我也是挺喜欢这个class的…
//public class UserUtils {
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
//}
