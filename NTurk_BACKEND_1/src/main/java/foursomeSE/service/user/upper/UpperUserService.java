package foursomeSE.service.user.upper;


import foursomeSE.entity.communicate.ExchangeRequest;
import foursomeSE.entity.statistics.UserActivity;
import foursomeSE.entity.statistics.UserDistribution;
import foursomeSE.entity.statistics.UserGrowth;
import foursomeSE.entity.statistics.UserNum;
import foursomeSE.entity.user.MyUser;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.function.Predicate;

public interface UpperUserService<T extends MyUser, D> {
    /**
     * 对传入user的要求（没做）
     * emailAddress合法，且之前没人用过
     * password合法
     * iconName必须是有图片的
     * credit，experimentPoint自己设0
     * province（必须有吗？）
     */
    void add(T user);

    /**
     * 改密码另外来吧唉
     * 可以改的
     * nickname
     * iconName
     * province
     */
    void update(T user);

    /**
     * TODO 改成get By Username
     */
    D getById(long id);

    long usernameToId(String username);

    boolean isUsernameExist(String username);

    /**
     * worker判断够不够换
     */
    void exchange(ExchangeRequest exchangeRequest, String username);

    default void editTags(List<String> tagNames, String username) {
    }


    /**
     * statistics
     */
    UserNum getUserNum();

    List<UserGrowth> getUserGrowth();

    List<UserDistribution> getUserDistribution();

    List<UserActivity> getUserActivity();
}
