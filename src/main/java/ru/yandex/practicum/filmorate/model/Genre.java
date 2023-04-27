package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Builder
@Component
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "GENRES")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max = 25)
    private String name;

    public int getGenreIdToCompare(Genre genre) {
        return genre.id;
    }
}