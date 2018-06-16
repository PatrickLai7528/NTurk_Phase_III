package foursomeSE.schedule;

import foursomeSE.entity.task.Microtask;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.task.TaskUtils.mtById;

@Component
public class SchedulerTask implements MyConstants {
    private MicrotaskJPA microtaskJPA;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public SchedulerTask(MicrotaskJPA microtaskJPA) {
        this.microtaskJPA = microtaskJPA;
    }

    @Scheduled(fixedRate = 6000)
    public void reportCurrentTime() {
        System.out.println("现在时间：" + dateFormat.format(new Date()));

        check(CriticalSection.qualityVerificationRecords);
        check(CriticalSection.coverageVerificationRecords);
        check(CriticalSection.drawRecords);
    }


//    private void checkMicroTask() {
//        microtaskJPA.checkUnfinished(LocalDateTime.now().minusMinutes(TASK_DURATION));
//    }

    private void check(List<CriticalSection.Item> list) {
//        while (!CriticalSection.inspectRecords.isEmpty()) {
//            CriticalSection.Item item = CriticalSection.inspectRecords.get(0);
//            if (item.requestTime.isBefore(LocalDateTime.now().minusMinutes(INSPECTION_DURATION))) {
//                CriticalSection.Item i0 = CriticalSection.inspectRecords.get(0);
//                Annotation a = annotationJPA.findById(i0.microtaskId).orElseThrow(() -> new MyObjectNotFoundException(""));
//                a.setParallel(a.getParallel() - 1);
//                annotationJPA.save(a);
//
//                CriticalSection.inspectRecords.remove(0);
//            } else {
//                break;
//            }
//        }

        // 其实写成上面那样，就够了

        ArrayList<CriticalSection.Item> toBeDeleted = list.stream()
                .filter(item -> item.requestTime.isBefore(
                        LocalDateTime.now().minusMinutes(INSPECTION_DURATION)
                )).collect(Collectors.toCollection(ArrayList::new));

        toBeDeleted.forEach(item -> {
            Microtask mt = mtById(microtaskJPA, item.microtaskId);
            mt.setParallel(mt.getParallel() - 1);
            microtaskJPA.save(mt);

            list.remove(item);
        });
    }
}
