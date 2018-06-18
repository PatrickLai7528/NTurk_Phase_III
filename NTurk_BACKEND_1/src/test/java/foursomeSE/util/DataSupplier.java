package foursomeSE.util;

import foursomeSE.entity.task.*;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.task.UpperTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static foursomeSE.service.user.UserUtils.userByUsername;
import static foursomeSE.util.ConvenientFunctions.iterableToList;
import static org.junit.Assert.assertEquals;

@Service
public class DataSupplier extends WithTheAutowired {
    public final static String[] tagCandidates = {"花", "草", "⾍", "鱼", "兽"};


    public void mockWorkers() {
        int ptr = 0;

        List<Worker> result = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Worker w = new Worker(); // 没iconName
//            w.setCreateTime(LocalDateTime.now().minusDays(100));
            w.setEmailAddress("worker" + i + "@ex.com");
            w.setNickname("三刀" + i);
            w.setPassword(new BCryptPasswordEncoder().encode("123456"));
            w.setIconName("hamburger1.png");
            w.setCredit(0);
            w.setExperiencePoint(101);
            w.setProvince("北京");

            w.userTags = Arrays.asList(tagCandidates[ptr], tagCandidates[(ptr + 1) % 5]);

            result.add(w);
            workerService.add(w);

            ptr = (ptr + 1) % 5;
        }
//        workerJPA.saveAll(result);
    }

    public void mockRequesters() {
        List<Requester> result = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Requester r = new Requester();
            r.setCreateTime(LocalDateTime.now());
            r.setEmailAddress("requester" + i + "@ex.com");
            r.setNickname("岩雪");
            r.setPassword(new BCryptPasswordEncoder().encode("requester" + i));
            r.setIconName("hamburger1.png");
            r.setCredit(10000);
            r.setExperiencePoint(13500);
            r.setProvince("山西");

            result.add(r);
        }
        requesterJPA.saveAll(result);
    }

    public void mockTasks() {
        Task task = new Task();
        task.setTaskName("task1");
        task.setTaskDescription("这是改接口以后的测试");
        task.setWorkerRequirement(WorkerRequirement.NONE);
        task.setTaskCategory(TaskCategory.GENERAL);
        task.setRewardPerMicrotask(10);

        ArrayList<String> imgs = IntStream.rangeClosed(101, 129).mapToObj(i -> i + ".jpg").collect(Collectors.toCollection(ArrayList::new));
        task.setImgNames(imgs);

        taskService.add(task, "requester1@ex.com");

        Task t = new Task();
        t.setTaskName("task2");
        t.setTaskDescription("框");
        t.setWorkerRequirement(WorkerRequirement.NONE);
        t.setTaskCategory(TaskCategory.FRAME);
        t.setRewardPerMicrotask(0);

        ArrayList<String> imgs1 = IntStream.rangeClosed(1, 40).mapToObj(i -> i + ".jpg").collect(Collectors.toCollection(ArrayList::new));
        t.setImgNames(imgs1);

        ArrayList<String> tags = new ArrayList<>(Arrays.asList("花,草,⾍,鱼".split(",")));
        t.setTaskTags(tags);

        taskService.add(t, "requester1@ex.com");
    }
}
