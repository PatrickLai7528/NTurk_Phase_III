package foursomeSE.util;

import foursomeSE.entity.annotation.FrameAnnotation;
import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.entity.verification.Verification;
import foursomeSE.jpa.BlacklistJPA;
import foursomeSE.jpa.annotation.*;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.gold.GoldJPA;
import foursomeSE.jpa.tag.TagAndTaskJPA;
import foursomeSE.jpa.tag.TagAndWorkerJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.jpa.verification.VerificationJPA;
import foursomeSE.service.annotation.UpperAnnotationService;
import foursomeSE.service.task.UpperTaskService;
import foursomeSE.service.task.UpperTaskServiceImpl;
import foursomeSE.service.user.upper.UpperRequesterService;
import foursomeSE.service.user.upper.UpperWorkerService;
import foursomeSE.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class WithTheAutowired {
    @Autowired
    protected WorkerJPA workerJPA;
    @Autowired
    protected RequesterJPA requesterJPA;

    @Autowired
    protected TaskJPA taskJPA;
    @Autowired
    protected ContractJPA contractJPA;
    @Autowired
    protected MicrotaskJPA microtaskJPA;

    @Autowired
    protected AnnotationJPA annotationJPA;
    @Autowired
    protected FrameAnnotationJPA frameAnnotationJPA;
    @Autowired
    protected GeneralAnnotationJPA generalAnnotationJPA;
    @Autowired
    protected SegmentAnnotationJPA segmentAnnotationJPA;

    @Autowired
    protected VerificationJPA verificationJPA;
    @Autowired
    protected GoldJPA goldJPA;
    @Autowired
    protected BlacklistJPA blacklistJPA;
    @Autowired
    protected TagAndTaskJPA tagAndTaskJPA;
    @Autowired
    protected TagAndWorkerJPA tagAndWorkerJPA;


    @Autowired
    protected UpperRequesterService requesterService;
    @Autowired
    protected UpperWorkerService workerService;

    @Autowired
    protected UpperTaskService taskService;
    @Autowired
    protected UpperTaskServiceImpl taskServiceImpl;
    @Autowired
    protected UpperAnnotationService<GeneralAnnotation> generalAnnotationService;
    @Autowired
    protected UpperAnnotationService<FrameAnnotation> frameAnnotationService;

    @Autowired
    @Qualifier("quality")
    protected VerificationService qualityVerificationService;

    @Autowired
    @Qualifier("coverage")
    protected VerificationService coverageVerificationService;
}
