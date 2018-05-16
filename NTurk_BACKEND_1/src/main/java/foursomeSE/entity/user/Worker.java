package foursomeSE.entity.user;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class Worker extends MyUser {
    private long workerId;

    public Worker() {
    }

    public Worker(CWorker cWorker) {
        setSameFields(this, cWorker);
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker)) return false;

        Worker worker = (Worker) o;

        return workerId == worker.workerId;
    }

    @Override
    public int hashCode() {
        return (int) (workerId ^ (workerId >>> 32));
    }
}
