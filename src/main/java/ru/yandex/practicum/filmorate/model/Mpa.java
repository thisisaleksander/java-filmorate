package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "MPA")
public class Mpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max = 25)
    private String name;
    @JsonIgnore
    private Boolean deleted;

    public Mpa(int mpaId) {
        this.id = mpaId;
    }
}
