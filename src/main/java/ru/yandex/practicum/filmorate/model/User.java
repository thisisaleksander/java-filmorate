package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    @NonNull
    private int id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public User (String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
