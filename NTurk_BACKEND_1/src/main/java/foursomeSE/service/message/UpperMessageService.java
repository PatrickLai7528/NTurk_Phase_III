package foursomeSE.service.message;

import foursomeSE.entity.message.Message;

import java.util.List;

public interface UpperMessageService {
    List<Message> getByUsername(String username);
}
