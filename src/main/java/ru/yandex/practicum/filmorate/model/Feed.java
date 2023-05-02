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
    Instant timestamp;
    Integer userId;
    EventType eventType;
    OperationType operation;
    Integer eventId;
    Integer entityId;
}
