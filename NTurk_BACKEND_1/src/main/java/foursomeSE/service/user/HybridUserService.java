package foursomeSE.service.user;

public interface HybridUserService {
    long usernameToId(String username);

    boolean isUsernameExist(String username);
}
