package foursomeSE.recommendation.datastructure;

import foursomeSE.recommendation.tools.WilsonScore;

public class Record {
    public int userID;
    public int taskID;
    public double score;

    private Record(){
    }

    public Record(int userID, int taskID, double score){
        this.userID = userID;
        this.taskID = taskID;
        this.score = score;
    }

    public Record(int userID, int taskID, int pos, int total){
        this.userID = userID;
        this.taskID = taskID;
        this.score = WilsonScore.getScore(pos, total);
    }
}
