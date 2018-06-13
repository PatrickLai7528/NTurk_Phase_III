package foursomeSE.error;

import foursomeSE.entity.communicate.Warning;

import java.util.ArrayList;

public class MyFailTestException extends RuntimeException {
    private Warning warning;

    public MyFailTestException(Warning warning) {
        this.warning = warning;
    }

    public Warning getWarning() {
        return warning;
    }

    public void setWarning(Warning warning) {
        this.warning = warning;
    }
}
