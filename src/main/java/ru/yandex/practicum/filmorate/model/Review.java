package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Review {
    private Integer reviewId;
    @NotNull
    private Integer filmId;
    @NotNull
    private Integer userId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    private int useful;
    private final List<Integer> usersDislikes = new ArrayList<>();
    private final List<Integer> usersLikes = new ArrayList<>();
    @JsonIgnore
    private Boolean deleted;
}
