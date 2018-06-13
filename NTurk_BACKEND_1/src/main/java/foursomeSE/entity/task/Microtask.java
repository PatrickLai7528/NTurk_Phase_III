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

    @NotNull
    private int parallel;

    @NotNull
    private int iteration;

    @NotNull
    private int isSample;

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

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }

    public int getIsSample() {
        return isSample;
    }

    public void setIsSample(int isSample) {
        this.isSample = isSample;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }
}
