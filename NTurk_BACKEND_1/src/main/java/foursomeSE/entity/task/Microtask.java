package foursomeSE.entity.task;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "microtasks")
public class Microtask {
    @Id
    @GeneratedValue
    private long microtaskId;

    @NotNull
    private long taskId;

    @NotNull
    private String imgName;

    @NotNull
    private MicrotaskStatus microtaskStatus;

    @NotNull
    private int ord;

    private LocalDateTime lastRequestTime;

    public Microtask() {
    }

    public long getMicrotaskId() {
        return microtaskId;
    }

    public void setMicrotaskId(long microtaskId) {
        this.microtaskId = microtaskId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public MicrotaskStatus getMicrotaskStatus() {
        return microtaskStatus;
    }

    public void setMicrotaskStatus(MicrotaskStatus microtaskStatus) {
        this.microtaskStatus = microtaskStatus;
    }

    public LocalDateTime getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(LocalDateTime lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }
}
