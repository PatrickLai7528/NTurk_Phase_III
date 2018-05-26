package foursomeSE.error;

public class MyObjectNotFoundException extends RuntimeException {
    public MyObjectNotFoundException(String message) {
        super(message);
    }
}
