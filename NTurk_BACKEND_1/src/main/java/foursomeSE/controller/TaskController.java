package foursomeSE.controller;

import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.statistics.*;
import foursomeSE.entity.tag.CTag;
import foursomeSE.entity.task.CTask;
import foursomeSE.entity.task.Task;
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

    @RequestMapping(value = "/get/taskId/{taskId}",
            method = RequestMethod.GET)
    public ResponseEntity<?> getTaskById(@RequestHeader("Authorization") String token,
                                         @PathVariable("taskId") long taskId) {
        CTask t = taskService.getById(taskId);
        return new ResponseEntity<Object>(t, HttpStatus.OK);
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
                                     @RequestBody Task task) {
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
    public ResponseEntity<?> enterTask(@RequestHeader("Authorization") String token,
                                       @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        EnterResponse enterResponse = taskService.enterTask(taskId, username);
        if (enterResponse.getImgNames().isEmpty()) {
            return new ResponseEntity<>(new MyErrorType("no microtasks to return"), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(enterResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/requesterTasks/taskChart",
            method = RequestMethod.POST)
    public ResponseEntity<?> taskChart(@RequestHeader("Authorization") String token,
                                       @RequestBody int taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<PHItem> ph = taskService.PHChart(taskId, username);
        return new ResponseEntity<Object>(ph, HttpStatus.OK);
    }

    @RequestMapping(value = "/userProfile/requester/charts/",
            method = RequestMethod.POST)
    public ResponseEntity<?> commitChart(@RequestHeader("Authorization") String token,
                                         @RequestBody int taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<CommitItem> cis = taskService.commitChart(taskId, username);
        return new ResponseEntity<Object>(cis, HttpStatus.OK);
    }

    @RequestMapping(value = "/userProfile/worker/charts/point",
            method = RequestMethod.GET)
    public ResponseEntity<?> accuracyChart(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        Accuracy accuracy = taskService.accuraccyChart(username);
        return new ResponseEntity<Object>(accuracy, HttpStatus.OK);
    }

    @RequestMapping(value = "/userProfile/worker/charts/active",
            method = RequestMethod.GET)
    public ResponseEntity<?> heatChart(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<Heat> heatChart = taskService.heatChart(username);
        return new ResponseEntity<Object>(heatChart, HttpStatus.OK);
    }


    @RequestMapping(value = "/systemTags",
            method = RequestMethod.GET)
    public ResponseEntity<?> getTags() {
        List<CTag> result = taskService.getSystemTags();
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/recommendTasks",
    method = RequestMethod.GET)
    public ResponseEntity<?> recommend(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<CTask> result = taskService.recommend(username);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
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
