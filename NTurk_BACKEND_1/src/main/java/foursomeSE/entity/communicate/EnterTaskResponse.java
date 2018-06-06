package foursomeSE.entity.communicate;

import java.util.ArrayList;

public class EnterTaskResponse {
    private ArrayList<String> imgNames;

    public EnterTaskResponse() {
    }

    public ArrayList<String> getImgNames() {
        return imgNames;
    }

    public void setImgNames(ArrayList<String> imgNames) {
        this.imgNames = imgNames;
    }
}
