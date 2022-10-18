package exceptions;

public class RepositoryException extends Exception {
    private String errMsg;

    public RepositoryException(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrorMessage() {
        return errMsg;
    }
}
