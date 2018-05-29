package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.SegmentAnnotation;

import javax.transaction.Transactional;

@Transactional
public interface SegmentAnnotationJPA extends AnnotationJPA<SegmentAnnotation> {
}
