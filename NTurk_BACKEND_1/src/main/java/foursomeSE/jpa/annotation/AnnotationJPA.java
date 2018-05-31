package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.Annotation;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface AnnotationJPA extends CrudRepository<Annotation, Long> {

}
