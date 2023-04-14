package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    @JsonIgnore
    private transient Set<Long> friendIds = new HashSet<>();

    public void addFriend(long id) {
        this.friendIds.add(id);
    }

    public void removeFriend(long id) {
        this.friendIds.remove(id);
    }

    public boolean isAlreadyFriendWith(long id) {
        return friendIds.contains(id);
    }
}
