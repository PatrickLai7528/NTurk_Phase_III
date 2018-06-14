package foursomeSE.entity.communicate;

import java.io.Serializable;
import java.util.ArrayList;

public class Warning implements Serializable {
//    private ArrayList<Long> failedIds; // faild gold test annotationId
    private ArrayList<String> failedImgNames;
    private boolean forbidden;

//    public ArrayList<Long> getFailedIds() {
//        return failedIds;
//    }
//
//    public void setFailedIds(ArrayList<Long> failedIds) {
//        this.failedIds = failedIds;
//    }


    public ArrayList<String> getFailedImgNames() {
        return failedImgNames;
    }

    public void setFailedImgNames(ArrayList<String> failedImgNames) {
        this.failedImgNames = failedImgNames;
    }

    public boolean isForbidden() {
        return forbidden;
    }

    public void setForbidden(boolean forbidden) {
        this.forbidden = forbidden;
    }
}