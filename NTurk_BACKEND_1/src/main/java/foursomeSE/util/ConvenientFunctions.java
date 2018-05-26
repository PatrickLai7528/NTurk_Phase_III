package foursomeSE.util;

import foursomeSE.entity.user.MyUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ConvenientFunctions {
    public static<D, S> void setSameFields(D destination, S source) {
        Class<?> dClazz = destination.getClass();
        Method[] dMethods = dClazz.getMethods();

        Class<?> sClazz = source.getClass();
        Method[] sMethods = sClazz.getMethods();

        for (Method cMethod : dMethods) {
            if (cMethod.getName().startsWith("set")) {
                for (Method method : sMethods) {
                    if (method.getName().startsWith("get")
                            && method.getName().substring(3)
                            .equals(cMethod.getName().substring(3))) {
                        try {
                            cMethod.invoke(destination, method.invoke(source));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static <D, S> List<D> listConvert(List<S> source, Function<S, D> converter) {
        return source.stream()
                .map(converter)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T extends MyUser> int ExperiementPointDescend(T w1, T w2) {
        if (w2.getExperiencePoint() - w1.getExperiencePoint() < 0) {
            return -1;
        } else if (w2.getExperiencePoint() - w1.getExperiencePoint() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public static <T> Stream<T> iterableToStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

}
