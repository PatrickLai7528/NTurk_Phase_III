package foursomeSE.service.message;

import foursomeSE.entity.message.Message;
import foursomeSE.jpa.message.MessageJPA;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpperMessageServiceImpl implements UpperMessageService {
//    private CommonCongruentService<Message> lowerMessageService;
    private MessageJPA messageJPA;

    public UpperMessageServiceImpl(MessageJPA messageJPA) {
        this.messageJPA = messageJPA;
    }

    @Override
    public List<Message> getByUsername(String username) {
//        return lowerMessageService.getLotBy(m -> m.getUsername().equals(username));
        return messageJPA.findByUsername(username);
    }
}
