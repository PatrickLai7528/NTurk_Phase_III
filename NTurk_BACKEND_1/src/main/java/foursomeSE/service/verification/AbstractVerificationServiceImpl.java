package foursomeSE.service.verification;

import foursomeSE.entity.BlacklistItem;
import foursomeSE.entity.Gold;
import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.communicate.Warning;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.task.Task;
import foursomeSE.entity.task.TaskStatus;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.Verification;
import foursomeSE.entity.verification.VerificationType;
import foursomeSE.error.MyFailTestException;
import foursomeSE.error.MyNotValidException;
import foursomeSE.jpa.BlacklistJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.gold.GoldJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.verification.VerificationJPA;
import foursomeSE.service.contract.LowerContractService;
import foursomeSE.util.CriticalSection;
import foursomeSE.util.MyConstants;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.service.annotation.AnnotationUtils.anttById;
import static foursomeSE.service.task.TaskUtils.taskById;

public abstract class AbstractVerificationServiceImpl implements VerificationService, MyConstants {

    protected MicrotaskJPA microtaskJPA;
    protected TaskJPA taskJPA;
    protected GoldJPA goldJPA;
    protected AnnotationJPA annotationJPA;
    protected VerificationJPA verificationJPA;
    protected BlacklistJPA blacklistJPA;
    protected LowerContractService lowerContractService;


    protected String username;
    protected Microtask microtask;
    protected Annotation annotation;
    protected Task task;

    public AbstractVerificationServiceImpl(MicrotaskJPA microtaskJPA, TaskJPA taskJPA, GoldJPA goldJPA, AnnotationJPA annotationJPA, VerificationJPA verificationJPA, BlacklistJPA blacklistJPA, LowerContractService lowerContractService) {
        this.microtaskJPA = microtaskJPA;
        this.taskJPA = taskJPA;
        this.goldJPA = goldJPA;
        this.annotationJPA = annotationJPA;
        this.verificationJPA = verificationJPA;
        this.blacklistJPA = blacklistJPA;
        this.lowerContractService = lowerContractService;
    }

    @Override
    public EnterResponse enterVerification(long taskId, String username) {
        this.username = username;

        List<Microtask> candidates = microtaskJPA.getForVerification(taskId, username, getPriorMtStt().ordinal());
        if (candidates.size() > NUM_OF_INSPECTION_PER_REQUEST) {
            candidates = candidates.subList(0, NUM_OF_INSPECTION_PER_REQUEST);
        }

        List<String> golds = new ArrayList<>();

        this.task = taskById(taskJPA, taskId);

        if (task.getIsCollecting() == 0) {
            long haveEnter;//  = microtaskJPA.countInspectionTimes(taskId, username, getPriorMtStt().ordinal());
            BlacklistItem bli = blacklistJPA.findByUsernameAndTaskIdAndVerificationType(username, taskId, getVType());
            if (bli == null) {
                bli = new BlacklistItem();
                bli.setTaskId(taskId);
                bli.setUsername(username);
                bli.setWrong(0);
                bli.setHaveEnter(0);
                bli.setVerificationType(getVType());

                blacklistJPA.save(bli);
            }

            if (bli.getWrong() >= TRAP_FALL_TOLERANCE) {
                EnterResponse response = new EnterResponse();
                response.setImgNames(null);
                return response;
            }

            haveEnter = bli.getHaveEnter();

            if (haveEnter + 1 == TRAPS.get(0)) { // 感觉应该直接写0或1的
                if (candidates.size() > 2) {
                    candidates = candidates.subList(0, 2);
                }
                String img1 = goldJPA.findByTaskIdAndOrdAndVerificationType(taskId, 0, getVType().ordinal());
                String img2 = goldJPA.findByTaskIdAndOrdAndVerificationType(taskId, 1, getVType().ordinal());
                String img3 = goldJPA.findByTaskIdAndOrdAndVerificationType(taskId, 2, getVType().ordinal());

                golds.add(img1);
                golds.add(img2);
                golds.add(img3);
            } else if (haveEnter + 1 == TRAPS.get(1)) {
                // 这种重复做码也是有点不爽
                if (candidates.size() > 3) {
                    candidates = candidates.subList(0, 3);
                }
                String img1 = goldJPA.findByTaskIdAndOrdAndVerificationType(taskId, 3, getVType().ordinal());
                String img2 = goldJPA.findByTaskIdAndOrdAndVerificationType(taskId, 4, getVType().ordinal());

                golds.add(img1);
                golds.add(img2);
            } else {
                ArrayList<Integer> find = TRAPS.stream().filter(t -> t == haveEnter + 1).collect(Collectors.toCollection(ArrayList::new));
                if (find.size() == 1) {
                    if (candidates.size() > 4) {
                        candidates = candidates.subList(0, 4);
                    }
                    int ord = TRAPS.indexOf(find.get(0)) + 3;
                    String img = goldJPA.findByTaskIdAndOrdAndVerificationType(taskId, ord, getVType().ordinal());
                    golds.add(img);
                }
            }
        }

        EnterResponse response = new EnterResponse();

        if (candidates.isEmpty()) {
            response.setImgNames(new ArrayList<>());
            return response; // 不写也行
        }

        lowerContractService.recordEnterTask(taskId, username);


        ArrayList<String> imgs = candidates.stream().map(Microtask::getImgName)
                .collect(Collectors.toCollection(ArrayList::new));
        imgs.addAll(golds);
        response.setImgNames(imgs);


        candidates.forEach(mt -> {
            mt.setParallel(mt.getParallel() + 1);
            microtaskJPA.save(mt);

            CriticalSection.Item item = new CriticalSection.Item();
            item.requestTime = LocalDateTime.now();
            item.username = username;
            item.microtaskId = mt.getMicrotaskId();
            getRecords().add(item);
        });

        // 可以考虑打乱一下顺序，但是这样就不好测试了，

        return response;
    }

