package foursomeSE.jpa.tag;

import foursomeSE.entity.tag.TagAndWorker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface TagAndWorkerJPA extends CrudRepository<TagAndWorker, Long> {
    @Query(value = "SELECT tag_name\n" +
            "FROM tag_and_worker\n" +
            "WHERE username = ?1",
            nativeQuery = true)
    List<String> getWorkerTags(String username);
}

/*
SELECT tag_name
FROM tag_and_worker
WHERE username = ? 1
 */
