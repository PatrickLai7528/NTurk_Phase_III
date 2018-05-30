package foursomeSE.jpa.task;

import foursomeSE.entity.task.Task;
import foursomeSE.entity.task.TaskStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
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

    List<Task> findByTaskStatusAndEndTimeBefore(TaskStatus taskStatus, LocalDateTime now);

    // TODO 突然发现上面那个endTimeBefore好像不需要，反正有轮询endTime来改taskStatus
    List<Task> findByTaskStatus(TaskStatus taskStatus);

    /**
     * 这个方法只是测试用…感觉这样很不好……
     * */
    Task findByTaskName(String name);
}
