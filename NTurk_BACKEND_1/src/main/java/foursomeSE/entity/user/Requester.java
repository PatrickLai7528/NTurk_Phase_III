package foursomeSE.entity.user;

import javax.persistence.Entity;
import javax.persistence.Table;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

@Entity
@Table(name = "requesters")
public class Requester extends MyUser {
    public Requester() {
    }

    public Requester(CRequester cRequester) {
        setSameFields(this, cRequester);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Requester)) return false;

        Requester requester = (Requester) o;

        return id == requester.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
