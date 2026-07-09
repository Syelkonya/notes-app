package su.syel.fourthrest.service;

import su.syel.fourthrest.dto.request.NoteRequestDTO;
import su.syel.fourthrest.dto.response.NoteResponseDTO;

import java.util.List;

public interface NoteService {

    NoteResponseDTO createNote(NoteRequestDTO noteRequestDTO);

    NoteResponseDTO getNote(long id);

    NoteResponseDTO updateNote(long id, NoteRequestDTO noteRequestDTO);

    void deleteNote(long id);

    List<NoteResponseDTO> getAllNotes();

    long getStorageSize();
}
