package foursomeSE.service.contract;

import foursomeSE.entity.contract.Contract;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.jpa.contract.ContractJPA;
import foursomeSE.jpa.user.WorkerJPA;

import static foursomeSE.service.user.UserUtils.userByUsername;

public class ContractUtils { // 转到私有方法
    public static Contract contractByTaskIdAndUsername(ContractJPA contractJPA, WorkerJPA workerJPA, long taskId, String username) {
        return contractJPA
                .findByTaskIdAndWorkerId(taskId, userByUsername(workerJPA, username).getId())
                .orElseThrow(() -> new MyObjectNotFoundException("contract with taskId " + taskId + " and worker username " + username  + " is not found"));
    }
}
