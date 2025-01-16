package sk.tuke.service.errors;

public class ValidationFailureException extends RuntimeException{

    public ValidationFailureException(String message) {
        super(message);
    }
}
