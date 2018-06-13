//package foursomeSE.controller;
//
//import foursomeSE.entity.communicate.CInspectionContract;
//import foursomeSE.entity.communicate.EnterInspectionResponse;
//import foursomeSE.entity.inspection.RInspections;
//import foursomeSE.error.MyNotValidException;
//import foursomeSE.security.JwtUtil;
//import foursomeSE.service.inspection.UpperInspectionService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//public class InspectionController {
//    private UpperInspectionService inspectionService;
//
//    public InspectionController(UpperInspectionService inspectionService) {
//        this.inspectionService = inspectionService;
//    }
//
////    @RequestMapping(value = "/inspect",
////            method = RequestMethod.POST)
////    @PreAuthorize("hasRole('WORKER')")
////    public ResponseEntity<?> addInspections(@RequestHeader("Authorization") String token,
////                                            @RequestBody CInspectionContract cInspectionContract) {
////        String username = JwtUtil.getUsernameFromToken(token);
////        inspectionService.add(cInspectionContract, username);
////
////        return new ResponseEntity<>(HttpStatus.OK);
////    }
////
////    @RequestMapping(value = "/inspect/imgName/{imgName}",
////            method = RequestMethod.GET)
////    @PreAuthorize("hasRole('REQUESTER')")
////    public ResponseEntity<?> getBestKth(@RequestHeader("Authorization") String token,
////                                        @PathVariable("imgName") String imgName) {
////        String username = JwtUtil.getUsernameFromToken(token);
////        return new ResponseEntity<>(inspectionService.getBestKth(imgName, username), HttpStatus.OK);
////    }
//
//    @RequestMapping(value = "/inspect/enterInspection/{taskId}",
//            method = RequestMethod.GET)
//    @PreAuthorize("hasRole('WORKER')")
//    public ResponseEntity<?> enterInspection(@RequestHeader("Authorization") String token,
//                                             @PathVariable("taskId") int taskId) {
//        String username = JwtUtil.getUsernameFromToken(token);
//        EnterInspectionResponse result;
//        try {
//            result = inspectionService.enterInspection(taskId, username);
//        } catch (MyNotValidException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        if (result.getAnnotationIds().isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/inspect/saveInspections",
//            method = RequestMethod.POST)
//    @PreAuthorize("hasRole('WORKER')")
//    public ResponseEntity<?> saveInspections(@RequestHeader("Authorization") String token,
//                                             @RequestBody RInspections rInspections) {
//        String username = JwtUtil.getUsernameFromToken(token);
//
//        inspectionService.saveInspections(rInspections, token);
//        return new ResponseEntity<>(HttpStatus.OK);
//
//    }
//}
