package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.service.common.CommonCongruentService;
import foursomeSE.service.contract.UpperContractService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleGeneralAnnotationService")
public class SimpleGeneralAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<GeneralAnnotation> {

    public SimpleGeneralAnnotationServiceImpl(UpperContractService contractService,
                                              CommonCongruentService<GeneralAnnotation> service) {
        super(contractService, service);
    }
}
