package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;

@Data
@AllArgsConstructor
@Builder
public class Feed {
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private OperationType operation;
    private Integer eventId;
    private Integer entityId;
    @JsonIgnore
    private Boolean deleted;
}
