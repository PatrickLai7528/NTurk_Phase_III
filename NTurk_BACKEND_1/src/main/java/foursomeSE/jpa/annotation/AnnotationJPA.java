package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDateTime;
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


    @Query(value = "select * from annotation\n" +
            "where microtask_id in (\n" +
            "    select microtask_id from microtasks\n" +
            "    where img_name = ?1\n" +
            ") and create_time = all (\n" +
            "    select max(create_time) from annotation\n" +
            ")", nativeQuery = true)
    Annotation findLatestByImgName(String imgName);

    @Query(value = "select\n" +
            "    id\n" +
            "from (\n" +
            "    select \n" +
            "        annotation.annotation_id,\n" +
            "        annotation.annotation_status, -- 这个好像可以按这个来排 \n" +
            "        annotation.iteration,\n" +
            "        (\n" +
            "            select sum(rate) from verification \n" +
            "            where verification.annotation_id = annotation.annotation_id\n" +
            "                and verification_type = ?2\n" +
            "        ) as rate_sum,\n" +
            "    from annotation\n" +
            "    where microtask_id = ?1\n" +
            ") as A\n" +
            "where rate_sum = 3 or rate_sum = 0\n" +
            "order by rate_sum asc, iteration desc",
            nativeQuery = true)
    List<BigInteger> getSample(long microtaskId, int verificationTypeOrd);

}

/*
select * from (
    select * from annotation
    where microtask_id in (
        select microtask_id from microtasks
        where img_name = ?1
    )
) as a
where  create_time = all (
    select max(create_time) from a
)

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




// getSample: long microtaskId, int verificationTypeOrd
select
    id
from (
    select
        annotation.annotation_id,
        annotation.annotation_status, -- 这个好像可以按这个来排
        annotation.iteration,
        (
            select sum(rate) from verification
            where verification.annotation_id = annotation.annotation_id
                and verification_type = ?2
        ) as rate_sum,
    from annotation
    where microtask_id = ?1
) as A
where rate_sum = 3 or rate_sum = 0
order by rate_sum asc, iteration desc

 */