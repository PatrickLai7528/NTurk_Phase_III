package foursomeSE.entity.contract;

public enum ContractStatus {
    COMPLETED,
    IN_PROGRESS,
    VIRGIN,
    // Virgin就是没有人做过的task
    // 在contract里的contractStatus里不会出现，
    // 但是在CTask里会有…CTask里如果是给worker看的，virgin就表示新任务。
    // 如果是给requester看的，status就是null

    ABORT
}
