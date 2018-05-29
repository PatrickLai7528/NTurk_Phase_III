package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.GeneralAnnotation;

import javax.transaction.Transactional;

@Transactional
public interface GeneralAnnotationJPA extends AnnotationJPA<GeneralAnnotation> {
}
