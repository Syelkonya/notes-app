package su.syel.fourthrest.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import su.syel.fourthrest.dto.response.NoteResponseDTO;

import java.util.List;

@Component("json")
@RequiredArgsConstructor
public class JsonExporter implements NoteExporter{

    private final ObjectMapper objectMapper;

    @Override
    public String export(List<NoteResponseDTO> noteResponseDTOList) {
        try {
            return objectMapper.writeValueAsString(noteResponseDTOList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to export JSON", e);
        }
    }

    @Override
    public String contentType() {
        return "application/json";
    }
}
