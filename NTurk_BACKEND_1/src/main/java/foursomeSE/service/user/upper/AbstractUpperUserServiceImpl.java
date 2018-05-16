package foursomeSE.service.user.upper;

import foursomeSE.entity.message.Message;
import foursomeSE.entity.statistics.UserDistribution;
import foursomeSE.entity.statistics.UserGrowth;
import foursomeSE.entity.statistics.UserNum;
import foursomeSE.entity.user.MyUser;
import foursomeSE.entity.user.UserType;
import foursomeSE.service.message.LowerMessageService;
import foursomeSE.service.user.lower.LowerUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractUpperUserServiceImpl<T extends MyUser, D> implements UpperUserService<T, D> {
    protected LowerUserService<T> service;
    protected LowerMessageService lowerMessageService;

    public AbstractUpperUserServiceImpl(LowerUserService<T> service,
                                        LowerMessageService lowerMessageService) {
        this.service = service;
        this.lowerMessageService = lowerMessageService;
    }

    @Override
    public long usernameToId(String username) {
        return service.usernameToId(username);
    }

    @Override
    public UserNum getUserNum() {
        return new UserNum(getUserType(), service.getLotBy(p -> true).size());
    }

    @Override
    public List<UserGrowth> getUserGrowth() {
        List<UserGrowth> results = new ArrayList<>();

        Map<LocalDate, Long> collect = service.getLotBy(p -> true).stream()
                .collect(Collectors.groupingBy(w -> w.getCreateTime().toLocalDate(), Collectors.counting()));
        collect.forEach((d, l) -> {
            results.add(new UserGrowth(d, Integer.parseInt(l.toString())));
        });
        return results;
    }

    @Override
    public List<UserDistribution> getUserDistribution() {
        List<UserDistribution> results = new ArrayList<>();

        Map<String, Long> collect = service.getLotBy(p -> true).stream()
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
        service.add(user);


        // lowerMessageService.add(new Message(user.getEmailAddress(), "创建用户", "在"));
    }

    @Override
    public void update(T user) {
        service.update(user);
    }

//    @Override
//    public List<T> getLotBy(Predicate<T> p) {
//        return service.getLotBy(p);
//    }

    @Override
    public boolean isUsernameExist(String username) {
        return service.isUsernameExist(username);
    }

    /**
     * abstract methods
     */
    protected abstract UserType getUserType();
}
