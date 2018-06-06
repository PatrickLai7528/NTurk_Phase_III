package foursomeSE.entity.annotation;


import java.util.List;

public class RAnnotations<T extends Annotation> {
    private List<T> annotations;

    public List<T> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<T> annotations) {
        this.annotations = annotations;
    }
}
