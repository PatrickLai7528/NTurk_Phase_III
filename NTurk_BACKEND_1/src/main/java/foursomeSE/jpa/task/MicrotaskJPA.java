package foursomeSE.jpa.task;

import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
public interface MicrotaskJPA extends CrudRepository<Microtask, Long> {
    Optional<Microtask> findByImgName(String imgName);

    @Query(value = "select * from microtasks\n" +
            "where task_id = ?1\n" +
            "    and microtask_status = 0\n" +
            "    and parallel = 0\n" +
            "order by is_sample desc,\n" +
            "    iteration desc,\n" +
            "    ord asc",
            nativeQuery = true)
    List<Microtask> getMicroTasks(long taskId);

    @Query(value = "SELECT *\n" +
            "FROM microtasks\n" +
            "WHERE microtask_id IN (\n" +
            "  SELECT B.microtask_id\n" +
            "  FROM (-- 这里只是多求了一下这个最新的annotation_id已经被这种verify多少次\n" +
            "         SELECT\n" +
            "           A.microtask_id,\n" +
            "           A.parallel,\n" +
            "           (\n" +
            "             SELECT COUNT(*)\n" +
            "             FROM verification\n" +
            "             WHERE annotation_id = A.latest_annotation_id\n" +
            "                   AND verification_type = ?3 - 1\n" +
            "           ) AS have_done\n" +
            "         FROM (-- 这里只是多求了一下最新的annotation_id\n" +
            "                SELECT\n" +
            "                  m.microtask_id,\n" +
            "                  m.parallel,\n" +
            "                  (\n" +
            "                    SELECT annotation_id\n" +
            "                    FROM annotation a1\n" +
            "                    WHERE a1.microtask_id = m.microtask_id\n" +
            "                          AND NOT EXISTS(\n" +
            "                        SELECT *\n" +
            "                        FROM annotation a2\n" +
            "                        WHERE a2.microtask_id = m.microtask_id\n" +
            "                              AND a1.create_time < a2.create_time\n" +
            "                    )\n" +
            "                  ) AS latest_annotation_id\n" +
            "                FROM microtasks m\n" +
            "                WHERE task_id = ?1\n" +
            "                      AND microtask_status = ?3\n" +
            "                      AND is_sample = ALL (SELECT is_collecting\n" +
            "                                           FROM tasks\n" +
            "                                           WHERE task_id = ?1)\n" +
            "              ) AS A\n" +
            "         WHERE NOT EXISTS(SELECT *\n" +
            "                          FROM annotation a1\n" +
            "                          WHERE annotation_id = latest_annotation_id\n" +
            "                                AND username = ?2 OR EXISTS(\n" +
            "                                    SELECT *\n" +
            "                                    FROM verification\n" +
            "                                    WHERE verification.annotation_id = a1.annotation_id\n" +
            "                                          AND verification_type = ?3 - 1\n" +
            "                                          AND username = ?2))\n" +
            "       ) AS B\n" +
            "  WHERE B.parallel < ALL (SELECT 1 + 2 * is_collecting - have_done AS n\n" +
            "                          FROM tasks\n" +
            "                          WHERE task_id = ?1)\n" +
            ")\n" +
            "ORDER BY iteration DESC, ord ASC",
            nativeQuery = true)
    List<Microtask> getForVerification(long taskId, String username, int microtaskStatusOrd);

    @Query(value = "select * from microtasks\n" +
            "where microtask_id in (\n" +
            "    select microtask_id from annotation\n" +
            "    where annotation_id = ?1\n" +
            ")",
            nativeQuery = true)
    Microtask findByAnnotationId(long annotation_id);

    @Query(value = "select count(*) from verification\n" +
            "where verification_status = ?3 - 1\n" +
            "    and username = ?2\n" +
            "    and annotation_id in (\n" +
            "        select annotation_id from annotation\n" +
            "        where microtask_id in (\n" +
            "            select microtask_id from microtasks\n" +
            "            where task_id = ?1\n" +
            "        )\n" +
            "    )",
            nativeQuery = true)
    long countInspectionTimes(long taskId, String username, int microtaskStatus);

    @Query(value = "select * from microtasks\n" +
            "where task_id = ?1 and is_sample = 1"
            , nativeQuery = true)
    List<Microtask> getSampling(long taskId);

    @Query(value = "select * from microtasks\n" +
            "where task_id = ?1 and is_sample = 0\n" +
            "order by ord asc",
            nativeQuery = true)
    List<Microtask> getUnSampled(long taskId);

//    @Modifying(clearAutomatically = true)
//    @Query(value = "update microtasks\n" +
//            "set microtask_status = 4\n" +
//            "where microtask_status = 1 and last_request_time < ?1", nativeQuery = true)
//    void checkUnfinished(LocalDateTime nowMinusXMinutes);


    // 这个是为了
    @Query(value = "select * from microtasks\n" +
            "where task_id = ?1 and microtask_status <> 3",
            nativeQuery = true)
    List<Microtask> findByTaskIdNotPassed(long taskId);

    @Query(value = "select img_name from microtasks where task_id = ?1",
            nativeQuery = true)
    List<String> retrieveImgNames(long taskId);
}

