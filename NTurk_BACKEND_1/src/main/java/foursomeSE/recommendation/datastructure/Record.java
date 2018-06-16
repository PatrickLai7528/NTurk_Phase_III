package foursomeSE.recommendation.datastructure;

public class Record {
    public int userID;
    public int taskID;
    public int score;

    private Record(){
    }

    public Record(int userID, int taskID, int score){
        this.userID = userID;
        this.taskID = taskID;
        this.score = score;
    }
}
