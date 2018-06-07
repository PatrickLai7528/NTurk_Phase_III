package foursomeSE.entity.annotation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Annotation { // 先没有abstract，可能也许和annotationJPA冲突
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long annotationId;

//    @NotNull
//    private long contractId;
    @NotNull
    private String username;

    @NotNull
    private long microtaskId;

    @NotNull
    private AnnotationStatus annotationStatus;

    @NotNull
    private int parallel;

    @Transient
    private String imgName;

    public Annotation() {
    }

    public long getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(long annotationId) {
        this.annotationId = annotationId;
    }

    public long getMicrotaskId() {
        return microtaskId;
    }

    public void setMicrotaskId(long microtaskId) {
        this.microtaskId = microtaskId;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public AnnotationStatus getAnnotationStatus() {
        return annotationStatus;
    }

    public void setAnnotationStatus(AnnotationStatus annotationStatus) {
        this.annotationStatus = annotationStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }
}