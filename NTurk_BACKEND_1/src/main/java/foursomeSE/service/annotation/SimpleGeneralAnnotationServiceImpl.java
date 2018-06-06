package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleGeneralAnnotationService")
public class SimpleGeneralAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<GeneralAnnotation> {
    public SimpleGeneralAnnotationServiceImpl(ContractJPA contractJPA, AbstractAnnotationJPA<GeneralAnnotation> annotationJPA, WorkerJPA workerJPA, MicrotaskJPA microtaskJPA) {
        super(contractJPA, annotationJPA, workerJPA, microtaskJPA);
    }
}
