package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private int id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    Set<Long> friends;

    private int countFriends() {
        return friends.size();
    }

    public int compareByFriends(User o1, User o2) {
        return o1.countFriends() - o2.countFriends();
    }
}
