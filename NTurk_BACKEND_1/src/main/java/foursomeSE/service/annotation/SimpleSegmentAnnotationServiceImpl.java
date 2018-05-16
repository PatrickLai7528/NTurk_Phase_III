package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.service.common.CommonCongruentService;
import foursomeSE.service.contract.UpperContractService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("simpleSegmentAnnotationServiceImpl")
public class SimpleSegmentAnnotationServiceImpl extends AbstractUpperAnnotationServiceImpl<SegmentAnnotation> {
    public SimpleSegmentAnnotationServiceImpl(UpperContractService contractService,
                                              CommonCongruentService<SegmentAnnotation> service) {
        super(contractService, service);
    }
}
