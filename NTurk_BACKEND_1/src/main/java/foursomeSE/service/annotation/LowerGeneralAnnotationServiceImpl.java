package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.service.common.CommonCongruentService;
import foursomeSE.service.common.CommonCongruentServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class LowerGeneralAnnotationServiceImpl
        extends AbstractLowerAnnotationServiceImpl<GeneralAnnotation>
        implements CommonCongruentService<GeneralAnnotation> {
    @Override
    public String getTableName() {
        return "general_annotation";
    }

    @Override
    public Class<GeneralAnnotation[]> getTArrayType() {
        return GeneralAnnotation[].class;
    }
}
