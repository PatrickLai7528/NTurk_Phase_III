package foursomeSE.service.message;

import foursomeSE.entity.message.Message;
import foursomeSE.service.common.CommonCongruentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpperMessageServiceImpl implements UpperMessageService {
    private CommonCongruentService<Message> lowerMessageService;

    public UpperMessageServiceImpl(CommonCongruentService<Message> lowerMessageService) {
        this.lowerMessageService = lowerMessageService;
    }

    @Override
    public List<Message> getByUsername(String username) {
        return lowerMessageService.getLotBy(m -> m.getUsername().equals(username));
    }
}
