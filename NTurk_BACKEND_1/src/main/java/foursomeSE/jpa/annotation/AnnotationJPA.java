package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface AnnotationJPA extends CrudRepository<Annotation, Long> {
//    @Query(value = "select * from annotation\n" +
//            "where username = ?2 and microtask_id in (\n" +
//            "    select microtask_id from microtasks\n" +
//            "    where task_id = ?1\n" +
//            ")", nativeQuery = true)
//    long countByTaskIdAndUsername(long taskId, String username);

//    @Query(value = "SELECT\n" +
//            "  id\n" +
//            "FROM (\n" +
//            "       SELECT\n" +
//            "         annotation.annotation_id AS id,\n" +
//            "         parallel,\n" +
//            "         (\n" +
//            "           SELECT count(*)\n" +
//            "           FROM inspections\n" +
//            "           WHERE annotation.annotation_id = inspections.annotation_id\n" +
//            "         )                        AS have_done,\n" +
//            "         (\n" +
//            "           SELECT ord\n" +
//            "           FROM microtasks\n" +
//            "           WHERE annotation.microtask_id = microtasks.microtask_id\n" +
//            "         )                        AS ord\n" +
//            "       FROM annotation\n" +
//            "       WHERE microtask_id IN (\n" +
//            "         SELECT microtasks.microtask_id\n" +
//            "         FROM microtasks\n" +
//            "         WHERE microtask_status = 2 -- unreviewed\n" +
//            "               AND microtask_id IN (\n" +
//            "           SELECT microtask_id\n" +
//            "           FROM microtasks\n" +
//            "           WHERE task_id = ?1\n" +
//            "         )\n" +
//            "       ) AND annotation_status = 0 -- reviewable\n" +
//            "             AND username <> ?2 -- 不是他做的\n" +
//            "             AND annotation_id NOT IN (-- 不是他评过的\n" +
//            "         SELECT annotation_id\n" +
//            "         FROM inspections\n" +
//            "         WHERE username = ?2\n" +
//            "       )\n" +
//            "     ) AS x\n" +
//            "WHERE parallel < 2 - have_done\n" +
//            "ORDER BY have_done DESC, ord ASC", nativeQuery = true)
//    List<BigInteger> getIdsByTaskId(long taskId, String username);


    //    @Modifying(clearAutomatically = true)
    @Query(value = "SELECT annotation_id\n" +
            "FROM annotation\n" +
            "WHERE microtask_id IN (\n" +
            "  SELECT microtask_id\n" +
            "  FROM microtasks\n" +
            "  WHERE img_name = ?1\n" +
            ") AND create_time = ALL (SELECT max(create_time)\n" +
            "                         FROM annotation\n" +
            "                         WHERE microtask_id IN (\n" +
            "                           SELECT microtask_id\n" +
            "                           FROM microtasks\n" +
            "                           WHERE img_name = ?1\n" +
            "                         ))", nativeQuery = true)
    BigInteger findLatestByImgName(String imgName);

    @Query(value = "SELECT\n" +
            "  a1.annotation_id,\n" +
            "  a1.annotation_status\n" +
            "FROM annotation a1\n" +
            "WHERE create_time < ?2\n" +
            "      AND microtask_id = ?1\n" +
            "      AND NOT exists(SELECT *\n" +
            "                     FROM annotation a2\n" +
            "                     WHERE a2.create_time < ?2\n" +
            "                           AND a1.microtask_id = ?1\n" +
            "                           AND a2.create_time > a1.create_time)",
            nativeQuery = true)
    Object[] findLatestBefore(long microtaskId, LocalDateTime localDateTime);

    @Query(value = "SELECT annotation_id\n" +
            "FROM annotation\n" +
            "WHERE microtask_id IN (SELECT microtasks.microtask_id\n" +
            "                       FROM microtasks\n" +
            "                       WHERE task_id = ? 1)\n" +
            "      AND create_time > ?2 AND create_time < ?3",
            nativeQuery = true)
    List<BigInteger> findAidsBetween(long taskId, LocalDateTime floor, LocalDateTime roof);

    @Query(value = "SELECT\n" +
            "  annotation_id\n" +
            "FROM (\n" +
            "       SELECT\n" +
            "         annotation.annotation_id,\n" +
            "         annotation.annotation_status, -- 这个好像可以按这个来排\n" +
            "         annotation.iteration,\n" +
            "         (\n" +
            "           SELECT sum(rate) FROM verification\n" +
            "           WHERE verification.annotation_id = annotation.annotation_id\n" +
            "                 AND verification_type = ?2\n" +
            "         ) AS rate_sum\n" +
            "       FROM annotation\n" +
            "       WHERE microtask_id = ?1\n" +
            "     ) AS A\n" +
            "WHERE rate_sum = 3 OR rate_sum = 0\n" +
            "ORDER BY rate_sum ASC, iteration DESC",
            nativeQuery = true)
    List<BigInteger> getSample(long microtaskId, int verificationTypeOrd);

}

/*
// findLatestByImgName
// 这是错的，用不了
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

SELECT annotation_id
FROM annotation
WHERE microtask_id IN (
  SELECT microtask_id
  FROM microtasks
  WHERE img_name = ?1
) AND create_time = ALL (SELECT max(create_time)
                         FROM annotation
                         WHERE microtask_id IN (
                           SELECT microtask_id
                           FROM microtasks
                           WHERE img_name = ?1
                         ))

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
  annotation_id
from (
       select
         annotation.annotation_id,
         annotation.annotation_status, -- 这个好像可以按这个来排
         annotation.iteration,
         (
           select sum(rate) from verification
           where verification.annotation_id = annotation.annotation_id
                 and verification_type = ?2
         ) as rate_sum
       from annotation
       where microtask_id = ?1
     ) as A
where rate_sum = 3 or rate_sum = 0
order by rate_sum asc, iteration desc

// findLatestBefore
SELECT
  a1.annotation_id,
  a1.annotation_status
FROM annotation a1
WHERE create_time < ?2
      AND microtask_id = ?1
      AND NOT exists(SELECT *
                     FROM annotation a2
                     WHERE a2.create_time < ?2
                           AND a1.microtask_id = ?1
                           AND a2.create_time > a1.create_time)

// findAidsBetween
SELECT annotation_id
FROM annotation
WHERE microtask_id IN (SELECT microtasks.microtask_id
                       FROM microtasks
                       WHERE task_id = ? 1)
      AND create_time > ?2 AND create_time < ?3
 */