package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.service.common.CommonCongruentServiceImpl;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractLowerAnnotationServiceImpl<T extends Annotation> extends CommonCongruentServiceImpl<T> {
    @Override
    public Function<T, Long> getIdFunction() {
        return Annotation::getAnnotationId;
    }

    @Override
    public Consumer<T> setIdFunction(long newId) {
        return a -> a.setAnnotationId(newId);
    }

}
