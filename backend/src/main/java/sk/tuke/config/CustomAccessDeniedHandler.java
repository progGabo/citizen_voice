package sk.tuke.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req,
        HttpServletResponse res,
        AccessDeniedException accessDeniedException) throws IOException {

        res.setContentType(ContentType.APPLICATION_JSON.toString());
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.getWriter().write("Access Denied... Forbidden");
    }
}
