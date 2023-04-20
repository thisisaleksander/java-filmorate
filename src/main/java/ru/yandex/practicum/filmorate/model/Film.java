package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@Builder
@Component
@Table(name = "FILMS")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int filmId;
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    //@JsonIgnore
    //private Set<Long> userIds = new HashSet<>();
    //@JsonIgnore
    private long rate;
    private String raiting;


    public void addLike(long userId) {
        //userIds.add(userId);
        //rate = userIds.size();
    }

    public void removeLike(long userId) {
        //userIds.remove(userId);
        //rate = userIds.size();
    }

    public int countLikes() {
        //return userIds.size();
        return 1;
    }

    public boolean isAlreadyLikedBy(long id) {
        //return userIds.contains(id);
        return true;
    }
}
