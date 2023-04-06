package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    Set<Long> likes;

    public int countLikes() {
        return likes.size();
    }

    public static int compareByLikes(Film o1, Film o2) {
        return o1.countLikes() - o2.countLikes();
    }
}
