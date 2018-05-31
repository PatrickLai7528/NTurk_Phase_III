package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.FrameAnnotation;

import javax.transaction.Transactional;

@Transactional
public interface FrameAnnotationJPA extends AbstractAnnotationJPA<FrameAnnotation> {

}
