package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(long id, long friendId) {
        User userX = userStorage.get(id);
        if (userX.getFriends().contains(friendId)) {
            throw new AlreadyExistException(String.format(
                    "User with id = %s is already friend of user with id = %s",
                    id,
                    friendId
            ));
        }
        userX.getFriends().add(friendId);
        return userX;
    }

    public User removeFriend(long id, long friendId) {
        User userX = userStorage.get(id);
        if (!userX.getFriends().contains(friendId)) {
            throw new DoNotExistException(String.format(
                    "User with id = %s is not friend of user with id = %s",
                    id,
                    friendId
            ));
        }
        userX.getFriends().remove(friendId);
        return userX;
    }

    public Set<User> getFriends(long id) {
        Set<User> friends = new HashSet<>();
        Set<Long> friendsId = userStorage.get(id).getFriends();
        friendsId.forEach(
                value -> friends.add(userStorage.get(value))
        );
        return friends;
    }

    public Set<User> getMutualFriends(long id, long otherId) {
        Set<Long> s1 = userStorage.get(id).getFriends();
        Set<Long> s2 = userStorage.get(otherId).getFriends();
        Set<User> commonFriends = new HashSet<>();
        if (s1 == null || s2 == null) {
            return commonFriends;
        } else {
            for (Long friendId : s1) {
                if (s2.contains(friendId)) {
                    commonFriends.add(userStorage.get(friendId));
                }
            }
            return commonFriends;
        }
    }
}
