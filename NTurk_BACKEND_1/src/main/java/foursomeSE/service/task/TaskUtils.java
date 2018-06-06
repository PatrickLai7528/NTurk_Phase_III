package foursomeSE.service.task;

import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.Task;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;

public class TaskUtils {
     public static Task taskById(TaskJPA taskJPA, long id) {
         return taskJPA.findById(id)
                 .orElseThrow(() -> new MyObjectNotFoundException("task with id " + id + " is not found"));
     }

     public static Microtask mtByImg(MicrotaskJPA microtaskJPA, String imgName) {
         return microtaskJPA.findByImgName(imgName)
                 .orElseThrow(() -> new MyObjectNotFoundException("microtask with imgName " + imgName + " is not found."));
     }

     public static Microtask mtById(MicrotaskJPA microtaskJPA, long id) {
         return microtaskJPA.findById(id)
                 .orElseThrow(() -> new MyObjectNotFoundException("microtask with id " + id + " is not found."));
     }
}
