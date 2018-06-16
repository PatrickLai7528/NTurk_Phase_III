package foursomeSE.recommendation.tools;

import java.util.*;

public class Sort {
    /**
     * 对map的降序排列
     * 具体实现我也不是很懂
     * https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> descSortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
