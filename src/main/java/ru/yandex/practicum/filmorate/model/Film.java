package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.util.Date;

@Data
@AllArgsConstructor
public class Film {
    @NonNull
    private int id;
    private String name;
    private String description;
    private Date releaseDate;
    private Duration duration;

    public Film (String name, String description, Date releaseDate, Duration duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
