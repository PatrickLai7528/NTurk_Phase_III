package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.common.CommonCongruentService;
import foursomeSE.service.contract.UpperContractService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleSegmentAnnotationServiceImpl")
public class SimpleSegmentAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<SegmentAnnotation> {
    public SimpleSegmentAnnotationServiceImpl(ContractJPA contractJPA, AnnotationJPA<SegmentAnnotation> annotationJPA, WorkerJPA workerJPA) {
        super(contractJPA, annotationJPA, workerJPA);
    }
}
