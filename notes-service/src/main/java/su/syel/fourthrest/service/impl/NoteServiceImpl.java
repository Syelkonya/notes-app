package su.syel.fourthrest.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import su.syel.fourthrest.config.ConfigProperties;
import su.syel.fourthrest.dto.request.NoteRequestDTO;
import su.syel.fourthrest.dto.response.NoteResponseDTO;
import su.syel.fourthrest.entity.NoteEntity;
import su.syel.fourthrest.service.NoteService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final ConfigProperties configProperties;
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<Long, NoteEntity> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);
    private final AtomicLong idCounterStorageLength = new AtomicLong(0);

    {
        storage.put(idCounter.incrementAndGet(), new NoteEntity(idCounter.get(), "ВАУ", "СИЛЬНО ВАУ-ВАУ", LocalDateTime.now()));
        idCounterStorageLength.incrementAndGet();
        storage.put(idCounter.incrementAndGet(), new NoteEntity(idCounter.get(), "Тачки", "Супра, крузер", LocalDateTime.now()));
        idCounterStorageLength.incrementAndGet();
    }

    @PostConstruct
    public void init() {
        Gauge.builder("notes.current.count", idCounterStorageLength, AtomicLong::get)
                .description("Current notes count")
                .register(meterRegistry);
    }

    @Override
    public NoteResponseDTO createNote(NoteRequestDTO noteRequestDTO) {
        synchronized (storage) {
            if (idCounterStorageLength.get() >= configProperties.getMaxCount()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Notes limit exceeded");
            }
            Long id = idCounter.incrementAndGet();
            NoteEntity note = new NoteEntity(id, noteRequestDTO.title(), noteRequestDTO.content(), LocalDateTime.now());
            storage.put(id, note);
            idCounterStorageLength.incrementAndGet();
            return new NoteResponseDTO(id, note.getTitle(), note.getContent(), note.getTimestamp());
        }
    }


    @Override
    public NoteResponseDTO getNote(long id) {
        NoteEntity note = storage.get(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        return new NoteResponseDTO(id, note.getTitle(), note.getContent(), note.getTimestamp());
    }

    @Override
    public NoteResponseDTO updateNote(long id, NoteRequestDTO noteRequestDTO) {
        NoteEntity note = storage.get(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        note.setTitle(noteRequestDTO.title());
        note.setContent(noteRequestDTO.content());
        storage.put(id, note);
        return new NoteResponseDTO(id, note.getTitle(), note.getContent(), note.getTimestamp());
    }

    @Override
    public void deleteNote(long id) {
        synchronized (storage) {
            NoteEntity note = storage.get(id);
            if (note == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
            }
            storage.remove(id);
            idCounterStorageLength.decrementAndGet();
        }
    }

    @Override
    public List<NoteResponseDTO> getAllNotes() {
        return storage.values().stream()
                .map(note -> new NoteResponseDTO(note.getId(), note.getTitle(), note.getContent(), note.getTimestamp()))
                .toList();
    }

    @Override
    public long getStorageSize() {
        return idCounterStorageLength.get();
    }
}
