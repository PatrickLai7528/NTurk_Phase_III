package foursomeSE.jpa.user;

import foursomeSE.entity.user.Requester;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

@Transactional
public interface RequesterJPA extends UserJPA<Requester> {
    @Override
    @Query(value = "select id from requesters where email_address = ?1", nativeQuery = true)
    long usernameToId(String username);
}

/*
select id from requesters where email_address = ?1
 */
