package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.service.common.CommonCongruentService;
import foursomeSE.service.contract.UpperContractService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleFrameAnnotationServiceImpl")
public class SimpleFrameAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<FrameAnnotation> {
    public SimpleFrameAnnotationServiceImpl(UpperContractService contractService,
                                            CommonCongruentService<FrameAnnotation> service) {
        super(contractService, service);
    }
}
