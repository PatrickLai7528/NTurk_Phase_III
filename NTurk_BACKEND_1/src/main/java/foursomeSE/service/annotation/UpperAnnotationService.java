package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.RAnnotations;
import foursomeSE.entity.communicate.report.Reports;

public interface UpperAnnotationService<T extends Annotation> {
    T getByImgName(String imgName, String username);


    // 感觉上面那些以前写的方法，现在都想删了。。。

    void saveAnnotations(RAnnotations<T> rAnnotations, String username);

    Reports getJson(long taskId, String username);
}
