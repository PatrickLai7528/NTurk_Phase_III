package foursomeSE.controller.annotation;

import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.service.annotation.UpperAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/segmentAnnotation")
public class SegmentAnnotationController extends AbstractAnnotationController<SegmentAnnotation>{
    @Override
    @Autowired
    @Qualifier("simpleSegmentAnnotationServiceImpl")
    public void setAnnotationService(UpperAnnotationService<SegmentAnnotation> annotationService) {
        this.annotationService = annotationService;
    }
}
