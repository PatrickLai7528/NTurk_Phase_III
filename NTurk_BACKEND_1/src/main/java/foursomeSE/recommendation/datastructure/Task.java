package foursomeSE.recommendation.datastructure;

import java.util.ArrayList;

public class Task {
    private static int IDCount = 0;
    public int ID;
    public ArrayList<String> tagList;

    public Task(){
        this.ID = Task.IDCount;
        this.tagList=new ArrayList<>();
        IDCount++;
    }

    public Task(int ID, ArrayList<String> tagList){
        this.ID = ID;
        this.tagList = tagList;
    }
}
