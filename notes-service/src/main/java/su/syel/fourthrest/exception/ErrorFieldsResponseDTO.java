package su.syel.fourthrest.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorFieldsResponseDTO(
        LocalDateTime timestamp,
        int status,
        List<FieldErrorDetail> errors,
        String message
) {}