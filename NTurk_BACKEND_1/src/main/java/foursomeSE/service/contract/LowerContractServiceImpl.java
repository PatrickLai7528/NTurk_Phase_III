//package foursomeSE.service.contract;
//
//import foursomeSE.entity.contract.Contract;
//import foursomeSE.service.common.CommonCongruentServiceImpl;
//import org.springframework.stereotype.Service;
//
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//@Service
//public class LowerContractServiceImpl extends CommonCongruentServiceImpl<Contract> implements LowerContractService {
//    /**
//     * trivial
//     * */
//    @Override
//    public String getTableName() {
//        return "contract";
//    }
//
//    @Override
//    public Class<Contract[]> getTArrayType() {
//        return Contract[].class;
//    }
//
//    @Override
//    public Function<Contract, Long> getIdFunction() {
//        return Contract::getContractId;
//    }
//
//    @Override
//    public Consumer<Contract> setIdFunction(long newId) {
//        return c -> c.setContractId(newId);
//    }
//}
