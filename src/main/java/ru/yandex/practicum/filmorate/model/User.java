package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private final int id;
    private String email;
    private String login;
    private String name;
    private final LocalDate birthday;
}
