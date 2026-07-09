package su.syel.fourthrest.exporter;

import su.syel.fourthrest.dto.response.NoteResponseDTO;

import java.util.List;

public interface NoteExporter {
    String export(List<NoteResponseDTO> noteResponseDTOList);

    String contentType();
}
