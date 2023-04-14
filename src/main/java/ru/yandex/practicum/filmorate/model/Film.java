package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Data
public class Film {
    private int id;
    private String name;
    @Size(max = 200)
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @JsonIgnore
    private Set<Long> userIds = new HashSet<>();

    @JsonIgnore
    private long rate = 0;

    public void addLike(long userId) {
        userIds.add(userId);
        rate = userIds.size();
    }

    public void removeLike(long userId) {
        userIds.remove(userId);
        rate = userIds.size();
    }

    public int countLikes() {
        return userIds.size();
    }

    public boolean isAlreadyLikedBy(long id) {
        return userIds.contains(id);
    }
}
