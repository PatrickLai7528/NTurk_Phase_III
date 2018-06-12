package foursomeSE.entity.verification;

import java.util.List;

public class RVerifications<T extends Verification> {
    private List<T> verifications;

    public List<T> getVerifications() {
        return verifications;
    }

    public void setVerifications(List<T> verifications) {
        this.verifications = verifications;
    }
}
