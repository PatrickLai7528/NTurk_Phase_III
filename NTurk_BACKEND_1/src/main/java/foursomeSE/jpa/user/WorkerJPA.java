package foursomeSE.jpa.user;

import foursomeSE.entity.user.Worker;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

@Transactional
public interface WorkerJPA extends UserJPA<Worker> {
    @Override
    @Query(value = "select id from workers where email_address = ?1", nativeQuery = true)
    long usernameToId(String username);
}

/*
select id from workers where email_address = ?1
 */
