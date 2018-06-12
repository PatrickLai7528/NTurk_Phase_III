package foursomeSE.controller.verification;

import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.security.JwtUtil;
import foursomeSE.service.verification.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class AbstractVerificationController {
    protected VerificationService verificationService;



    @RequestMapping(value = "/taskId/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<?> enterVerification(@RequestHeader("Authorization") String token,
                                               @PathVariable("taskId") long taskId) {
        String username = JwtUtil.getUsernameFromToken(token);

        EnterResponse response = verificationService.enterVerification(taskId, username);
        if (response.getImgNames().isEmpty()) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveVerifications", method = RequestMethod.GET)
    public ResponseEntity<?> saveVerifications(@RequestHeader("Authorization") String token,
                                               @RequestBody RVerifications verifications) {
        String username = JwtUtil.getUsernameFromToken(token);

        verificationService.saveVerifications(verifications, username);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

}
