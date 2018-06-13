package foursomeSE.service.verification;

import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.Verification;

public interface VerificationService {
    EnterResponse enterVerification(long taskId, String username);

    void saveVerifications(RVerifications verifications, String username);
}
