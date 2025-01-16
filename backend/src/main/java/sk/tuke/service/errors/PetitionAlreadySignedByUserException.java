package sk.tuke.service.errors;

public class PetitionAlreadySignedByUserException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public PetitionAlreadySignedByUserException() {
        super("Petition was already signed by user");
    }
}
