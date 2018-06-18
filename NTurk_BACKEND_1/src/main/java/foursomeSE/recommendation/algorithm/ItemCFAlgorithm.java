package foursomeSE.recommendation.algorithm;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.*;
import foursomeSE.recommendation.datastructure.Record;
import foursomeSE.recommendation.datastructure.Task;
import foursomeSE.recommendation.datastructure.User;
import foursomeSE.recommendation.tools.Sort;
import sun.applet.Main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class ItemCFAlgorithm {
    private static ArrayList<User> userList = new ArrayList<>();

    private static ArrayList<Task> taskList = new ArrayList<>();

    private static ArrayList<Record> recordList = new ArrayList<>();

    /*
     * 计算任务之间基于其参与用户的相似度，返回的是相似矩阵W
     *
     * 任务i和任务j的相似度wij = |N(i)&N(j)| / sqrt(|N(i)|*|N(j)|)
     * 分母|N(i)|是喜欢物品i的用户数，而分子|N(i)&N(j)|是同时喜欢物品i和物品j的用户数。
     * 因此，上述公式可以理解为喜欢物品i的用户中有多少比例的用户也喜欢物品j。
     *
     * 具体实现：
     * 用ItemCF算法计算物品相似度时首先建立用户—物品倒排表（即对每个用户建立一个他参与过的任务的列表），
     * 然后对于每个用户，将他任务列表中的任务两两在共现矩阵（即下面的稀疏矩阵）中加1，遍历完成后就可以得到所有|N(i)&N(j)|
     * 最后对稀疏矩阵中的每一项，再除以分母部分sqrt(|N(i)|*|N(j)|，既可以得到最后的W
     *
     * 参看《推荐系统实践》P53
     * */
    private static Table<Integer, Integer, Double> taskSimilarity(){
        // 一些经常用到的数据，所以提出来，看起来方便一点
        int taskNumber = taskList.size();
        int userNumber = userList.size();

        /*
         * invertList：用户对于任务的倒排表，对于每个用户保存该用户参与过的任务
         *             ID为x的用户对应倒排表中的第x项，第x项中的ArrayList<Integer>是这个用户参与过的任务的ID的列表
         *
         * taskCount：任务数量统计，它的第x项的数值 == ID为x的任务的参与人数
         *
         * spareMatrix：taskNumber * taskNumber 的稀疏矩阵，
         *              spareMatrix[i][j] == spareMatrix[j][i] == 既参与ID为i的任务又参与ID为j的任务的用户数量
         *
         * maxWij：为矩阵归一化服务，增加推荐的准确度，还可以提高推荐的覆盖率和多样性
         *
         * 使用倒排表的理由是为了避免任务数量过多时，造成的任务两两对比的高时间开销
         * 参见《推荐系统事件》的P46上半部分、P53下半部分（P46说的是userCF用这个的理由，P53说的是itemCF也有必要用这个）
         *
         * 关于相似矩阵归一化，参见《推荐系统实践》P58
         * */
        Map<Integer, Set<Integer>> invertedList = new HashMap<>(userNumber);
        Map<Integer, Integer> taskCount = new HashMap<>(taskNumber);
        Table<Integer, Integer, Double> spareMatrix = HashBasedTable.create();
        Map<Integer, Double> maxWij = new HashMap<>();

        // 对ArrayList初始化
        for(User user: userList){
            int userID = user.ID;
            invertedList.put(userID, new HashSet<>());
        }

        // 遍历任务记录，来更新"倒排表"和"任务数量统计"的数值
        for(Record record: recordList){
            int userID = record.userID;
            int taskID = record.taskID;
            invertedList.get(userID).add(taskID);
            taskCount.compute(taskID, (k, v) -> (v == null) ? 1 : v + 1);
        }

        // 根据倒排表来更新稀疏矩阵
        for(Map.Entry<Integer, Set<Integer>> data: invertedList.entrySet()){
            Set<Integer> tasks = data.getValue();
            for(int u: tasks){
                for(int v: tasks){
                    if(u!=v){
                        /*
                         * John S. Breese在论文 中提出了一个称为IUF（Inverse User Frequence），即用户活跃度对数的 倒数的参数，
                         * 他也认为活跃用户对物品相似度的贡献应该小于不活跃的用户，他提出应该增加IUF 参数来修正物品相似度的计算公式：
                         * wij = Sigma( 1 / log(1 + N(u))) / sqrt(|N(i)|*|N(j)|), 其中Sigma的限制条件为 u belongs to（N(i)&N(j)）
                         * 为了计算方便，分子部分的改动在稀疏矩阵这一步进行
                         * 参考《推荐系统实践》P57
                         *
                         * 注意：
                         * 下面的IUF的分子不用担心除零错误，因为如果tasks.size()==0的话
                         * 根本不会进入到两个for循环的内部
                         * */
                        double IUF = 1 / Math.log(1 + tasks.size());
                        spareMatrix.put(u, v, spareMatrix.contains(u,v)? spareMatrix.get(u,v)+IUF: IUF);
                        spareMatrix.put(v, u, spareMatrix.contains(v,u)? spareMatrix.get(v,u)+IUF: IUF);
                    }
                }
            }
        }


        /* 计算最后的相似矩阵W，其每一项是某两个任务之间的相似度
         * |N(i)|是参与任务i的用户数，|N(j)|是参与任务j的用户数，|N(i)&N(j)|是参加两种任务的用户数
         * 相似度 wij = |N(i)&N(j)| / sqrt(|N(i)|*|N(j)|)
         * */
        Table<Integer, Integer, Double> W = spareMatrix;
        Set<Table.Cell<Integer, Integer, Double>> tableSet = W.cellSet();

        for(Table.Cell<Integer, Integer, Double> data: tableSet){
            int i = data.getRowKey();
            int j = data.getColumnKey();
            double value = data.getValue();
            double newValue = value / (Math.sqrt(taskCount.get(i) * taskCount.get(j)));
            W.put(i, j, newValue);
            if(maxWij.containsKey(i)){
                maxWij.compute(i, (k, v) -> (newValue >= v) ? newValue : v);
            } else {
                maxWij.put(i, newValue);
            }
        }

        /*
         * 相似矩阵归一化，增加推荐的准确度，还可以提高推荐的覆盖率和多样性
         * Wij' = Wij / maxWij
         * 参看《推荐系统实践》P58
         * */
        for(Table.Cell<Integer, Integer, Double> data: tableSet){
            int i = data.getRowKey();
            int j = data.getColumnKey();
            double value = data.getValue();
            double newValue = value / maxWij.get(i);
            W.put(i, j, newValue);
        }
        return W;
    }

    /**
     * 带解释的推荐算法
     *
     * 根据用户ID，返回的是给用户的推荐列表（任务ID列表）
     * 推荐列表的每一项是被推荐的ID，推荐列表按照推荐程度降序排列
     * 参考书目：《推荐系统实践》P55
     *
     * 思路如下：
     *
     * 对于用户u喜欢的某个物品i,
     * 遍历相似度矩阵W中第i行，取得物品i和物品1～n的相似度，并从大到小排序，并取得前K个，就是算出S(i,k)
     * 遍历这前K个，如果用户喜欢过这个物品，则跳过;
     * 否则，对于某个新物品j，rank列表中添加一条 [ j: 用户对物品i的兴趣*物品j和物品i的相似度]
     *
     * 于是，用户u对某个任务j的兴趣：Puj = sigma(Wij * Rui)，其中：i belongs to N(u) ; j belongs to S(i, K)
     * 最后把rank处理一下，就是返回的推荐列表
     * */
    public static Map<Integer, Double> Recommendation(ArrayList<User> users, ArrayList<Task> tasks, ArrayList<Record> records, User worker){
        userList = users;
        taskList = tasks;
        recordList = records;
        int userID = worker.ID;

        // 一些经常用到的数据，所以提出来，看起来方便一点
        // Ru中的某一项，是<任务ID，用户对这个任务的兴趣>
        Map<Integer, Double> rank = new HashMap<>();
        Map<Integer, Double> Ru = new HashMap<>();
        Table<Integer, Integer, Double> W = taskSimilarity();
        int K = 10;     //根据《推荐系统实践》的P57，K=10的时候有最高的准确率和召回率

        /*
        * 通过遍历记录条目，生成用户u感兴趣的物品列表
        * Ru中的某一项，是<任务ID，用户对这个任务的兴趣>
        * Ru中的任务是用户已经做过的任务
        * */
        for(Record record: recordList){
            if(userID==record.userID){
                int taskID = record.taskID;
                double score = record.score;

                // 这里计算旧的平均得分和新得分的平均值
                // 如果用户对于一个任务做了多次，就说明是打回重做，这样计算得分的话最新一次的得分权重最高
                Ru.compute(taskID, (k, v) -> (v == null) ? score : (v+score)/2);
            }
        }

        /*
        * 对于用户感兴趣的每一个任务i
        * 遍历相似度矩阵W中第i行，取得物品i和物品1～n的相似度，并从大到小排序，并取得前K个，就是算出S(i,k)
        * 遍历这前K个，如果用户喜欢过这个物品，则跳过;
        * 否则，对于某个新物品j，rank列表中添加一条 [ j: 用户对物品i的兴趣*物品j和物品i的相似度]
        * */
        for (int i : Ru.keySet()) {
            Map<Integer, Double> Sik = Sort.descSortByValue(W.row(i));
            int count = 0;

            for (Map.Entry<Integer, Double> data : Sik.entrySet()) {
                int taskID = data.getKey();
                double similarity = data.getValue();
                if(Ru.containsKey(taskID)){
                    continue;
                } else {
                    // 如果任务不存在，则插入新的相似度值；否则，更新任务的相似度值
                    rank.compute(taskID, (k, v) -> (v == null) ? Ru.get(i) * similarity : v + Ru.get(i) * similarity);
                }

                count++;
                if(count==K){
                    break;
                }
            }
        }

        return Sort.descSortByValue(rank);
    }
}
