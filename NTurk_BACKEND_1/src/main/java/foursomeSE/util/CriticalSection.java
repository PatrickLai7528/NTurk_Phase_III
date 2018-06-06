package foursomeSE.util;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CriticalSection {

    public static class Item{
        public LocalDateTime requestTime;
        public String username;
        public long annotationId;
    }

    // 这个会不会特别不巧出现并行问题啊...我也不知道，但先加这么一个synchronized吧。。
    // 但那也是正好15分钟提交这种情况吧。。不然。。应该。。吧。。不会。。出现。。吧
    public static List<Item> inspectRecords = Collections.synchronizedList(new LinkedList<>());
}
