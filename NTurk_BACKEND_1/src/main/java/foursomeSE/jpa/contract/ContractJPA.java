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

//    long countByTaskIdAndContractStatus(long taskId, ContractStatus contractStatus);
//
//    List<Contract> findByTaskIdAndContractStatus(long taskId, ContractStatus contractStatus);

    List<Contract> findByTaskId(long taskId);

    List<Contract> findByWorkerId(long id);

    @Query(value = "select c1.contract_id, contract_status, last_edit_time, task_id, worker_id from contracts c1 inner join ( -- 求出这个任务的所有已完成的他没有审过的也不是他自己做的contract中，被inspect几次\n" +
            "select contract_id, (\n" +
            "select count(*) from inspection_contracts ic\n" +
            "where c.contract_id = ic.contract_id\n" +
            ") as cnt\n" +
            "from contracts c\n" +
            "where c.task_id = ?1 and c.contract_id not in (\n" +
            "select ic.contract_id from inspection_contracts ic\n" +
            "where ic.worker_id = ?2 -- 不是他审的\n" +
            ") and c.contract_status = 0 -- 已完成\n" +
            "and c.worker_id <> ?2 -- 不是他自己做的\n" +
            ") as x1 on c1.contract_id = x1.contract_id\n" +
            "order by cnt asc",
            nativeQuery = true)
    List<Contract> findByTaskIdForInspection(long taskId, long workerId);

    @Query(value = "select * from contracts\n" +
            "where task_id = ?1 and worker_id in (\n" +
            "    select id from workers where email_address = ?2\n" +
            ")",
            nativeQuery = true)
    Contract findByTaskIdByUsername(long taskId, String username);
}

/*
select * from contracts c1
where c1.contract_id in (
    select x1.contract_id
    from ( -- 求出这个任务的所有已完成的他没有审过的也不是他自己做的contract中，被inspect几次
        select contract_id, (
            select count(*) from inspection_contracts ic
            where c.contract_id = ic.contract_id
        ) as cnt
        from contracts c
        where c.task_id = ?1 and c.contract_id not in (
            select ic.contract_id from inspection_contracts ic
            where ic.worker_id = ?2 -- 不是他审的
        ) and c.contract_status = 0 -- 已完成
        and c.worker_id <> ?2 -- 不是他自己做的
    ) as x1
    where x1.cnt = all ( -- 求这个任务的所有已完成的他没有审过的也不是他自己做的contract中被inspect最小的次数
        select min(cnt) from (
            select contract_id, (
                select count(*) from inspection_contracts ic
                where c.contract_id = ic.contract_id
            ) as cnt
            from contracts c
            where c.task_id = ?1 and c.contract_id not in (
                select ic.contract_id from inspection_contracts ic
                where ic.worker_id = ?2 -- 不是他审的
            ) and c.contract_status = 0 -- 已完成
            and c.worker_id <> ?2 -- 不是他自己做的
        ) as x2
    )
);

select c1.contract_id, contract_status, last_edit_time, task_id, worker_id
from contracts c1 inner join ( -- 求出这个任务的所有已完成的他没有审过的也不是他自己做的contract中，被inspect几次
    select contract_id, (
        select count(*) from inspection_contracts ic
        where c.contract_id = ic.contract_id
    ) as cnt
    from contracts c
    where c.task_id = ?1 and c.contract_id not in (
        select ic.contract_id from inspection_contracts ic
        where ic.worker_id = ?2 -- 不是他审的
    ) and c.contract_status = 0 -- 已完成
    and c.worker_id <> ?2 -- 不是他自己做的
) as x1 on c1.contract_id = x1.contract_id
order by cnt asc


select * from contracts
where task_id = ?1 and worker_id in (
    select id from workers where email_address = ?2
)
 */