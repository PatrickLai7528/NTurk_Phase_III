package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleFrameAnnotationServiceImpl")
public class SimpleFrameAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<FrameAnnotation> {
    public SimpleFrameAnnotationServiceImpl(ContractJPA contractJPA, AbstractAnnotationJPA<FrameAnnotation> abstractAnnotationJPA, WorkerJPA workerJPA, MicrotaskJPA microtaskJPA, AnnotationJPA annotationJPA) {
        super(contractJPA, abstractAnnotationJPA, workerJPA, microtaskJPA, annotationJPA);
    }
}
