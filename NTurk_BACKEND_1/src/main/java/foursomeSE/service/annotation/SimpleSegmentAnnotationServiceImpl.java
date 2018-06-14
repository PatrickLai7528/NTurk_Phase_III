package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.gold.GoldJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleSegmentAnnotationServiceImpl")
public class SimpleSegmentAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<SegmentAnnotation> {
    public SimpleSegmentAnnotationServiceImpl(ContractJPA contractJPA, AbstractAnnotationJPA<SegmentAnnotation> abstractAnnotationJPA, WorkerJPA workerJPA, MicrotaskJPA microtaskJPA, AnnotationJPA annotationJPA, GoldJPA goldJPA) {
        super(contractJPA, abstractAnnotationJPA, workerJPA, microtaskJPA, annotationJPA, goldJPA);
    }
}
