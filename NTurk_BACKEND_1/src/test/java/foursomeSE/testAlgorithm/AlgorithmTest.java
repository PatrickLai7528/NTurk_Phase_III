package foursomeSE.testAlgorithm;

import foursomeSE.recommendation.Recommend;
import foursomeSE.recommendation.algorithm.ItemCFAlgorithm;
import foursomeSE.recommendation.algorithm.TagFilterAlgorithm;
import foursomeSE.recommendation.datastructure.User;
import foursomeSE.recommendation.tools.WilsonScore;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AlgorithmTest {
    public ItemCFAlgorithm itemCF;
    public TagFilterAlgorithm tagFilter;

    // must be static
    @Before
    public void beforeClassTest(){
        itemCF = new ItemCFAlgorithm();
        tagFilter = new TagFilterAlgorithm();
    }

//    @Test
////    public void recommendation0() {
////        //readJson(0);
////        ArrayList<Integer> result = itemCF.Recommendation(0);
////        System.out.println("Recommendation Result："+result);
////        assertEquals(10, (int)result.get(0));
////    }
////
////    @Test
////    public void TagFilter() {
////        ArrayList<Integer> result = tagFilter.Recommendation(0);
////        System.out.println("Recommendation Result："+result);
////        assertEquals(1, (int)result.get(0));
////    }



    @Test
    public void Fusion() {
        ArrayList<Integer> result = Recommend.getResult(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new User(0, new ArrayList<>()));
        System.out.println("Final Result："+result);
        assertEquals(10, (int)result.get(0));
    }

    @Test
    public void Wilson() {
        double score = WilsonScore.getScore(15, 15);
        System.out.println("Final Result：" + score);
    }
//    private void readJson(int i){
//        JsonParser parser=new JsonParser();
//        try {
//            JsonObject object=(JsonObject) parser.parse(new FileReader(Sample_ReadJSON.class.getResource("/foursomeSE.recommendation/datastructure" +i+".json").getFile()));
//
//            generateTaskList(object.get("taskNumber").getAsInt());
//            generateUserList(object.get("userNumber").getAsInt());
//            generateRecordList(object.get("record").getAsJsonArray());
//
//            System.out.println("userList: "+SystemData.userList.size());
//            System.out.println("taskList: "+SystemData.taskList.size());
//            System.out.println("recordList: "+SystemData.recordList.size());
//        } catch (JsonIOException e) {
//            e.printStackTrace();
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void generateUserList(int number){
//        SystemData.userList.clear();
//        for(int i=0;i<number;i++){
//            User newUser = new User();
//            SystemData.userList.add(newUser);
//        }
//    }
//
//    private void generateTaskList(int number){
//        SystemData.taskList.clear();
//        for(int i=0;i<number;i++){
//            Task newTask = new Task();
//            SystemData.taskList.add(newTask);
//        }
//    }
//
//    private void generateRecordList(JsonArray array){
//        SystemData.recordList.clear();
//        for (int i = 0; i < array.size(); i++) {
//            JsonArray subArray=array.get(i).getAsJsonArray();
//            Record newRecord = new Record(subArray.get(0).getAsInt(),
//                    subArray.get(1).getAsInt(), subArray.get(2).getAsInt());
//            SystemData.recordList.add(newRecord);
//        }
//    }


}