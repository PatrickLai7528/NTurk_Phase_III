package foursomeSE.entity.communicate.jwt;

import java.io.Serializable;

public class JwtAuthenticationRequest implements Serializable {
    private static final long serialVersionUID = -8445943548965154778L;

    private String emailAddress;
    private String password;

    public JwtAuthenticationRequest() {
    }

    public JwtAuthenticationRequest(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}