package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(long id, long friendId) {
        Set<Long> friends_1 = new HashSet<>();
        Set<Long> friends_2 = new HashSet<>();
        User user_1 = userStorage.get(id);
        User user_2 = userStorage.get(friendId);
        if (user_1.getFriends() == null) {
            friends_1.add(friendId);
            user_1.setFriends(friends_1);
        }
        if (user_2.getFriends() == null) {
            friends_2.add(id);
            user_2.setFriends(friends_2);
        }
        if (user_1.getFriends().contains(friendId) || user_2.getFriends().contains(id)) {
            throw new AlreadyExistException(String.format(
                    "User with id = %s is already friend of user with id = %s",
                    id,
                    friendId
            ));
        } else {
            friends_1 = user_1.getFriends();
            friends_2 = user_2.getFriends();
            friends_1.add(friendId);
            friends_2.add(id);
            user_1.setFriends(friends_1);
            user_2.setFriends(friends_2);
        }
        return user_1;
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
        Set<Long> friends = new HashSet<>();
        friends = userX.getFriends();
        friends.remove(friendId);
        userX.setFriends(friends);
        return userX;
    }

    public List<User> getFriends(long id) {
        List<User> friends = new ArrayList<>();
        Set<Long> friendsId = userStorage.get(id).getFriends();
        if (friendsId == null) {
            return friends;
        } else {
            friendsId.forEach(
                    value -> friends.add(userStorage.get(value))
            );
        }
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
