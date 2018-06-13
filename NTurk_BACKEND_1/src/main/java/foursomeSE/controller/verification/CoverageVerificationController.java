package foursomeSE.controller.verification;

import foursomeSE.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coverageVerification")
public class CoverageVerificationController extends AbstractVerificationController {

    @Autowired
    @Qualifier("coverage")
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }
}
