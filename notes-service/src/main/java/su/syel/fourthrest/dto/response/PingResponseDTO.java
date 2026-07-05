package su.syel.fourthrest.dto.response;

import java.time.LocalDateTime;

public record PingResponseDTO(
        int statusCode,
        LocalDateTime timestamp
) {
}
