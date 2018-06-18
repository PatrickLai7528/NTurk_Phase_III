package foursomeSE.recommendation.algorithm;

import com.google.gson.*;
import foursomeSE.recommendation.datastructure.Record;
import foursomeSE.recommendation.datastructure.Task;
import foursomeSE.recommendation.datastructure.User;
import foursomeSE.recommendation.tools.Sort;
import foursomeSE.recommendation.tools.WordSimilarity;
import sun.applet.Main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TagFilterAlgorithm {
    private static User user = null;
    private static ArrayList<Task> taskList = new ArrayList<>();
    private static ArrayList<Record> recordList = new ArrayList<>();

    /**
     * 基于用户对任务的"兴趣"来进行推荐
     * 兴趣通过用户本身的Tag和任务的Tag来进行计算
     * 
     * 算法思路：
     * 用户对某一个任务的"兴趣"可以理解为：用户的每一个UserTag和这个任务的契合度之和
     * 某个UserTag和某个任务的契合度，用(这个UserTag和这个任务中某个TaskTag的相似度)中的最大值来表示
     * 即：Pij = Sigma(MAX(Similarity(ut, tt)))，ut belongs to UT(i)，tt belongs to TT(j)
     * 其中：Pij是用户i对于任务j的兴趣，UT(i)是用户i的Tag集合，TT(j)是任务j的Tag集合
     *      Similarity(ut, tt)指ut和tt这两个标签的相似度，
     *
     * 参考《推荐系统实践》P106～110，并有所改动
     *
     * */
    public static Map<Integer, Double> Recommendation(ArrayList<Task> tasks, ArrayList<Record> records, User worker){
        user = worker;
        taskList = tasks;
        recordList = records;

        // 一些经常用到的数据，所以提出来，看起来方便一点
        // Rank中的某一项，是<任务ID，用户对这个任务的兴趣>
        // tasksJoined是用户参与过的任务列表
        Map<Integer, Double> rank = new HashMap<>();
        Set<Integer> tasksJoined = new HashSet<>();

        // 通过遍历任务记录，找出用户参与过的所有任务，并存储在tasksJoined中
        for(Record record: recordList){
            if(user.ID==record.userID){
                tasksJoined.add(record.taskID);
            }
        }

        /*
        * 对于用户未参与过的某个任务，
        * 计算某个UserTag和某个任务的契合度，并更新用户的兴趣列表
        * */
        for (String userTag: user.tagList) {
            for (Task task: taskList) {
                if(!tasksJoined.contains(task.ID)){
                    double fitness = getFitness(userTag, task);
                    rank.compute(task.ID, (k, v) -> (v == null) ? fitness : v + fitness);
                }
            }
        }

        return Sort.descSortByValue(rank);
    }

    /*
    * 计算某个UserTag和某个任务的契合度，
    * 用(这个UserTag和这个任务中某个TaskTag的相似度)中的最大值来表示
    *
    * userTag和taskTag的相似度用如下手段计算：
    * 调用tools.WordSimilarity里的方法来判断两个词语的相似度（基于同义词词林）
    * */
    private static double getFitness(String userTag, Task task){
        double maxSimilarity = 0;
        for(String taskTag: task.tagList){
            double sim = WordSimilarity.getSimilarity(userTag, taskTag);
            if(sim > maxSimilarity){
                maxSimilarity = sim;
            }
        }
        return maxSimilarity;
    }
}
