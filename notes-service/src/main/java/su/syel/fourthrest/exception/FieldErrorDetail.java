package su.syel.fourthrest.exception;

public record FieldErrorDetail(
        String field,
        String message
) {}
