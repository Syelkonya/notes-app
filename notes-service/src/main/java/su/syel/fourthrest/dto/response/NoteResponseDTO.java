package su.syel.fourthrest.dto.response;

import java.time.LocalDateTime;

public record NoteResponseDTO(
        Long id,
        String title,
        String content,
        LocalDateTime timestamp
) {
}
