package foursomeSE.service.verification;

import foursomeSE.entity.communicate.EnterResponse;
import foursomeSE.entity.verification.RVerifications;
import foursomeSE.entity.verification.Verification;

public interface VerificationService<T extends Verification> {
    EnterResponse enterVerification(long taskId);

    void saveVerifications(RVerifications<T> verifications);
}
