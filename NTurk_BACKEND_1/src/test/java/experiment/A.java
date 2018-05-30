package experiment;

import com.google.gson.Gson;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

class E extends RuntimeException {
}

class CL<T> implements Iterable<T> {
    private int currentPos = 0;
    private List<T> lst;

    public CL(List<T> lst) {
        this.lst = lst;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < lst.size();
            }

            @Override
            public T next() {
                i++;
                return lst.get((currentPos++) % lst.size());
            }
        };
    }
}

public class A {
    public static void main(String[] args) {
        CL<String> cl = new CL<>(Arrays.asList("1", "2", "a", "b"));
        int i = 0;
        for (String c: cl) {
            System.out.println(c);
            if (i == 2) {
                break;
            }
            i++;
        }

        System.out.println();

        for (String c: cl) {
            System.out.println(c);
        }
    }
}
