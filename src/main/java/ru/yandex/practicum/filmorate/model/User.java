package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private final int id;
    private String email;
    private String login;
    private final String name;
    private final LocalDate birthday;
}
