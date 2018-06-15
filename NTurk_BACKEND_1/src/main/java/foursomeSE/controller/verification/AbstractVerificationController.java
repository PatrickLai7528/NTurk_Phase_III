package foursomeSE.controller.verification;

import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.error.MyFailTestException;
import foursomeSE.security.JwtUtil;
import foursomeSE.service.verification.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

public class AbstractVerificationController {
    protected VerificationService verificationService;


    @RequestMapping(value = "/taskId/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<?> enterVerification(@RequestHeader("Authorization") String token,
                                               @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        EnterResponse response = verificationService.enterVerification(taskId, username);
        if (response.getImgNames() == null) {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        } else if (response.getImgNames().isEmpty()) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveVerifications", method = RequestMethod.GET)
    public ResponseEntity<?> saveVerifications(@RequestHeader("Authorization") String token,
                                               @RequestBody RVerifications verifications) {
        String username = JwtUtil.getUsernameFromToken(token);

        try {
            verificationService.saveVerifications(verifications, username);
        } catch (MyFailTestException e) {
            return new ResponseEntity<Object>(e.getWarning(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

}
