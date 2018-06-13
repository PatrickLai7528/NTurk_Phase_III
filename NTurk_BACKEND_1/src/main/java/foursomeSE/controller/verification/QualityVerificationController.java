package foursomeSE.controller.verification;

import foursomeSE.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qualityVerification")
public class QualityVerificationController extends AbstractVerificationController {
    @Autowired
    @Qualifier("quality")
    public void setVerificationService(VerificationService qualityVerificationService) {
        verificationService = qualityVerificationService;
    }
}
