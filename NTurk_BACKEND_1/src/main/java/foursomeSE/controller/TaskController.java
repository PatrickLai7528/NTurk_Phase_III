package foursomeSE.controller;

import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.task.RTask;
import foursomeSE.entity.statistics.TaskGrowth;
import foursomeSE.entity.statistics.TaskParticipation;
import foursomeSE.entity.statistics.TaskStatusData;
import foursomeSE.entity.task.CTask;
import foursomeSE.entity.statistics.TaskNum;
import foursomeSE.error.MyErrorType;
import foursomeSE.error.MyNotValidException;
import foursomeSE.security.JwtUtil;
import foursomeSE.service.task.UpperTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {
    private UpperTaskService taskService;

    /**
     * Dependency inject
     */
    @Autowired
    @Qualifier("upperTaskServiceImpl")
    public void setTaskService(UpperTaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * RequestMapping
     */
    @RequestMapping(value = "/newTasks",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<CTask>> getNewTasks(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<CTask> tasks = taskService.getNewTasks(username);

        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/workerTasks",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<CTask>> getWorkerTasks(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<CTask> tasks = taskService.getWorkerTasks(username);

        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/requesterTasks",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<List<CTask>> getRequesterTasks(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<CTask> tasks = taskService.getRequesterTasks(username);

        // TODO 如果是requester看，返回的东西里面应该包含所有的图片

        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/task",
            method = RequestMethod.POST)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<?> addTask(@RequestHeader("Authorization") String token,
                                     @RequestBody RTask task) {
        String username = JwtUtil.getUsernameFromToken(token);
        try {
            taskService.add(task, username);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (MyNotValidException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/task/{taskId}",
            method = RequestMethod.GET)
    public ResponseEntity<?> enterTask(@RequestHeader("Authorization") String token, @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        EnterResponse enterResponse = taskService.enterTask(taskId, username);
        if (enterResponse.getImgNames().isEmpty()) {
            return new ResponseEntity<>(new MyErrorType("no microtasks to return"), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(enterResponse, HttpStatus.OK);
    }



    /**
     * statistics
     */

    @RequestMapping(value = "/admin/overview/taskNum",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskNum>> getTaskNum() {
        return new ResponseEntity<>(taskService.getTaskNums(), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/task/taskState",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskStatusData>> getTaskStatus() {
        List<TaskStatusData> result = taskService.getTaskStatus();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/task/taskGrowth",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskGrowth>> getTaskGrowth() {
        List<TaskGrowth> result = taskService.getTaskGrowth();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/task/taskParticipation",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskParticipation>> getTaskParticipation() {
        return new ResponseEntity<>(taskService.getTaskParticipation(), HttpStatus.OK);
    }
}
