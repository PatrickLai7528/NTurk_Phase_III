//package foursomeSE.service.inspection;
//
//import foursomeSE.entity.communicate.CInspection;
//import foursomeSE.entity.communicate.CInspectionContract;
//import foursomeSE.entity.communicate.EnterInspectionResponse;
//import foursomeSE.entity.inspection.Inspection;
//import foursomeSE.entity.inspection.InspectionContract;
//import foursomeSE.entity.inspection.RInspections;
//
//import java.util.List;
//
//public interface UpperInspectionService {
//    /**
//     * 需要有contractId, 和那个list，list每一项包括rate和annotationId
//     * 要求这个user之前没有对这个contract评审过
//     * */
//    void add(CInspectionContract cInspectionContract, String username);
//
//    /**
//     * 先返回最好的k个inspection吧，就不和annotation整合了
//     */
//    List<CInspection> getBestKth(String imgName, String username);
//
//
//    // 又是感觉上面的都不需要了
//    EnterInspectionResponse enterInspection(long taskId, String username);
//
//    void saveInspections(RInspections rInspections, String username);
//}
