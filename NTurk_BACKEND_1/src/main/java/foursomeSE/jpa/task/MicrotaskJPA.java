package foursomeSE.jpa.task;

import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public interface MicrotaskJPA extends CrudRepository<Microtask, Long> {
    @Query(value = "select * from microtasks\n" +
            "where task_id = ?1\n" +
            "  and microtask_status <> 3 -- passed\n" +
            "  and microtask_status <> 2 -- unreviewed\n" +
            "  and microtask_status <> 1 -- unfinished\n" +
            "order by microtask_status desc -- prefer \"failed\" to \"virgin\"",
            nativeQuery = true)
    List<Microtask> getMicroTasks(long taskId);

    @Modifying(clearAutomatically = true)
    @Query(value = "update microtasks\n" +
            "set microtask_status = 4\n" +
            "where microtask_status = 1 and last_request_time < ?1", nativeQuery = true)
    void checkUnfinished(LocalDateTime nowMinusXMinutes);

    Optional<Microtask> findByImgName(String imgName);

    @Query(value = "select * from microtasks\n" +
            "where task_id = ?1 and microtask status <> 3",
            nativeQuery = true)
    List<Microtask> findByTaskIdNotPassed(long taskId);
}

/*
select * from microtasks
where task_id = ?1
  and microtask_status <> 3 -- passed
  and microtask_status <> 2 -- unreviewed
  and microtask_status <> 1 -- unfinished
order by microtask_status desc -- prefer "failed" to "virgin"

update microtasks
set microtask_status = 4 -- failed
where microtask_status = 1 -- unfinished
    and last_request_time < ?1


select * from microtasks
where task_id = ?1 and microtask status <> 3
*/
