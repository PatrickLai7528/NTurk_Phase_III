package foursomeSE.service.verification;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.AnnotationStatus;
import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.task.MicrotaskStatus;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.VerificationType;
import foursomeSE.jpa.BlacklistJPA;
import foursomeSE.jpa.annotation.AnnotationJPA;
import foursomeSE.jpa.annotation.GeneralAnnotationJPA;
import foursomeSE.jpa.gold.GoldJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.jpa.verification.VerificationJPA;
import foursomeSE.service.contract.LowerContractService;
import foursomeSE.util.CriticalSection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("quality")
public class QualityVerificationServiceImpl extends AbstractVerificationServiceImpl {
    private GeneralAnnotationJPA generalAnnotationJPA;

    public QualityVerificationServiceImpl(MicrotaskJPA microtaskJPA, TaskJPA taskJPA, GoldJPA goldJPA, AnnotationJPA annotationJPA, VerificationJPA verificationJPA, BlacklistJPA blacklistJPA, LowerContractService lowerContractService, WorkerJPA workerJPA, GeneralAnnotationJPA generalAnnotationJPA) {
        super(microtaskJPA, taskJPA, goldJPA, annotationJPA, verificationJPA, blacklistJPA, lowerContractService, workerJPA);
        this.generalAnnotationJPA = generalAnnotationJPA;
    }

    @Override
    protected void passVerification() {
        super.passVerification();

        if (isGeneral()) {
            // 但是general也不用管iteration。
            if (needToFindGold()) {
                findGoldForGeneral();
            }
            checkFinishTask();
        }
    }

    private boolean isGeneral() {
        return generalAnnotationJPA.findById(annotation.getAnnotationId()).isPresent();
    }

    @Override
    protected MicrotaskStatus getPriorMtStt() {
        return MicrotaskStatus.YET_TO_VERIFY_QUALITY;
    }

    @Override
    protected MicrotaskStatus getSuccessfulMtStt() {
        return isGeneral() ? MicrotaskStatus.PASSED : MicrotaskStatus.YET_TO_VERIFY_COVERAGE;
    }

    @Override
    protected VerificationType getVType() {
        return VerificationType.QUALITY;
    }

    @Override
    protected List<CriticalSection.Item> getRecords() {
        return CriticalSection.qualityVerificationRecords;
    }

    @Override
    protected AnnotationStatus getFailedAnStt() {
        return AnnotationStatus.FAILED;
    }
}
