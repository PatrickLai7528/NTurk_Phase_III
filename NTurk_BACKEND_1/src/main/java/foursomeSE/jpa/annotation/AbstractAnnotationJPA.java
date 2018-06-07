package foursomeSE.jpa.annotation;


import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.FrameAnnotation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface AbstractAnnotationJPA<T extends Annotation> extends CrudRepository<T, Long> {
//    Optional<T> findByContractIdAndImgName(long contractId, String imgName);
    /**
     * 测试用
     * */
    List<T> findByMicrotaskId(long microtaskId);
}
