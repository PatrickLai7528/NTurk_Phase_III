package foursomeSE.service.user.upper;

import foursomeSE.entity.statistics.UserDistribution;
import foursomeSE.entity.statistics.UserGrowth;
import foursomeSE.entity.statistics.UserNum;
import foursomeSE.entity.user.MyUser;
import foursomeSE.entity.user.UserType;
import foursomeSE.jpa.message.MessageJPA;
import foursomeSE.jpa.user.UserJPA;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static foursomeSE.service.user.UserUtils.userById;
import static foursomeSE.service.user.UserUtils.userByUsername;
import static foursomeSE.util.ConvenientFunctions.*;


public abstract class AbstractUpperUserServiceImpl<T extends MyUser, D> implements UpperUserService<T, D> {
    //    protected LowerUserService<T> service;
    protected UserJPA<T> userJPA;
    protected MessageJPA messageJPA;

    public AbstractUpperUserServiceImpl(UserJPA<T> userJPA,
                                        MessageJPA messageJPA) {
        this.userJPA = userJPA;
        this.messageJPA = messageJPA;
    }

    @Override
    public long usernameToId(String username) {
        return userByUsername(userJPA, username).getId();
    }

    @Override
    public UserNum getUserNum() {
        return new UserNum(getUserType(), (int) userJPA.count());
    }

    @Override
    public List<UserGrowth> getUserGrowth() {
        List<UserGrowth> results = new ArrayList<>();

        Map<LocalDate, Long> collect = iterableToStream(userJPA.findAll())
                .collect(Collectors.groupingBy(w -> w.getCreateTime().toLocalDate(), Collectors.counting()));
        collect.forEach((d, l) -> {
            results.add(new UserGrowth(d, Integer.parseInt(l.toString())));
        });
        return results;
    }

    @Override
    public List<UserDistribution> getUserDistribution() {
        List<UserDistribution> results = new ArrayList<>();

        Map<String, Long> collect = iterableToStream(userJPA.findAll())
                .collect(Collectors.groupingBy(MyUser::getProvince, Collectors.counting()));
        collect.forEach((d, l) -> {
            results.add(new UserDistribution(d, Integer.parseInt(l.toString())));
        });
        return results;
    }

    @Override
    public void add(T user) {
        user.setCreateTime(LocalDateTime.now());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        if (user.getIconName() == null || user.getIconName().equals("")) {
            user.setIconName("hamburger1.png");
        }
        user.setCredit(0);
        user.setExperiencePoint(0);

        userJPA.save(user);
    }

    @Override
    public void update(T user) {
        userJPA.save(user);
    }


    @Override
    public boolean isUsernameExist(String username) {
        return userJPA.findByEmailAddress(username).isPresent();
    }

    /**
     * abstract methods
     */
    protected abstract UserType getUserType();
}
