package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private final int id;
    @NonNull
    private String email;
    @NonNull
    private String login;
    private final String name;
    private final LocalDate birthday;
}
