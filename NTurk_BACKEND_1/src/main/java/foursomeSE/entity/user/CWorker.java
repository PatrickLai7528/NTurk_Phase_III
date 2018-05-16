package foursomeSE.entity.user;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class CWorker extends Worker {
    private int rank;

    public CWorker() {
    }

    public CWorker(Worker worker, int rank) {
        setSameFields(this, worker);
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
