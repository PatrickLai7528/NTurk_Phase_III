package foursomeSE.service.task;

import foursomeSE.entity.task.Task;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.task.TaskJPA;

public class TaskUtils {
     public static Task taskById(TaskJPA taskJPA, long id) {
         return taskJPA.findById(id)
                 .orElseThrow(() -> new MyObjectNotFoundException("task with id " + id + " is not found"));
     }
}
