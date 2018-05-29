package foursomeSE.jpa.inspection;

import foursomeSE.entity.inspection.InspectionContract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface InspectionContractJPA extends CrudRepository<InspectionContract, Long> {
    @Query(value = "select count(*) from inspection_contracts\n" +
            "where contract_id in (\n" +
            "    select contract_id from contracts\n" +
            "    where task_id = ?2\n" +
            ") and worker_id in (\n" +
            "    select id from workers\n" +
            "    where email_address = ?1\n" +
            ")",
            nativeQuery = true)
    long countByWorkerUsernameAndTaskId(String username, long taskId);
}

/*
select count(*) from inspection_contracts
where contract_id in (
    select contract_id from contracts
    where task_id = ?2
) and worker_id in (
    select id from workers
    where email_address = ?1
)
*/