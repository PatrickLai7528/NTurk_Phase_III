package foursomeSE.controller.verification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class AbstractVerificationController {

    @RequestMapping(value = "/taskId/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<?> enterVerification(@PathVariable("taskId") String taskId) {
        return null;
    }

    @RequestMapping(value = "/saveVerifications", method = RequestMethod.GET)
    public ResponseEntity<?> saveVerifications() {
        return null;
    }

}
