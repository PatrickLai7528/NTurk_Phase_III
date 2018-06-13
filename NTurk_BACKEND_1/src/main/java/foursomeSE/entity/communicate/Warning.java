package foursomeSE.entity.communicate;

import java.io.Serializable;
import java.util.ArrayList;

public class Warning implements Serializable {
    private ArrayList<Long> failedIds; // faild gold test annotationId
    private boolean forbidden;

    public ArrayList<Long> getFailedIds() {
        return failedIds;
    }

    public void setFailedIds(ArrayList<Long> failedIds) {
        this.failedIds = failedIds;
    }

    public boolean isForbidden() {
        return forbidden;
    }

    public void setForbidden(boolean forbidden) {
        this.forbidden = forbidden;
    }
}
