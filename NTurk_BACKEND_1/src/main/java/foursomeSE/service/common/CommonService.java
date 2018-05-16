package foursomeSE.service.common;

import java.util.List;
import java.util.function.Predicate;

public interface CommonService<D, S> {
    D getById(long id);

    D getOneBy(Predicate<S> p);

    List<D> getLotBy(Predicate<S> p);

    void add(D record);

    void update(D record);

    void delete(long id);
}
