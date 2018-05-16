package foursomeSE.error;

public class MyAuthenticationException extends RuntimeException {
    public MyAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}