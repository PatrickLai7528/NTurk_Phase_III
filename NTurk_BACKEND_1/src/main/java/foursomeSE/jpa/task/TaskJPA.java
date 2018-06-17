package foursomeSE.jpa.task;

import foursomeSE.entity.contract.ContractStatus;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.task.TaskStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public interface TaskJPA extends CrudRepository<Task, Long> {
    List<Task> findByRequesterId(long id);

//    @Query(value = "select * from tasks where task_status = ?1 and end_time < ?2",
//            nativeQuery = true)
//    List<Task> findJustFinishedTasks(TaskStatus taskStatus, LocalDateTime now);
    // 但是这个还需要localDateTimeNow，这个有点怪

//    List<Task> findByTaskStatusAndEndTimeBefore(TaskStatus taskStatus, LocalDateTime now);
//    List<Task> findByTaskStatusAndDdlBefore(TaskStatus taskStatus, LocalDateTime now);

    @Query(value = "SELECT * FROM tasks\n" +
            "WHERE task_id IN (\n" +
            "    SELECT task_id FROM microtasks\n" +
            "    WHERE microtask_id IN (\n" +
            "        SELECT microtask_id FROM annotation\n" +
            "        WHERE annotation_id = ?1\n" +
            "    )\n" +
            ")",
            nativeQuery = true)
    Task findByAnnotationId(long id);


    @Query(value = "SELECT * FROM tasks WHERE task_status = ?1", nativeQuery = true)
    List<Task> findByTaskStatus(int taskStatus);

    @Query(value = "SELECT *\n" +
            "FROM tasks\n" +
            "WHERE task_id IN (SELECT task_id\n" +
            "                  FROM contracts\n" +
            "                  WHERE worker_id IN (SELECT worker_id\n" +
            "                                      FROM workers\n" +
            "                                      WHERE email_address = ?1))",
            nativeQuery = true)
    List<Task> getByUsername(String username);




//    @Query(value =
//            "select * from tasks\n" +
//            "where task_status = 1 and task_id in (\n" +
//            "    select task_id from contracts\n" +
//            "    where contract_status = 0 and worker_id in (\n" +
//            "        select id from workers\n" +
//            "        where email_address = ?1\n" +
//            "    )\n" +
//            ")",
//            nativeQuery = true)
//    List<Task> findWorkerInspectionTasks(String username);

    /**
     * 这个主要是创建时用，先存，然后再根据createTime拿，这样才知道id，也只有创建时才知道createTime
     * 其他Entity类似情形下也可能采用这个方法
     * <p>
     * 但是不知道为什么只能用craeteTime.minusNanos(1)这样调这个方法而不能直接判断相等
     * 呃，-1还有时不行？？
     * 呃，-1000还有时不行。。
     */
    Task findByCreateTimeAfter(LocalDateTime createTime);

    @Query(value = "SELECT * FROM hibernate_sequence", nativeQuery = true)
    List<BigInteger> temp();

    /**
     * 这个方法只是测试用的…感觉这样不好……
     */
    Task findByTaskName(String name);
}


/*
select * from tasks
where task_status = ?2 and task_id in (
    select task_id from contracts
    where contract_status = ?3 and worker_id in (
        select id from workers
        where email_address = ?1
    )
)

select * from tasks
where task_status = 1 and task_id in (
    select task_id from contracts
    where contract_status = 0 and worker_id in (
        select id from workers
        where email_address = 'worker2@ex.com'
    )
)


select * from tasks
where task_id in (
    select task_id from microtasks
    where microtask_id in (
        select microtask_id from annotation
        where annotation_id = ?1
    )
)

SELECT *
FROM tasks
WHERE task_id IN (SELECT task_id
                  FROM contracts
                  WHERE worker_id IN (SELECT worker_id
                                      FROM workers
                                      WHERE email_address = ?1))
*/
