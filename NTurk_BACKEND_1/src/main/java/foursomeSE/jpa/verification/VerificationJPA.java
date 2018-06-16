package foursomeSE.jpa.verification;

import foursomeSE.entity.verification.Verification;
import foursomeSE.entity.verification.VerificationType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface VerificationJPA extends CrudRepository<Verification, Long> {
    @Query(value = "SELECT *\n" +
            "FROM verification\n" +
            "WHERE annotation_id IN (SELECT annotation.annotation_id\n" +
            "                        FROM annotation\n" +
            "                        WHERE microtask_id IN (SELECT microtask_id\n" +
            "                                               FROM microtasks\n" +
            "                                               WHERE task_id = ?1))\n" +
            "      AND verification_type = ?2\n" +
            "      AND create_time > ?3 AND create_time < ?4",
            nativeQuery = true)
    List<Verification> findBetween(long taskId, int vTypeOrd, LocalDateTime floor, LocalDateTime roof);

    List<Verification> findByAnnotationIdAndVerificationType(long annotationId, VerificationType verificationType);
}


/*

// findBetween
SELECT *
FROM verification
WHERE annotation_id IN (SELECT annotation.annotation_id
                        FROM annotation
                        WHERE microtask_id IN (SELECT microtask_id
                                               FROM microtasks
                                               WHERE task_id = ?1))
      AND verification_type = ?2
      AND create_time > ?3 AND create_time < ?4
 */