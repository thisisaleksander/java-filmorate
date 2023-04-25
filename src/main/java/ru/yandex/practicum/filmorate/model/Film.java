package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
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

    public int getFilmIdToCompare(Film film) {
        return film.id;
    }
}
