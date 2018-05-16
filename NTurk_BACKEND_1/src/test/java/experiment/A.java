package experiment;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

class E extends RuntimeException {
}

public class A {
    static void a(Runnable runnable) {
        runnable.run();
    }

    public static void main(String[] args) {
//        try {
//            a(() -> {
//                throw new E();
//            });
//        } catch (E e) {
//            System.out.println("ok");
//        }

        Map<String, Integer> map = new HashMap<>();
        map.put("fdas", 134);
        map.put("fsda", 543);

        Gson gson = new Gson();
        System.out.println(gson.toJson(map));
    }
}
