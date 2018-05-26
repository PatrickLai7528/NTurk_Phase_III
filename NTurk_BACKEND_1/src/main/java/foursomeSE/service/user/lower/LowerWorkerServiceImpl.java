//package foursomeSE.service.user.lower;
//
//import foursomeSE.entity.user.Worker;
//import foursomeSE.service.common.CommonCongruentServiceImpl;
//import org.springframework.stereotype.Service;
//
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//@Service
//public class LowerWorkerServiceImpl extends CommonCongruentServiceImpl<Worker> implements LowerUserService<Worker> {
//
//    @Override
//    // 懒的再提了，copy过来拉倒
//    public long usernameToId(String username) {
//        return getIdFunction().apply(getOneBy(r -> r.getEmailAddress().equals(username)));
//    }
//
//    /**
//     * Implement
//     */
//    @Override
//    public String getTableName() {
//        return "worker";
//    }
//
//    @Override
//    public Class<Worker[]> getTArrayType() {
//        return Worker[].class;
//    }
//
//    @Override
//    public Function<Worker, Long> getIdFunction() {
//        return Worker::getId;
//    }
//
//    @Override
//    public Consumer<Worker> setIdFunction(long newId) {
//        return w -> w.setId(newId);
//    }
//}