    @Override
    public void saveVerifications(RVerifications verifications, String username) {
        this.username = username;

        if (verifications.getVerifications().isEmpty()) {
            throw new MyNotValidException();
        }

        if (isInTime(verifications)) {
            // 肯定都是这个task
            // 虽然好像应该检查一下。。
            Task task = taskJPA.findByAnnotationId(verifications.getVerifications().get(0).getAnnotationId());

            if (task.getIsCollecting() == 1) {
                verifications.getVerifications().forEach(v -> {
                    acceptVerification(v);

                    List<Verification> haveVerified = verificationJPA.findByAnnotationIdAndVerificationType(annotation.getAnnotationId(), getVType());
                    if (haveVerified.size() == INSPECTION_PER_CONTRACT) {
                        int sum = haveVerified.stream().mapToInt(Verification::getRate).sum();
                        if (sum >= 2) { // 如果成功了
                            passVerification();
                        } else {
                            failVerification();
                        }
                    }
                });
            } else {
                BlacklistItem bli = blacklistJPA.findByUsernameAndTaskIdAndVerificationType(username, task.getTaskId(), getVType());
                bli.setHaveEnter(bli.getHaveEnter() + 1);
                blacklistJPA.save(bli);

                ArrayList<Verification> traps = verifications.getVerifications().stream().filter(v -> {
                    Microtask mt = microtaskJPA.findByAnnotationId(v.getAnnotationId());
                    return mt.getMicrotaskStatus() == MicrotaskStatus.PASSED;
                }).collect(Collectors.toCollection(ArrayList::new));

                verifications.getVerifications().removeAll(traps);
                // 这之后verification只有非trap的verification

                ArrayList<Verification> diffs = traps.stream().filter(v -> {
                    List<Verification> vs = verificationJPA.findByAnnotationIdAndVerificationType(v.getAnnotationId(), getVType());
                    return vs.get(0).getRate() != v.getRate();
                }).collect(Collectors.toCollection(ArrayList::new));

                if (diffs.isEmpty()) {
                    for (Verification v : verifications.getVerifications()) {
                        acceptVerification(v);

                        if (v.getRate() == 1) {
                            passVerification();
                        } else {
                            failVerification();
                        }
                    }
                } else {
                    // 还是要accept一下的
                    for (Verification v : verifications.getVerifications()) {
                        acceptVerification(v);
                    }
//                    ArrayList<Long> failedId = diffs.stream().map(Verification::getAnnotationId)
//                            .collect(Collectors.toCollection(ArrayList::new));

                    ArrayList<String> failedImgNames = diffs.stream()
                            .map(v -> microtaskJPA.findByAnnotationId(v.getAnnotationId()).getImgName())
                            .collect(Collectors.toCollection(ArrayList::new));

                    int haveEnter = bli.getHaveEnter();
                    if (haveEnter != 1) {
                        bli.setWrong(bli.getWrong() + 1);
                        blacklistJPA.save(bli);
                    }
                    Warning warning = new Warning();
//                    warning.setFailedIds(failedId);
                    warning.setFailedImgNames(failedImgNames);
                    warning.setForbidden(bli.getWrong() >= TRAP_FALL_TOLERANCE);
                    throw new MyFailTestException(warning);
                }
            }
        } else {
            throw new MyNotValidException();
        }
    }

    /**
     * private
     */
    private boolean isInTime(RVerifications verifications) {
        // 只要有一个就可以了，因为是一波一波的，实际上应该保证每一项都满足要么是gold，要么在时间限度内
        return verifications.getVerifications().stream().anyMatch(v -> {
            Microtask mt = microtaskJPA.findByAnnotationId(v.getAnnotationId());

            return mt.getMicrotaskStatus() != MicrotaskStatus.PASSED // 说明是gold，有gold则下面肯定没有记录
                    && getRecords().stream().anyMatch(i -> i.username.equals(username) && i.microtaskId == mt.getMicrotaskId());
        });
    }

    private void acceptVerification(Verification v) {
        setMicrotaskAndAnnotation(v);

        v.setVerificationType(getVType());
        v.setUsername(username);
        v.setCreateTime(LocalDateTime.now());
        v.setIsMajorityVoting(task.getIsCollecting());
        verificationJPA.save(v);


        microtask.setParallel(microtask.getParallel() - 1);
        microtaskJPA.save(microtask);

        getRecords().removeIf(ii -> ii.username.equals(username) && ii.microtaskId == microtask.getMicrotaskId());
    }

