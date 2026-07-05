package su.syel.fourthrest.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import su.syel.fourthrest.config.ConfigProperties;
import su.syel.fourthrest.service.NoteService;

@Component
@RequiredArgsConstructor
public class NotesLimitHealthIndicator implements HealthIndicator {

    private final NoteService noteService;
    private final ConfigProperties configProperties;

    @Override
    public Health health() {
        long current = noteService.getStorageSize();
        long max = configProperties.getMaxCount();

        if (current >= max) {
            return Health.down()
                    .withDetail("reason", "Notes limit reached")
                    .withDetail("current", current)
                    .withDetail("max", max)
                    .build();
        }

        return Health.up()
                .withDetail("current", current)
                .withDetail("max", max)
                .build();
    }
}