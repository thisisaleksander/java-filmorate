package ru.yandex.practicum.filmorate.model;

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
    Set<Long> friends = new HashSet<>();

    public int countFriends() {
        return friends.size();
    }

    public void addFriend(long id) {
        this.friends.add(id);
    }

    public void removeFriend(long id) {
        this.friends.remove(id);
    }

    public boolean isAlreadyFriendWith(long id) {
        return friends.contains(id);
    }
}
