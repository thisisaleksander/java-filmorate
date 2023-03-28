package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.util.Date;

@Data
@AllArgsConstructor
public class Film {
    private final int id;
    private final String name;
    private String description;
    private final Date releaseDate;
    private final Duration duration;
}
