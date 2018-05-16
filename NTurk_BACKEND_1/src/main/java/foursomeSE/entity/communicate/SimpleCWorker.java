package foursomeSE.entity.communicate;

import foursomeSE.entity.user.Worker;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

public class SimpleCWorker {
    private long workerId;
    private String nickname;
    private String emailAddress;

    public SimpleCWorker(Worker worker) {
        setSameFields(this, worker);
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
