package foursomeSE.service.verification;

import foursomeSE.entity.Gold;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.task.Microtask;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.task.TaskStatus;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.VerificationType;
import foursomeSE.jpa.BlacklistJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.gold.GoldJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.jpa.verification.VerificationJPA;
import foursomeSE.service.contract.LowerContractService;
import foursomeSE.util.CriticalSection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("coverage")
public class CoverageVerificationServiceImpl extends AbstractVerificationServiceImpl {

    public CoverageVerificationServiceImpl(MicrotaskJPA microtaskJPA, TaskJPA taskJPA, GoldJPA goldJPA, AnnotationJPA annotationJPA, VerificationJPA verificationJPA, BlacklistJPA blacklistJPA, LowerContractService lowerContractService, WorkerJPA workerJPA) {
        super(microtaskJPA, taskJPA, goldJPA, annotationJPA, verificationJPA, blacklistJPA, lowerContractService, workerJPA);
    }

    @Override
    protected void passVerification() {
        super.passVerification();

        if (needToFindGold()) {
            findGoldForFS();
        }
        checkFinishTask();
        // 都是有可能结束了的，包括collecting还没有结束时。极端一点就是所有结果都是2/3
    }

    @Override
    protected void failVerification() {
        super.failVerification();

        // 呃，iteration好像就只是说有几个框的问题，不要也罢。。
        // 然后如果是general，那么一概不用管iteration
        microtask.setIteration(microtask.getIteration() + 1);
        microtaskJPA.save(microtask);
    }

    /**
     * trivial
     * */

    @Override
    protected MicrotaskStatus getPriorMtStt() {
        return MicrotaskStatus.YET_TO_VERIFY_COVERAGE;
    }

    @Override
    protected MicrotaskStatus getSuccessfulMtStt() {
        return MicrotaskStatus.PASSED;
    }

    @Override
    protected VerificationType getVType() {
        return VerificationType.COVERAGE;
    }

    @Override
    protected List<CriticalSection.Item> getRecords() {
        return CriticalSection.coverageVerificationRecords;
    }

    @Override
    protected AnnotationStatus getFailedAnStt() {
        return AnnotationStatus.PASSED;
    }
}
