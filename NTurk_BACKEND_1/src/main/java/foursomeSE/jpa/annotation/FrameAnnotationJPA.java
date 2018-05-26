package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.FrameAnnotation;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface FrameAnnotationJPA extends AnnotationJPA<FrameAnnotation> {

}
