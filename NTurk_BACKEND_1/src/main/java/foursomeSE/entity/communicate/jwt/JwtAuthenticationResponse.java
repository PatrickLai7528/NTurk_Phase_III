package foursomeSE.entity.communicate.jwt;

import foursomeSE.entity.user.UserType;

import java.io.Serializable;

public class JwtAuthenticationResponse implements Serializable {
    private static final long serialVersionUID = 1250166508152483573L;

    private String token;
    private long userId;
    private UserType userType;

    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String token, long userId, UserType userType) {
        this.token = token;
        this.userId = userId;
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public long getUserId() {
        return userId;
    }

    public UserType getUserType() {
        return userType;
    }
}