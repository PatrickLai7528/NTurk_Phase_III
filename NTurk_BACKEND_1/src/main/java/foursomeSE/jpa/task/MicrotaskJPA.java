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
            "order by is_collecting desc,\n" +
            "    iteration desc,\n" +
            "    ord asc",
            nativeQuery = true)
    List<Microtask> getMicroTasks(long taskId);

    @Query(value = "select * from microtasks\n" +
            "where task_id = ?1\n" +
            "    and microtask_status = ?3\n" +
            "    and is_sample = all (select is_collecting from tasks where task_id = ?1)\n" +
            "    and parallel < all (select 1 + 2 * is_collecting from tasks where task_id = ?1)\n" +
            "    and not exists (\n" +
            "        select * from ( -- 最新的那一个annotation（这个肯定有），不是：1他做的。2他评过\n" +
            "            select * from annotation a1\n" +
            "            where microtasks.microtask_id = a1.microtask_id\n" +
            "                and not exists (\n" +
            "                    select * from annotation a2\n" +
            "                    where microtasks.microtask_id = annotation.microtask_id\n" +
            "                        and a1.create_time < a2.create_time\n" +
            "                )\n" +
            "        ) as A\n" +
            "        where username = ?2 -- 1. 他做的\n" +
            "            or exists ( -- 2. 他过评的同种类的关于这个annotation的verification\n" +
            "                select * from verification\n" +
            "                where verification.annotation_id = A.annotation_id\n" +
            "                    and verification_status = ?3 - 1\n" +
            "                    and username = ?2\n" +
            "            )\n" +
            "    )\n" +
            "order by iteration desc, ord asc",
            nativeQuery = true)
    List<Microtask> getForVerification(long taskId, String username, int microtaskStatusOrd);

    @Query(value = "select * from microtasks\n" +
            "where microtask_id in (\n" +
            "    select microtask_id from annotation\n" +
            "    wherer annotation_id = ?1\n" +
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
order by is_collecting desc,
    iteration desc,
    ord asc

// is_collecting是1，那么可以拿3个
// is_collectin是0，那么可以拿1个

select * from microtasks
where task_id = ?1
    and microtask_status = ?3
    and is_sample = all (select is_collecting from tasks where task_id = ?1)
    and parallel < all (select 1 + 2 * is_collecting from tasks where task_id = ?1)
    and not exists (
        select * from ( -- 最新的那一个annotation（这个肯定有），不是：1他做的。2他评过
            select * from annotation a1
            where microtasks.microtask_id = a1.microtask_id
                and not exists (
                    select * from annotation a2
                    where microtasks.microtask_id = annotation.microtask_id
                        and a1.create_time < a2.create_time
                )
        ) as A
        where username = ?2 -- 1. 他做的
            or exists ( -- 2. 他过评的同种类的关于这个annotation的verification
                select * from verification
                where verification.annotation_id = A.annotation_id
                    and verification_status = ?3 - 1
                    and username = ?2
            )
    )
order by iteration desc, ord asc


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
