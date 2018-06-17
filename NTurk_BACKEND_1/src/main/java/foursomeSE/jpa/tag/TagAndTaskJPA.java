package foursomeSE.jpa.tag;

import foursomeSE.entity.tag.TagAndTask;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface TagAndTaskJPA extends CrudRepository<TagAndTask, Long> {
    @Query(value = "SELECT tag_name\n" +
            "FROM tag_and_task\n" +
            "WHERE task_id = ?1",
            nativeQuery = true)
    List<String> getTaskTags(long taskId);
}

/*

SELECT tag_name
FROM tag_and_task
WHERE task_id = ?1
 */
