package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.annotation.AbstractAnnotationJPA;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.user.WorkerJPA;

import static foursomeSE.service.annotation.AnnotationUtils.annotationByContractIdAndImgName;
import static foursomeSE.service.contract.ContractUtils.contractByTaskIdAndUsername;

public abstract class AbstractUpperAnnotationServiceImpl<T extends Annotation> implements UpperAnnotationService<T> {
    // 这个原则上不应该用upper，但是好像还是
//    private UpperContractService contractService;
    private ContractJPA contractJPA;

    private AbstractAnnotationJPA<T> annotationJPA;

    private WorkerJPA workerJPA;

//    private CommonCongruentService<fasT> service;


    public AbstractUpperAnnotationServiceImpl(ContractJPA contractJPA,
                                              AbstractAnnotationJPA<T> annotationJPA,
                                              WorkerJPA workerJPA) {
        this.contractJPA = contractJPA;
        this.annotationJPA = annotationJPA;
        this.workerJPA = workerJPA;
    }

    @Override
    public T getOneBy(long taskId, String username, String imgName) {
        return getOneBy(
                contractByTaskIdAndUsername(contractJPA, workerJPA, taskId, username)
                        .getContractId(),
                imgName
        );
    }

    @Override
    public T getOneBy(long contractId, String imgName) {
//        return service.getOneBy(a ->
//                a.getContractId() == contractId
//                        && a.getImgName().equals(imgName));

        return annotationByContractIdAndImgName(annotationJPA, contractId, imgName);
    }

    @Override
    public void addOneBy(long taskId, String username, T annotation) {
        annotation.setContractId(contractByTaskIdAndUsername(contractJPA, workerJPA, taskId, username).getContractId());
        annotationJPA.save(annotation);
    }

    @Override
    public void update(T annotation) {
        annotationJPA.save(annotation);
    }

    @Override
    public T getById(long id) {
        return annotationJPA.findById(id)
                .orElseThrow(() -> new MyObjectNotFoundException("annotation with id " + id + " is not found"));
    }
}


