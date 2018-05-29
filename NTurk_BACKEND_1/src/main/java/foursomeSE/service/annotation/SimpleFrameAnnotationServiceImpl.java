package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.contract.UpperContractService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleFrameAnnotationServiceImpl")
public class SimpleFrameAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<FrameAnnotation> {
    public SimpleFrameAnnotationServiceImpl(ContractJPA contractJPA, AnnotationJPA<FrameAnnotation> annotationJPA, WorkerJPA workerJPA) {
        super(contractJPA, annotationJPA, workerJPA);
    }
}
