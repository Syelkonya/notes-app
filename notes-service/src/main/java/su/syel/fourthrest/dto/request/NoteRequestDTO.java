package su.syel.fourthrest.dto.request;


import jakarta.validation.constraints.NotBlank;

public record NoteRequestDTO (
    @NotBlank String title,
    @NotBlank String content
){
}
