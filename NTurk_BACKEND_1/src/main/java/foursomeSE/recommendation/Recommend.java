package foursomeSE.recommendation;

import foursomeSE.recommendation.algorithm.ItemCFAlgorithm;
import foursomeSE.recommendation.algorithm.TagFilterAlgorithm;
import foursomeSE.recommendation.datastructure.Record;
import foursomeSE.recommendation.datastructure.Task;
import foursomeSE.recommendation.datastructure.User;
import foursomeSE.recommendation.tools.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recommend {
    /**
     * 使用了混合推荐算法（包括基于物品的协同过滤ItemCF和基于内容的过滤CB）
     * 采用了动态权重平衡两者关系
     * 动态权重：DynamicWeight = min(lambda*|Tr|/|I|, 1)，简记为dw
     * 其中，lambda是常数，经实验测得最佳值为4，Tr是用户参加过的任务集合，I是所有任务的集合
     * 推荐得分：P(i,j) = DW(i)*Ppcc(i,j) + (1-DW(i))*Pdbr(i,j)
     * 其中，DW(i)是上文提到的用户i的动态权重，
     * Ppcc(i,j)是由ItemCF算法计算的用户i对任务j的喜爱度
     * Pdbr(i,j)是由CB算法计算的用户i对任务j的喜爱度
     * P(i,j)是最后算出来的用户i对于任务j的喜爱度，也是最后排序的依据
     *
     * 参看刘沛文、陈华峰教授的论文
     * http://www.shcas.net/jsjyup/pdf/2017/4/基于用户行为特征的动态权重混合推荐算法.pdf
     * */
    public static ArrayList<Integer> getResult(ArrayList<User> users, ArrayList<Task> tasks, ArrayList<Record> records, User worker){
        double lambda = 4;
        double Tr = 3;
        double I = 15;
        double dWeight = Math.min(lambda*Tr/I, 1);

        Map<Integer, Double> itemCFList = ItemCFAlgorithm.Recommendation(users, tasks, records, worker);
        Map<Integer, Double> tagFilterList = TagFilterAlgorithm.Recommendation(tasks, records, worker);
        Map<Integer, Double> recommendList = new HashMap<>();

        for (Map.Entry<Integer, Double> data : itemCFList.entrySet()) {
            int taskID = data.getKey();
            double score = data.getValue();
            recommendList.compute(taskID, (k, v) -> (v == null) ? dWeight*score : v + dWeight*score);
        }

        for (Map.Entry<Integer, Double> data : tagFilterList.entrySet()) {
            int taskID = data.getKey();
            double score = data.getValue();
            recommendList.compute(taskID, (k, v) -> (v == null) ? (1-dWeight)*score : v + (1-dWeight)*score);
        }

        System.out.println("Fusion Recommend: " + Sort.descSortByValue(recommendList));
        ArrayList<Integer> resultList = new ArrayList<>(Sort.descSortByValue(recommendList).keySet());
        return resultList;
    }
}
