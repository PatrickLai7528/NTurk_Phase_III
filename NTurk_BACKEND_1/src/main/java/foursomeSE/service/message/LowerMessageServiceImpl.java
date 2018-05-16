package foursomeSE.service.message;

import foursomeSE.entity.message.Message;
import foursomeSE.service.common.CommonCongruentServiceImpl;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class LowerMessageServiceImpl extends CommonCongruentServiceImpl<Message> implements LowerMessageService {

    @Override
    protected String getTableName() {
        return "message";
    }

    @Override
    protected Class<Message[]> getTArrayType() {
        return Message[].class;
    }

    @Override
    protected Function<Message, Long> getIdFunction() {
        return Message::getId;
    }

    @Override
    protected Consumer<Message> setIdFunction(long newId) {
        return m -> m.setId(newId);
    }
}
