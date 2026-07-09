package su.syel.fourthrest.exporter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import su.syel.fourthrest.dto.response.NoteResponseDTO;
import su.syel.fourthrest.service.NoteService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final Map<String, NoteExporter> exporters;
    private final NoteService noteService;

    public ExportResult export(String format){
        NoteExporter noteExporter = exporters.get(format);
        if (noteExporter == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown format");
        }
        List<NoteResponseDTO> noteResponseDTOList = noteService.getAllNotes();
        return new ExportResult(noteExporter.export(noteResponseDTOList), noteExporter.contentType());
    }

}
