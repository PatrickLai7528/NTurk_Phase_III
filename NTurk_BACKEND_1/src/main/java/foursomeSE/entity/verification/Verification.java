package foursomeSE.entity.verification;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Verification {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String username;

    @NotNull
    private long annotationId;

    @NotNull
    private int rate;

    @NotNull
    private VerificationType verificationType;

    @NotNull
    private LocalDateTime createTime;

    @NotNull
    private int isMajorityVoting;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(long annotationId) {
        this.annotationId = annotationId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(VerificationType verificationType) {
        this.verificationType = verificationType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public int getIsMajorityVoting() {
        return isMajorityVoting;
    }

    public void setIsMajorityVoting(int isMajorityVoting) {
        this.isMajorityVoting = isMajorityVoting;
    }
}
