package foursomeSE.util;

import foursomeSE.entity.annotation.GeneralAnnotation;
import foursomeSE.jpa.annotation.*;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.inspection.InspectionContractJPA;
import foursomeSE.jpa.inspection.InspectionJPA;
import foursomeSE.jpa.task.MicrotaskJPA;
import foursomeSE.jpa.task.TaskJPA;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import foursomeSE.service.annotation.UpperAnnotationService;
import foursomeSE.service.inspection.UpperInspectionService;
import foursomeSE.service.task.UpperTaskService;
import foursomeSE.service.user.upper.UpperRequesterService;
import foursomeSE.service.user.upper.UpperWorkerService;
import org.springframework.beans.factory.annotation.Autowired;

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
    protected InspectionJPA inspectionJPA;
    @Autowired
    protected InspectionContractJPA inspectionContractJPA;

    @Autowired
    protected UpperRequesterService requesterService;
    @Autowired
    protected UpperWorkerService workerService;

    @Autowired
    protected UpperTaskService taskService;
    @Autowired
    protected UpperAnnotationService<GeneralAnnotation> generalAnnotationService;
    @Autowired
    protected UpperInspectionService inspectionService;
}
