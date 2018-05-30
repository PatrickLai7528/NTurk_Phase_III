package foursomeSE.jpa.contract;

import foursomeSE.entity.contract.Contract;
import foursomeSE.entity.contract.ContractStatus;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value =
            "select * from contracts c1\n" +
            "where c1.contract_id in (\n" +
            "    select x1.contract_id\n" +
            "    from ( -- 求出这个任务的所有已完成的他没有审过的也不是他自己做的contract中，被inspect几次\n" +
            "        select contract_id, (\n" +
            "            select count(*) from inspection_contracts ic\n" +
            "            where c.contract_id = ic.contract_id\n" +
            "        ) as cnt\n" +
            "        from contracts c\n" +
            "        where c.task_id = ?1 and c.contract_id not in (\n" +
            "            select ic.contract_id from inspection_contracts ic\n" +
            "            where ic.worker_id = ?2 -- 不是他审的\n" +
            "        ) and c.contract_status = 0 -- 已完成\n" +
            "        and c.worker_id <> ?2 -- 不是他自己做的\n" +
            "    ) as x1\n" +
            "    where x1.cnt = all ( -- 求这个任务的所有已完成的他没有审过的也不是他自己做的contract中被inspect最小的次数\n" +
            "        select IFNULL(min(cnt), 0) as minimum\n" +
            "        from (\n" +
            "            select contract_id, count(*) as cnt\n" +
            "            from inspection_contracts\n" +
            "            where contract_id in (\n" +
            "                select contract_id from contracts \n" +
            "                where task_id = ?1 and contract_status = 0 -- 已完成\n" +
            "                and worker_id <> ?2\n" +
            "            ) and contract_id not in (\n" +
            "                select ic.contract_id from inspection_contracts ic\n" +
            "                where ic.worker_id = ?2 -- 不是他审的\n" +
            "            )\n" +
            "            group by contract_id\n" +
            "        ) as x2\n" +
            "    )\n" +
            ");",
            nativeQuery = true)
    List<Contract> findByTaskIdForInspection(long taskId);
}
