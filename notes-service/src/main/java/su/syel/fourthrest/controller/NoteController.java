package su.syel.fourthrest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.syel.fourthrest.dto.request.NoteRequestDTO;
import su.syel.fourthrest.dto.response.NoteResponseDTO;
import su.syel.fourthrest.exporter.ExportResult;
import su.syel.fourthrest.exporter.ExportService;
import su.syel.fourthrest.service.NoteService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final ExportService exportService;

    @PostMapping("/notes")
    public ResponseEntity<NoteResponseDTO> createNote(
            @Valid @RequestBody NoteRequestDTO note
    ) {
        log.info("Creating note title={}", note.title());
        NoteResponseDTO createdNoteDTO = noteService.createNote(note);
        log.info("Note created id={}", createdNoteDTO.id());
        URI location = URI.create("/notes/" + createdNoteDTO.id());
        return ResponseEntity.created(location).body(createdNoteDTO);
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<NoteResponseDTO> getNote(
            @PathVariable Long id
    ) {
        log.info("Getting note id={}", id);
        NoteResponseDTO noteResponseDTO = noteService.getNote(id);
        return ResponseEntity.ok().body(noteResponseDTO);
    }

    @PutMapping("/notes/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequestDTO note
    ) {
        log.info("Updating note id={}", id);
        NoteResponseDTO updatedNote = noteService.updateNote(id, note);
        log.info("Note updated id={}", id);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<NoteResponseDTO> deleteNote(
            @PathVariable Long id
    ) {
        log.info("Deleting note id={}", id);
        noteService.deleteNote(id);
        log.info("Note deleted id={}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/notes/export")
    public ResponseEntity<String> exportNote(
            @RequestParam String format
    ) {
        log.info("Exporting notes format={}", format);
        ExportResult result = exportService.export(format);
        log.info("Export completed format={}", format);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, result.contentType())
                .body(result.content());
    }
}