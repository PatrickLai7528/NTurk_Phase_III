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

    /*
     * 基于用户对任务的"兴趣"来进行推荐
     * 兴趣通过用户本身的Tag和任务的Tag来进行计算
     * 
     * 算法思路：
     * 用户对某一个任务的"兴趣"可以理解为：用户的每一个UserTag和这个任务的契合度之和
     * 某个UserTag和某个任务的契合度，用(这个UserTag和这个任务中某个TaskTag的相似度)中的最大值来表示
     * 某个UserTag和某个任务的契合度如果低于某个界值，则被视为0
     * 即：Pij = Sigma(MAX(Similarity(ut, tt)))，ut belongs to UT(i)，tt belongs to TT(j)
     * 其中：Pij是用户i对于任务j的兴趣，UT(i)是用户i的Tag集合，TT(j)是任务j的Tag集合
     *      Similarity(ut, tt)指ut和tt这两个标签的相似度，
     *
     * 参考《推荐系统实践》P106～110，并有所改动
     *
     * */
    public static Map<Integer, Double> Recommendation(int userID){
        generate(userID);

        // 一些经常用到的数据，所以提出来，看起来方便一点
        // Rank中的某一项，是<任务ID，用户对这个任务的兴趣>
        // tasksJoined是用户参与过的任务列表
        Map<Integer, Double> rank = new HashMap<>();
        Set<Integer> tasksJoined = new HashSet<>();

        // 通过遍历任务记录，找出用户参与过的所有任务，并存储在tasksJoined中
        for(Record record: recordList){
            if(userID==record.userID){
                tasksJoined.add(record.taskID);
            }
        }

        /*
        * 对于用户未参与过的某个任务，
        * 计算某个UserTag和某个任务的契合度，并更新用户的兴趣列表
        * 某个UserTag和某个任务的契合度如果低于某个界值，则被视为0
        * 这里界限值被设为0.75，可能根据实际情况变动
        * 注意：现在已经不设置界限值了
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

    /*
     * 这个得麻烦连远翔来做
     * 函数作用：完成对于user、taskList的赋值
     * user："用户"对象（属性：用户ID、用户Tag列表）
     *
     * taskList：任务列表，按照任务ID排序。
     *          "任务"对象（属性：任务ID，任务Tag列表）
     *
     * recordList：任务记录列表，
     *             "任务记录"对象（属性：用户ID、任务ID、用户得分）
     * */
    private static void generate(int userID){
        JsonParser parser=new JsonParser();
        try {
            JsonObject object=(JsonObject) parser.parse(new FileReader(Main.class.getResource("/data/jsons/TagFilter.json").getFile()));

            JsonObject userObject = object.get("user").getAsJsonObject();
            int uID = userObject.get("ID").getAsInt();
            ArrayList<String> uTags = new ArrayList<>();

            JsonArray uTagsFromJSON = userObject.get("Tags").getAsJsonArray();
            for(int m=0;m<uTagsFromJSON.size();m++){
                uTags.add(uTagsFromJSON.get(m).getAsString());
            }

            user = new User(uID, uTags);

            taskList.clear();
            JsonArray array = object.get("taskList").getAsJsonArray();
            for(int i=0;i<array.size();i++){
                JsonObject task = array.get(i).getAsJsonObject();
                int ID = task.get("ID").getAsInt();
                ArrayList<String> tags = new ArrayList<>();

                JsonArray tagsFromJSON = task.get("Tags").getAsJsonArray();
                for(int k=0;k<tagsFromJSON.size();k++){
                    tags.add(tagsFromJSON.get(k).getAsString());
                }

                Task newTask = new Task(ID, tags);
                taskList.add(newTask);
            }

            recordList.clear();
            JsonArray array2 = object.get("recordList").getAsJsonArray();
            for (int i = 0; i < array2.size(); i++) {
                JsonObject record = array2.get(i).getAsJsonObject();
                int recordUserID = record.get("userID").getAsInt();
                int recordTaskID = record.get("taskID").getAsInt();
                int recordScore = record.get("score").getAsInt();
                Record newRecord = new Record(recordUserID,recordTaskID,recordScore);
                recordList.add(newRecord);
            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
