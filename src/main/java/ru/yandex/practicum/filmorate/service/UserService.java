package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
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
        User userOne = userStorage.get(id);
        User userTwo = userStorage.get(friendId);
        Set<Long> friendsOfUserOne = userOne.getFriends();
        Set<Long> friendsOfUserTwo = userTwo.getFriends();
        friendsOfUserOne.add(friendId);
        friendsOfUserTwo.add(id);
        userOne.setFriends(friendsOfUserOne);
        userTwo.setFriends(friendsOfUserTwo);
        return userOne;
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
