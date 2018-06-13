package foursomeSE.entity;

import foursomeSE.entity.verification.VerificationType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "blacklist")
public class BlacklistItem {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String username;

    @NotNull
    private long taskId;

    @NotNull
    private VerificationType verificationType;

    @NotNull
    private int wrong;

    @NotNull
    private int haveEnter;

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

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(VerificationType verificationType) {
        this.verificationType = verificationType;
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public int getHaveEnter() {
        return haveEnter;
    }

    public void setHaveEnter(int haveEnter) {
        this.haveEnter = haveEnter;
    }
}
