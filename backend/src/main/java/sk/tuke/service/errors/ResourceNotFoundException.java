package sk.tuke.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Resource not found")
public class ResourceNotFoundException extends Exception{

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
