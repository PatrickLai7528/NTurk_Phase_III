package foursomeSE.jpa.message;

import foursomeSE.entity.message.Message;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface MessageJPA extends CrudRepository<Message, Long> {
    List<Message> findByUsername(String username);
}
