package foursomeSE.service.contract;

import foursomeSE.entity.contract.Contract;

import java.util.List;

public interface UpperContractService {

    Contract getByTaskIdByWorkerUsername(long taskId, String username);

    /**
     * 只能访问自己发布的task的contracts
     * 目前是所有的，不管有没有做完
     * */
    List<Contract> getByTaskIdByRequesterUsername(long taskId, String username);

    /**
     * 对传来的contract的要求只有taskId
     * 需要该task的状态属于ONGOING，如果是appoint模式的contract，还要检查user属于nominee中（没做）
     * 要检查该user还没有参加过这个task（没做）
     * */
    void add(Contract contract, String username);

    /**
     * 检查他真的做完了（没做）
     * 更新contractStatus为已完成，检查整个任务是否完成，
     */
    void fulfilContract(long taskId, String username);
}
