package foursomeSE.controller.annotation;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.service.annotation.UpperAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frameAnnotation")
public class FrameAnnotationController extends AbstractAnnotationController<FrameAnnotation> {
    @Override
    @Autowired
    @Qualifier("upperFrameAnnotationServiceImpl")
    public void setAnnotationService(UpperAnnotationService<FrameAnnotation> annotationService) {
        this.annotationService = annotationService;
    }
}
