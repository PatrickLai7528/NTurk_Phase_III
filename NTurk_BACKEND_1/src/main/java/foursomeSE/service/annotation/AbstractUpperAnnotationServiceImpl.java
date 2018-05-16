package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.service.common.CommonCongruentService;
import foursomeSE.service.contract.UpperContractService;

public abstract class AbstractUpperAnnotationServiceImpl<T extends Annotation> implements UpperAnnotationService<T> {
    // 这个原则上不应该用upper
    private UpperContractService contractService;
    private CommonCongruentService<T> service;

    public AbstractUpperAnnotationServiceImpl(UpperContractService contractService,
                                              CommonCongruentService<T> service) {
        this.contractService = contractService;
        this.service = service;
    }

    @Override
    public T getOneBy(long taskId, String username, String imgName) {
        return getOneBy(contractService.getByTaskIdByWorkerUsername(taskId, username).getContractId(), imgName);
    }

    @Override
    public T getOneBy(long contractId, String imgName) {
        return service.getOneBy(a ->
                a.getContractId() == contractId
                        && a.getImgName().equals(imgName));
    }

    @Override
    public void addOneBy(long taskId, String username, T annotation) {
        annotation.setContractId(contractService.getByTaskIdByWorkerUsername(taskId, username).getContractId());
        service.add(annotation);
    }

    @Override
    public void update(T annotation) {
        service.update(annotation);
    }

    @Override
    public T getById(long id) {
        return service.getById(id);
    }
}


