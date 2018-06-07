package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.Annotation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Transactional
public interface AnnotationJPA extends CrudRepository<Annotation, Long> {
    @Query(value = "select * from annotation\n" +
            "where username = ?2 and microtask_id in (\n" +
            "    select microtask_id from microtasks\n" +
            "    where task_id = ?1\n" +
            ")", nativeQuery = true)
    long countByTaskIdAndUsername(long taskId, String username);

    @Query(value = "SELECT\n" +
            "  id\n" +
            "FROM (\n" +
            "       SELECT\n" +
            "         annotation.annotation_id AS id,\n" +
            "         parallel,\n" +
            "         (\n" +
            "           SELECT count(*)\n" +
            "           FROM inspections\n" +
            "           WHERE annotation.annotation_id = inspections.annotation_id\n" +
            "         )                        AS have_done,\n" +
            "         (\n" +
            "           SELECT ord\n" +
            "           FROM microtasks\n" +
            "           WHERE annotation.microtask_id = microtasks.microtask_id\n" +
            "         )                        AS ord\n" +
            "       FROM annotation\n" +
            "       WHERE microtask_id IN (\n" +
            "         SELECT microtasks.microtask_id\n" +
            "         FROM microtasks\n" +
            "         WHERE microtask_status = 2 -- unreviewed\n" +
            "               AND microtask_id IN (\n" +
            "           SELECT microtask_id\n" +
            "           FROM microtasks\n" +
            "           WHERE task_id = ?1\n" +
            "         )\n" +
            "       ) AND annotation_status = 0 -- reviewable\n" +
            "             AND username <> ?2 -- 不是他做的\n" +
            "             AND annotation_id NOT IN (-- 不是他评过的\n" +
            "         SELECT annotation_id\n" +
            "         FROM inspections\n" +
            "         WHERE username = ?2\n" +
            "       )\n" +
            "     ) AS x\n" +
            "WHERE parallel < 2 - have_done\n" +
            "ORDER BY have_done DESC, ord ASC", nativeQuery = true)
    List<BigInteger> getIdsByTaskId(long taskId, String username);

}

/*
select * from annotation
where username = ?2 and microtask_id in (
    select microtask_id from microtasks
    where task_id = ?1
)

SELECT
  id
FROM (
       SELECT
         annotation.annotation_id AS id,
         parallel,
         (
           SELECT count(*)
           FROM inspections
           WHERE annotation.annotation_id = inspections.annotation_id
         )                        AS have_done,
         (
           SELECT ord
           FROM microtasks
           WHERE annotation.microtask_id = microtasks.microtask_id
         )                        AS ord
       FROM annotation
       WHERE microtask_id IN (
         SELECT microtasks.microtask_id
         FROM microtasks
         WHERE microtask_status = 2 -- unreviewed
               AND microtask_id IN (
           SELECT microtask_id
           FROM microtasks
           WHERE task_id = ?1
         )
       ) AND annotation_status = 0 -- reviewable
             AND username <> ?2 -- 不是他做的
             AND annotation_id NOT IN (-- 不是他评过的
         SELECT annotation_id
         FROM inspections
         WHERE username = ?2
       )
     ) AS x
WHERE parallel < 2 - have_done
ORDER BY have_done DESC, ord ASC




 */