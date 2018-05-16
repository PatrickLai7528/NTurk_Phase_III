package foursomeSE.entity.user;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class CRequester extends Requester {
    private int rank;

    public CRequester() {
    }

    public CRequester(Requester requester, int rank) {
        setSameFields(this, requester);
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
