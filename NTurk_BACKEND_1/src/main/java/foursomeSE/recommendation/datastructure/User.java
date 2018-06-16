package foursomeSE.recommendation.datastructure;

import java.util.ArrayList;

public class User {
    private static int IDCount = 0;
    public int ID;
    public ArrayList<String> tagList;

    public User(){
        this.ID = User.IDCount;
        this.tagList = new ArrayList<>();
        IDCount++;
    }

    public User(int ID, ArrayList<String> tagList){
        this.ID = ID;
        this.tagList = tagList;
    }
}
