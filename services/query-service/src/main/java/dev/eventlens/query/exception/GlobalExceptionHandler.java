package dev.eventlens.query.exception;
import dev.eventlens.model.ApiError; import jakarta.servlet.http.HttpServletRequest; import java.time.Instant; import java.util.NoSuchElementException; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler({IllegalArgumentException.class,MethodArgumentNotValidException.class}) ResponseEntity<ApiError> bad(Exception ex,HttpServletRequest req){return response(HttpStatus.BAD_REQUEST,"VALIDATION_ERROR",ex.getMessage(),req);}
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<ApiError> missing(Exception ex,HttpServletRequest req){return response(HttpStatus.NOT_FOUND,"NOT_FOUND",ex.getMessage(),req);}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiError> error(Exception ex,HttpServletRequest req){return response(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_ERROR","Request could not be completed",req);}
 private ResponseEntity<ApiError> response(HttpStatus status,String code,String message,HttpServletRequest req){return ResponseEntity.status(status).body(new ApiError(Instant.now(),status.value(),code,message,req.getRequestURI()));}
}

