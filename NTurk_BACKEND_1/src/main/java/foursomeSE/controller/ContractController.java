package foursomeSE.controller;

import foursomeSE.entity.contract.Contract;
import foursomeSE.error.MyAccessDeniedException;
import foursomeSE.error.MyErrorType;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.security.JwtUtil;
import foursomeSE.service.contract.UpperContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContractController {
    private UpperContractService service;

    public ContractController(UpperContractService service) {
        this.service = service;
    }

    // 其实感觉不应该有这个方法的，前端所有时候都不应传contract相关，而应该是task
    @RequestMapping(value = "/contract",
            method = RequestMethod.POST)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> addContract(@RequestHeader("Authorization") String token, @RequestBody Contract contract) {
        String username = JwtUtil.getUsernameFromToken(token);

        service.add(contract, username);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/contract/taskId/{taskId}",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> getOneByTaskId(@RequestHeader("Authorization") String token
            , @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        try {
            return new ResponseEntity<>(service.getByTaskIdByWorkerUsername(taskId, username)
                    , HttpStatus.OK);
        } catch (MyObjectNotFoundException e) {
            return new ResponseEntity<>(new MyErrorType("Unable to update. contract" + " not found."),
                    HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/contracts/taskId/{taskId}",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<?> getLotByTaskId(@RequestHeader("Authorization") String token
            , @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        try {
            List<Contract> cs = service.getByTaskIdByRequesterUsername(taskId, username);
            return new ResponseEntity<>(cs, HttpStatus.OK);
        } catch (MyObjectNotFoundException e) {
            return new ResponseEntity<>(new MyErrorType("Unable to update. contracts" + " not found."),
                    HttpStatus.NOT_FOUND);
        } catch (MyAccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


//    @RequestMapping(value = "/contract",
//            method = RequestMethod.PUT)
//    @PreAuthorize("hasRole('WORKER')")
//    public ResponseEntity<?> updateContract(@RequestBody Contract contract) {
//        try {
//            service.update(contract);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (MyObjectNotFoundException e) {
//            return new ResponseEntity<>(new MyErrorType("Unable to update. contract with id " + contract.getContractId() + " not found."),
//                    HttpStatus.NOT_FOUND);
//        }
//    }

    @RequestMapping(value = "/contract/complete/{taskId}",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> completeTask(@RequestHeader("Authorization") String token,
                                          @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        try {
            service.fulfilContract(taskId, username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MyObjectNotFoundException e) {
            return new ResponseEntity<>(new MyErrorType("Unable to complete. task with id " + taskId + " not found."),
                    HttpStatus.NOT_FOUND);
        } catch (MyAccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/contract/review/{taskId}",
            method = RequestMethod.GET)
    public ResponseEntity<?> getContractForReview(@RequestHeader("Authorization") String token,
                                                  @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);
        Contract cddt = service.getByTaskIdForInspection(taskId, username);
        if (cddt == null) {
            return new ResponseEntity<>(new MyErrorType("no tasks to inspect"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(cddt, HttpStatus.OK);
    }
}
