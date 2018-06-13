package foursomeSE.jpa;

import foursomeSE.entity.BlacklistItem;
import foursomeSE.entity.verification.VerificationType;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@Transactional
public interface BlacklistJPA extends CrudRepository<BlacklistItem, Long> {
    BlacklistItem findByUsernameAndTaskIdAndVerificationType(@NotNull String username, @NotNull long taskId, @NotNull VerificationType verificationType);
}