/*

select * from microtasks
where task_id = ?1
    and microtask_status = 0
    and parallel = 0
order by is_sample desc,
    iteration desc,
    ord asc

// is_collecting是1，那么可以拿3个
// is_collectin是0，那么可以拿1个

select * from microtasks m
where task_id = ?1
    and microtask_status = ?3
    and is_sample = all (select is_collecting from tasks where task_id = ?1)
    and parallel < all (select 1 + 2 * is_collecting from tasks where task_id = ?1)
    and not exists (
        -- select * from ( -- 最新的那一个annotation（这个肯定有），不是：1他做的。2他评过
            select * from annotation a1
            where m.microtask_id = a1.microtask_id
                and not exists (
                    select * from annotation a2
                    where m.microtask_id = annotation.microtask_id
                        and a1.create_time < a2.create_time
                )
        -- ) as A
        -- where username = ?2 -- 1. 他做的
--             or exists ( -- 2. 他过评的同种类的关于这个annotation的verification
   --             select * from verification
     --           where verification.annotation_id = A.annotation_id
       --             and verification_status = ?3 - 1
         --           and username = ?2
           -- )
    )
order by iteration desc, ord asc

// 上面那个不行，好像是因为where里面的from里的subquery
SELECT *
FROM microtasks m
WHERE task_id = ?1
      AND microtask_status = ?3
      AND is_sample = ALL (SELECT is_collecting
                           FROM tasks
                           WHERE task_id = ?1)
      AND parallel < ALL (SELECT 1 + 2 * is_collecting
                          FROM tasks
                          WHERE task_id = ?1)
      AND NOT exists(
    SELECT *
    FROM annotation a1
    WHERE a1.microtask_id = m.microtask_id
          AND NOT exists(
        SELECT *
        FROM annotation a2
        WHERE a2.microtask_id = m.microtask_id
              AND a1.create_time < a2.create_time
    )
          AND (username = ?2 OR exists(
        SELECT *
        FROM verification
        WHERE verification.annotation_id = a1.annotation_id
              AND verification_type = ?3 - 1
              AND username = ?2
    ))
)
ORDER BY iteration DESC, ord ASC

// 上面那个不行，parallel没有考虑原来已经做了的
SELECT *
FROM microtasks
WHERE microtask_id IN (
  SELECT B.microtask_id
  FROM (-- 这里只是多求了一下这个最新的annotation_id已经被这种verify多少次
         SELECT
           A.microtask_id,
           A.parallel,
           (
             SELECT COUNT(*)
             FROM verification
             WHERE annotation_id = A.latest_annotation_id
                   AND verification_type = ?3 - 1
           ) AS have_done
         FROM (-- 这里只是多求了一下最新的annotation_id
                SELECT
                  m.microtask_id,
                  m.parallel,
                  (
                    SELECT annotation_id
                    FROM annotation a1
                    WHERE a1.microtask_id = m.microtask_id
                          AND NOT EXISTS(
                        SELECT *
                        FROM annotation a2
                        WHERE a2.microtask_id = m.microtask_id
                              AND a1.create_time < a2.create_time
                    )
                  ) AS latest_annotation_id
                FROM microtasks m
                WHERE task_id = ?1
                      AND microtask_status = ?3
                      AND is_sample = ALL (SELECT is_collecting
                                           FROM tasks
                                           WHERE task_id = ?1)
              ) AS A
         WHERE NOT EXISTS(SELECT *
                          FROM annotation a1
                          WHERE annotation_id = latest_annotation_id
                                AND username = ?2 OR EXISTS(
                                    SELECT *
                                    FROM verification
                                    WHERE verification.annotation_id = a1.annotation_id
                                          AND verification_type = ?3 - 1
                                          AND username = ?2))
       ) AS B
  WHERE B.parallel < ALL (SELECT 1 + 2 * is_collecting - have_done AS n
                          FROM tasks
                          WHERE task_id = ?1)
)
ORDER BY iteration DESC, ord ASC


select * from microtasks
where microtask_id in (
    select microtask_id from annotation
    wherer annotation_id = ?1
)



select count(*) from verification
where verification_status = ?3 - 1
    and username = ?2
    and annotation_id in (
        select annotation_id from annotation
        where microtask_id in (
            select microtask_id from microtasks
            where task_id = ?1
        )
    )

// getSampling
select * from microtasks
where task_id = ?1 and is_sample = 1

// getUnSampled
select * from microtasks
where task_id = ?1 and is_sample = 0
order by ord asc


select * from microtasks
where task_id = ?1
    and microtask_status <> 3 -- passed
    and microtask_status <> 2 -- unreviewed
    and microtask_status <> 1 -- unfinished
order by microtask_status desc,  -- prefer "failed" to "virgin"
    ord asc

update microtasks
set microtask_status = 4 -- failed
where microtask_status = 1 -- unfinished
    and last_request_time < ?1

// findByTaskIdNotPassed
select * from microtasks
where task_id = ?1 and microtask_status <> 3

// retrieveImgNames
select img_name from microtasks where task_id = ?1

*/
