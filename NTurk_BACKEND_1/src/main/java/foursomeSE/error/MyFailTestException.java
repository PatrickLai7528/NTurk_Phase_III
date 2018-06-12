package foursomeSE.error;

import java.util.ArrayList;

public class MyFailTestException extends RuntimeException {
    private ArrayList<Long> failedIds;

    public MyFailTestException(ArrayList<Long> failedIds) {
        this.failedIds = failedIds;
    }

    public ArrayList<Long> getFailedIds() {
        return failedIds;
    }
}
