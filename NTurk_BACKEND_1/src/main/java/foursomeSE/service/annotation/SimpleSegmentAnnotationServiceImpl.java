package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleSegmentAnnotationServiceImpl")
public class SimpleSegmentAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<SegmentAnnotation> {
    public SimpleSegmentAnnotationServiceImpl(ContractJPA contractJPA, AbstractAnnotationJPA<SegmentAnnotation> annotationJPA, WorkerJPA workerJPA) {
        super(contractJPA, annotationJPA, workerJPA);
    }
}
