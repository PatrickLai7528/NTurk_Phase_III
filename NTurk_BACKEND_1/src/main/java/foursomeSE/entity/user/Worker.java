package foursomeSE.entity.user;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.List;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

@Entity
@Table(name = "workers")
public class Worker extends MyUser {
    @Transient
    public List<String> userTags;

    public Worker() {
    }

    public Worker(CWorker cWorker) {
        setSameFields(this, cWorker);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker)) return false;

        Worker worker = (Worker) o;

        return id == worker.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
