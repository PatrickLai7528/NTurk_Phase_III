package foursomeSE.util;

public interface MyConstants {
    int MANDATORY_TIME = 3;
    int MINIMUM_PEOPLE_TO_ENTER_REVIEW = 3;
    int K = 4;

    int NUM_OF_MICROTASK_PER_REQUEST = 5;
    int NUM_OF_INSPECTION_PER_REQUEST = 5;
    int TASK_DURATION = 15;
    int INSPECTION_DURATION = 15;
    int INSPECTION_PER_CONTRACT = 2; // 在annotationJPA里的一个方法也写这个了，如果不写就要传进去，所以就直接写2了

    double ACCEPTED_SUM = 9;
}