    protected void passVerification() {
        // 如果是coverageVerification这个再set一次也没什么问题吧
        annotation.setAnnotationStatus(getSuccessfulAnStt());
        annotationJPA.save(annotation);

        microtask.setMicrotaskStatus(getSuccessfulMtStt());
        microtaskJPA.save(microtask);
    }


    protected void failVerification() {
        annotation.setAnnotationStatus(getFailedAnStt());
        annotationJPA.save(annotation);

        microtask.setMicrotaskStatus(getFailedMtStt());
        microtaskJPA.save(microtask);
    }

    private void setMicrotaskAndAnnotation(Verification v) {
        this.microtask = microtaskJPA.findByAnnotationId(v.getAnnotationId());
        this.annotation = anttById(annotationJPA, v.getAnnotationId());
    }

    protected void checkFinishTask() {
        List<Microtask> notPassed = microtaskJPA.findByTaskIdNotPassed(task.getTaskId());
        if (notPassed.isEmpty()) {
            task.setTaskStatus(TaskStatus.FINISHED);
            taskJPA.save(task);
        }
    }

//    protected void checkAndFindGold(VerificationType verificationType) {
//        if (task.getIsCollecting() == 1) {
//            List<Microtask> samples = microtaskJPA.getSampling(task.getTaskId());
//            // 如果所有sample都完成了，选gold
//            if (samples.stream().allMatch(s -> s.getMicrotaskStatus() == MicrotaskStatus.PASSED)) {
//                findGoldForFS(samples, verificationType);
//            }
//        }
//    }

    protected boolean needToFindGold() {
        if (task.getIsCollecting() == 0) {
            return false;
        }

        // 如果所有sample都完成了，选gold
        List<Microtask> samples = microtaskJPA.getSampling(task.getTaskId());
        return samples.stream().allMatch(s -> s.getMicrotaskStatus() == MicrotaskStatus.PASSED);
    }

    protected void findGoldForGeneral() {
        List<Microtask> samples = microtaskJPA.getSampling(task.getTaskId());
        List<Long> results = findCandidates(samples, VerificationType.QUALITY);

        if (results.size() >= GOLD_NUM) {
            // TODO 这里可以排一下序，按全是0的为优。下同。
            saveGold(results, VerificationType.QUALITY);

            task.setIsCollecting(0);
            taskJPA.save(task);
        } else { // 应该不怎么会用到这里吧。。
            int deficit = GOLD_NUM - results.size();
            arrangeNewSample(deficit);
        }
    }

    protected void findGoldForFS() {
        List<Microtask> samples = microtaskJPA.getSampling(task.getTaskId());

        List<Long> results1 = findCandidates(samples, VerificationType.QUALITY);
        List<Long> results2 = findCandidates(samples, VerificationType.COVERAGE);

        if (results1.size() >= GOLD_NUM && results2.size() >= GOLD_NUM) {
            saveGold(results1, VerificationType.QUALITY);
            saveGold(results2, VerificationType.COVERAGE);

            task.setIsCollecting(0);
            taskJPA.save(task);
        } else {
            int deficit = GOLD_NUM - Math.min(results1.size(), results2.size());
            arrangeNewSample(deficit);
        }
    }

    private ArrayList<Long> findCandidates(List<Microtask> samples, VerificationType vType) {
        ArrayList<Long> candidates = new ArrayList<>();
        for (Microtask s : samples) {
            List<BigInteger> subcddt = annotationJPA.getSample(s.getMicrotaskId(), vType.ordinal());
            if (!subcddt.isEmpty()) {
                candidates.add(subcddt.get(0).longValue());
            }
        }
        return candidates;
    }

    private void saveGold(List<Long> candidates, VerificationType verificationType) {
        for (int i = 0; i < GOLD_NUM; i++) {
            long l = candidates.get(i);
            Gold gold = new Gold();
            gold.annotationId = l;
            gold.ord = i;
            gold.taskId = task.getTaskId();
            gold.verificationType = verificationType;
            goldJPA.save(gold);
        }
    }

    private void arrangeNewSample(int deficit) {
        List<Microtask> unSampled = microtaskJPA.getUnSampled(task.getTaskId());
        if (unSampled.size() > deficit) {
            unSampled = unSampled.subList(0, deficit);
        }
        for (Microtask m : unSampled) {
            m.setIsSample(1);
            microtaskJPA.save(m);
        }
    }


    /**
     * trivial
     */
    protected abstract MicrotaskStatus getPriorMtStt();

    protected abstract MicrotaskStatus getSuccessfulMtStt();

    protected MicrotaskStatus getFailedMtStt() {
        return MicrotaskStatus.YET_TO_DRAW;
    }

    protected AnnotationStatus getSuccessfulAnStt() {
        return AnnotationStatus.PASSED;
    }

    protected abstract AnnotationStatus getFailedAnStt();


    protected abstract VerificationType getVType();

    protected abstract List<CriticalSection.Item> getRecords();

}
