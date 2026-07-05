package su.syel.fourthrest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorFieldsResponseDTO> handleParameterValidation(
            MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        List<FieldErrorDetail> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

            for(FieldError fieldError: fieldErrorList){
                String field = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                errors.add(new FieldErrorDetail(field, message));
            }
        }

        ErrorFieldsResponseDTO errorFieldsResponseDTO = new ErrorFieldsResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                errors,
                "There is NO some mandatory fields"
        );

        return ResponseEntity.badRequest().body(errorFieldsResponseDTO);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatus(
            ResponseStatusException e) {

        HttpStatusCode statusCode = e.getStatusCode();
        String message = e.getReason();
        if (message == null){
            message = e.getMessage();
        }

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                LocalDateTime.now(),
                statusCode.value(),
                message
        );

        return ResponseEntity.status(statusCode).body(errorResponseDTO);
    }

}
