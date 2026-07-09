package su.syel.fourthrest.exporter;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;
import su.syel.fourthrest.dto.response.NoteResponseDTO;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Component("csv")
public class CsvNoteExporter implements NoteExporter {

    @Override
    public String export(List<NoteResponseDTO> noteResponseDTOList) {
        StringWriter writer = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(new String[]{"id", "title", "content", "timestamp"});
            noteResponseDTOList.forEach(note ->
                    csvWriter.writeNext(new String[]{
                            String.valueOf(note.id()),
                            note.title(),
                            note.content(),
                            String.valueOf(note.timestamp())
                    }));
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV", e);
        }

        return writer.toString();
    }

    @Override
    public String contentType() {
        return "text/csv";
    }
}
