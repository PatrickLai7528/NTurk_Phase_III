package foursomeSE.jpa.inspection;

import foursomeSE.entity.inspection.Inspection;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface InspectionJPA extends CrudRepository<Inspection, Long> {
}
