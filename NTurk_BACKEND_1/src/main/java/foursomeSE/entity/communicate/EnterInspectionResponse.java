package foursomeSE.entity.communicate;


import java.util.List;

public class EnterInspectionResponse {
    private List<Long> annotationIds;

    public List<Long> getAnnotationIds() {
        return annotationIds;
    }

    public void setAnnotationIds(List<Long> annotationIds) {
        this.annotationIds = annotationIds;
    }
}
