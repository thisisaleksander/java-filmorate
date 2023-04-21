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
    private Integer rate;
    private String rating;
}
