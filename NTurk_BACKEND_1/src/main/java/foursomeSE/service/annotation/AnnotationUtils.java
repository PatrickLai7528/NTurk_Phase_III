package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;

public class AnnotationUtils {
    public static <T extends Annotation> T annotationByContractIdAndImgName(AbstractAnnotationJPA<T> annotationJPA, long contractId, String imgName) {
//        return annotationJPA.findByContractIdAndImgName(contractId, imgName)
//                .orElseThrow(() -> new MyObjectNotFoundException("annotation with contractId " + contractId + " and imgName " + imgName + " is not found"));
        return null;
    }

    public static Annotation anttById(AnnotationJPA annotationJPA, long id) {
        return annotationJPA.findById(id).orElseThrow(() -> new MyObjectNotFoundException("annotatoin with id " + id + " is not found"));
    }
}
