package foursomeSE.util;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.annotation.SegmentAnnotation;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;

import java.util.ArrayList;
import java.util.List;

public class DataKeeper {
    private static List<Contract> contracts;
    private static List<Task> tasks;

    private static List<FrameAnnotation> frameAnnotations;
    private static List<GeneralAnnotation> generalAnnotations;
    private static List<SegmentAnnotation> segmentAnnotations;

    private static List<Worker> workers;
    private static List<Requester> requesters;

    public static void stashAll() { // TODO bad smell，文件读写应该和service做在一处的
        contracts = FileUtil.readAll(Contract[].class, "contract");
        tasks = FileUtil.readAll(Task[].class, "task");

        frameAnnotations = FileUtil.readAll(FrameAnnotation[].class, "frame_annotation");
        generalAnnotations = FileUtil.readAll(GeneralAnnotation[].class, "general_annotation");
        segmentAnnotations = FileUtil.readAll(SegmentAnnotation[].class, "segment_annotation");

        workers = FileUtil.readAll(Worker[].class, "worker");
        requesters = FileUtil.readAll(Requester[].class, "requester");

        clearAll();
    }

    public static void reclaimAll() {
        FileUtil.saveAll(contracts, "contract");
        FileUtil.saveAll(tasks, "task");

        FileUtil.saveAll(frameAnnotations, "frame_annotation");
        FileUtil.saveAll(generalAnnotations, "general_annotation");
        FileUtil.saveAll(segmentAnnotations, "segment_annotation");

        FileUtil.saveAll(workers, "worker");
        FileUtil.saveAll(requesters, "requester");
    }

    private static void clearAll() {
        FileUtil.saveAll(new ArrayList<>(), "contract");
        FileUtil.saveAll(new ArrayList<>(), "task");

        FileUtil.saveAll(new ArrayList<>(), "frame_annotation");
        FileUtil.saveAll(new ArrayList<>(), "general_annotation");
        FileUtil.saveAll(new ArrayList<>(), "segment_annotation");

        FileUtil.saveAll(new ArrayList<>(), "worker");
        FileUtil.saveAll(new ArrayList<>(), "requester");
    }
}
