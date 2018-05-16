package foursomeSE.service.user.lower;

import foursomeSE.entity.user.Requester;
import foursomeSE.service.common.CommonCongruentServiceImpl;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class LowerRequesterServiceImpl extends CommonCongruentServiceImpl<Requester> implements LowerUserService<Requester> {
    @Override
    public long usernameToId(String username) {
        return getIdFunction().apply(getOneBy(r -> r.getEmailAddress().equals(username)));
    }

    @Override
    protected String getTableName() {
        return "requester";
    }

    @Override
    protected Class<Requester[]> getTArrayType() {
        return Requester[].class;
    }

    @Override
    protected Function<Requester, Long> getIdFunction() {
        return Requester::getRequesterId;
    }

    @Override
    protected Consumer<Requester> setIdFunction(long newId) {
        return r -> r.setRequesterId(newId);
    }
}
