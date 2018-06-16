package foursomeSE.service.contract;

import foursomeSE.entity.contract.Contract;

import java.util.List;

public interface LowerContractService {

    void recordEnterTask(long taskId, String username);
}
