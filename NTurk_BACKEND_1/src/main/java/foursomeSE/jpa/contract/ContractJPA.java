package foursomeSE.jpa.contract;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.contract.ContractStatus;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface ContractJPA extends CrudRepository<Contract, Long> {
    Optional<Contract> findByTaskIdAndWorkerId(long taskId, long workerId);
    long countByTaskIdAndContractStatus(long taskId, ContractStatus contractStatus);
    List<Contract> findByTaskIdAndContractStatus(long taskId, ContractStatus contractStatus);
    List<Contract> findByTaskId(long taskId);
    List<Contract> findByWorkerId(long id);
}
