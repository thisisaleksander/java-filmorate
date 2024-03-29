package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@Component
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "FILMS")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @Min(0)
    private Integer rate;
    private Boolean deleted;
    private transient Mpa mpa;
    private transient Set<Genre> genres;
    private transient Set<Director> directors;
    private final transient List<Review> reviews = new ArrayList<>();

    public int getFilmIdToCompare(Film film) {
        return film.id;
    }

    public int getFilmRateToCompare(Film film) {
        return film.rate;
    }
}
