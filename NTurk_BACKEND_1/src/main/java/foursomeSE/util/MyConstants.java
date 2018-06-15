package foursomeSE.util;

import java.util.ArrayList;
import java.util.Arrays;

public interface MyConstants {
    int MANDATORY_TIME = 3;
    int MINIMUM_PEOPLE_TO_ENTER_REVIEW = 3;
    int K = 4;

    int NUM_OF_MICROTASK_PER_REQUEST = 5;
    int NUM_OF_INSPECTION_PER_REQUEST = 5;
    int TASK_DURATION = 15;
    int INSPECTION_DURATION = 15;
    int INSPECTION_PER_CONTRACT = 3; // 在annotationJPA里的一个方法也写这个了，如果不写就要传进去，所以就直接写2了

    int SAMPLE_SIZE = 15;
    int TRAP_FALL_TOLERANCE = 2;
    int GOLD_NUM = 10;

    double ACCEPTED_SUM = 9;
    double ANNOTATION_PERCENT = 0.6;
    double INSPECTION_PERCENT = 0.05;

    // 如果是3，就要拿第6个了（ord是5），所以后面是index + 3
    ArrayList<Integer> TRAPS = new ArrayList<>(Arrays.asList(1, 2, 3, 5, 8, 13, 21));
}
