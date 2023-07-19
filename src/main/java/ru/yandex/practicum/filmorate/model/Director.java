package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private int id;
    @NotBlank
    private String name;
    @JsonIgnore
    private Boolean deleted;

    public Director(int id, String name, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
    }
}
