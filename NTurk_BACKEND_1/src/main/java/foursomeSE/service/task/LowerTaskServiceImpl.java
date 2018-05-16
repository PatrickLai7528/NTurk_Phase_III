package foursomeSE.service.task;

import foursomeSE.entity.task.Task;
import foursomeSE.service.common.CommonCongruentServiceImpl;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class LowerTaskServiceImpl
        extends CommonCongruentServiceImpl<Task>
        implements LowerTaskService {
    @Override
    public boolean isTaskBelongTo(long taskId, long requesterId) {
        return getById(taskId).getRequesterId() == requesterId;
    }

    @Override
    public String getTableName() {
        return "task";
    }

    @Override
    public Class<Task[]> getTArrayType() {
        return Task[].class;
    }

    @Override
    public Function<Task, Long> getIdFunction() {
        return Task::getTaskId;
    }

    @Override
    public Consumer<Task> setIdFunction(long newId) {
        return t -> t.setTaskId(newId);
    }

}
