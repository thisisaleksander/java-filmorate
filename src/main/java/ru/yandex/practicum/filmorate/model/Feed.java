package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class Feed {
    private Instant timestamp;
    private Integer userId;
    private EventType eventType;
    private OperationType operation;
    private Integer eventId;
    private Integer entityId;
}
