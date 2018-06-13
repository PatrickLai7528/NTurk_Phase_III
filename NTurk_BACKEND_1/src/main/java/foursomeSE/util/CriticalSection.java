package foursomeSE.util;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CriticalSection {

    public static class Item{
        public LocalDateTime requestTime;
        public String username;
        public long microtaskId;
    }

    // 这个会不会特别不巧出现并行问题啊...我也不知道，但先加这么一个synchronized吧。。
    // 但那也是正好15分钟提交这种情况吧。。不然。。应该。。吧。。不会。。出现。。吧
//    public static List<Item> inspectRecords = Collections.synchronizedList(new LinkedList<>());
    public static List<Item> qualityVerificationRecords = Collections.synchronizedList(new LinkedList<>());
    public static List<Item> coverageVerificationRecords = Collections.synchronizedList(new LinkedList<>());
    public static List<Item> drawRecords = Collections.synchronizedList(new LinkedList<>());

    // 如果有人先做一个任务的qualityVerification，然后做coverageVerification，那么就可以为前面的续命了。。

    public static void clearAll() {
        qualityVerificationRecords.clear();
        coverageVerificationRecords.clear();
        drawRecords.clear();
    }
}

