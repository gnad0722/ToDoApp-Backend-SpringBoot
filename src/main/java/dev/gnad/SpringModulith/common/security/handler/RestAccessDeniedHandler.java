package dev.gnad.SpringModulith.common.security.handler;

import dev.gnad.SpringModulith.common.api.exception.ErrorMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Authenticated user without the required role -> 403 with the API envelope.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        SecurityErrorResponseWriter.write(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                ErrorMessages.FORBIDDEN
        );
    }
}
