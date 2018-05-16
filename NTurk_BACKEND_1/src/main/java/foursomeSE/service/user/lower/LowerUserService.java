package foursomeSE.service.user.lower;

import foursomeSE.entity.user.MyUser;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.service.common.CommonCongruentService;

// 和Util一样，但是功能不一样，而且也不想复用了
public interface LowerUserService<T extends MyUser> extends CommonCongruentService<T> {
    long usernameToId(String username);

    default boolean isUsernameExist(String username) {
        try {
            usernameToId(username);
            return true;
        } catch (MyObjectNotFoundException e) {
            return false;
        }
    }
}
