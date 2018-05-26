package foursomeSE.jpa.user;

import foursomeSE.entity.user.MyUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserJPA<T extends MyUser> extends CrudRepository<T, Long> {
    Optional<T> findByEmailAddress(String emailAddress);
}
