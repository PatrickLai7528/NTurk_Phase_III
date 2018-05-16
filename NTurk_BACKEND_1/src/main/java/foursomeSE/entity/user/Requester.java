package foursomeSE.entity.user;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class Requester extends MyUser {
    private long requesterId;

    public Requester() {
    }

    public Requester(CRequester cRequester) {
        setSameFields(this, cRequester);

    }

    public long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(long requesterId) {
        this.requesterId = requesterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Requester)) return false;

        Requester requester = (Requester) o;

        return requesterId == requester.requesterId;
    }

    @Override
    public int hashCode() {
        return (int) (requesterId ^ (requesterId >>> 32));
    }
}
