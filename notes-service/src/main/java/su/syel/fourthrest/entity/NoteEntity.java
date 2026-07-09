package su.syel.fourthrest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoteEntity {
    Long id;
    String title;
    String content;
    LocalDateTime timestamp;
}
