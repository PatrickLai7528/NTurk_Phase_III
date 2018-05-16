package foursomeSE.error;

public class MyErrorType {
    private String errorMessage;

    public MyErrorType(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
