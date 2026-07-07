package su.syel.fourthrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import su.syel.fourthrest.dto.request.NoteRequestDTO;
import su.syel.fourthrest.dto.response.NoteResponseDTO;
import su.syel.fourthrest.entity.NoteEntity;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface NoteMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    NoteEntity toEntity(NoteRequestDTO dto);

    NoteResponseDTO toResponseDTO(NoteEntity entity);

    default NoteEntity toEntityWithId(NoteRequestDTO dto, Long id) {
        NoteEntity entity = toEntity(dto);
        entity.setId(id);
        entity.setTimestamp(LocalDateTime.now());
        return entity;
    }
}