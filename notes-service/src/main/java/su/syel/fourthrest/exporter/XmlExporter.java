package su.syel.fourthrest.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import su.syel.fourthrest.dto.response.NoteResponseDTO;

import java.util.List;

@Component("xml")
@RequiredArgsConstructor
public class XmlExporter implements NoteExporter {

    private final XmlMapper xmlMapper;

    @Override
    public String export(List<NoteResponseDTO> noteResponseDTOList) {
        try {
            return xmlMapper.writeValueAsString(noteResponseDTOList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to export JSON", e);
        }
    }

    @Override
    public String contentType() {
        return "application/xml";
    }
}
