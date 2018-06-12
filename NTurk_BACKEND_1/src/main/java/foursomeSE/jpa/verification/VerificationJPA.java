package foursomeSE.jpa.verification;

import foursomeSE.entity.verification.Verification;
import foursomeSE.entity.verification.VerificationType;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface VerificationJPA extends CrudRepository<Verification, Long> {
    List<Verification> findByAnnotationIdAndVerificationType(long annotationId, VerificationType verificationType);
}
