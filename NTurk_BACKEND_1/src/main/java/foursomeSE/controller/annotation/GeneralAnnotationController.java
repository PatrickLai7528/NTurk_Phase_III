package foursomeSE.controller.annotation;

import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.service.annotation.UpperAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/generalAnnotation")
public class GeneralAnnotationController extends AbstractAnnotationController<GeneralAnnotation> {

    @Override
    @Autowired
    @Qualifier("simpleGeneralAnnotationServiceImpl")
    public void setAnnotationService(UpperAnnotationService<GeneralAnnotation> annotationService) {
        this.annotationService = annotationService;
    }
}
