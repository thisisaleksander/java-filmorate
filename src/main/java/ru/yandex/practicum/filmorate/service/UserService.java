package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    String END_DATE = "9999-12-31";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public UserService(UserDbStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> acceptFriend(Integer id, Integer friendId) {
        Optional<User> optionalUser = userStorage.get(id);
        Optional<User> optionalFriend = userStorage.get(friendId);
        User user;
        if (optionalUser.isPresent() && optionalFriend.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new DoNotExistException(String.format(
                    "User with id %s or %s do not exist",
                    id,
                    friendId
            ));
        }
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select * from friends " +
                        "where (user_id = ? and friend_id = ?) " +
                        "and (status_id = 1)",
                friendId,
                id);
        if (resultSet.next()) {
            String sqlQuery1 = "update friends set " +
                    "status_end = ?" +
                    " where (user_id = ? and friend_id = ?) and (status_id = 1)";
            jdbcTemplate.update(sqlQuery1,
                    friendId,
                    id,
                    Instant.now()
            );
            String sqlQuery2 = "insert into friends (user_id, friend_id, status_id, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery2,
                    friendId,
                    id,
                    2,
                    Instant.now(),
                    LocalDate.parse(END_DATE, formatter)
            );
            log.info("Add friend {} to user {} with status {}", friendId, id, 2);
            return Optional.of(user);
        } else {
            throw new DoNotExistException(String.format(
                    "No friend request for user %s and user %s",
                    id,
                    friendId
            ));
        }
    }

    public Optional<User> addFriend(Integer id, Integer friendId) {
        Optional<User> optionalUser = userStorage.get(id);
        Optional<User> optionalFriend = userStorage.get(friendId);
        User user;
        if (optionalUser.isPresent() && optionalFriend.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new DoNotExistException(String.format(
                    "User with id %s or %s do not exist",
                    id,
                    friendId
            ));
        }
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "select * from friends " +
                        "where (user_id = ? and friend_id = ?) " +
                        "or (user_id = ? and friend_id = ?) " +
                        "and (status_id = 1)",
                friendId,
                id,
                id,
                friendId);
        if (resultSet.next()) {
            throw new AlreadyExistException(String.format(
                    "Friend request from user id %s to user %s already exist",
                    id,
                    friendId
            ));
        } else {
            String sqlQuery = "insert into friends (user_id, friend_id, status_id, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    friendId,
                    1,
                    Instant.now(),
                    LocalDate.parse(END_DATE, formatter)
            );
            log.info("Add friend request from user {} to user {} with status {}", friendId, id, 1);
            return Optional.of(user);
        }
    }

    public Optional<User> removeFriend(Integer id, Integer friendId) {
        Optional<User> optionalUser = userStorage.get(id);
        Optional<User> optionalFriend = userStorage.get(friendId);
        User user;
        if (optionalUser.isPresent() && optionalFriend.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new DoNotExistException(String.format(
                    "User with id %s or %s do not exist",
                    id,
                    friendId
            ));
        }
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "select * from friends " +
                        "where (user_id = ? and friend_id = ?) " +
                        "or (user_id = ? and friend_id = ?) " +
                        "and (status_id = 2)",
                friendId,
                id,
                id,
                friendId);
        if (resultSet.next()) {
            String sqlQuery1 = "update friends set " +
                    "status_end = ?" +
                    "where (user_id = ? and friend_id = ?) " +
                    "or (user_id = ? and friend_id = ?) " +
                    "and (status_id = 2)";
            jdbcTemplate.update(sqlQuery1,
                    Instant.now(),
                    id,
                    friendId,
                    friendId,
                    id
            );
            String sqlQuery2 = "insert into friends (user_id, friend_id, status_id, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery2,
                    id,
                    friendId,
                    3,
                    Instant.now(),
                    LocalDate.parse(END_DATE, formatter)
            );
            return Optional.of(user);
        } else {
            throw new DoNotExistException(String.format(
                    "User with id %s not a friend to user %s",
                    id,
                    friendId
            ));
        }
    }

    public List<Optional<User>> getFriends(Integer id) {
        List<Optional<User>> friends = new ArrayList<>();
        Set<Integer> friendsIds = new HashSet<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select distinct user_id, friend from friends where (user_id = " +
                id + "or friend_id = " + id + ") and (status_id = 2)");
        if(resultSet.next()) {
            while (resultSet.next()) {
                friendsIds.add(resultSet.getInt("user_id"));
                friendsIds.add(resultSet.getInt("friend_id"));
            }
            friendsIds.remove(id);
            log.info("Total friends found: {}", friendsIds.size());
            friendsIds.forEach(someId -> friends.add(userStorage.get(someId)));
            return friends;
        } else {
            log.info("No friends found");
            return Collections.emptyList();
        }
    }

    public Set<Optional<User>> getMutualFriends(Integer id, Integer otherId) {
        Set<Optional<User>> commonFriends = new HashSet<>();
        Set<Integer> commonFriendsIds = new HashSet<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "select f1.user_id, f1.friend_id, f2.user_id, f2.friend_id from friends as f1 where (f1.user_id = " +
                        id + " or f1.friend_id = " + id + ") and (status_id = 2) and f2.user_id is not null" +
                        "left join (select distinct user_id, friend from friends where (user_id = " +
                        otherId + " or friend_id = " + otherId + ") and (status_id = 2)) as f2" +
                        "on f2.user_id = f2.user_id or f1.friend_id = f2.friend_id"
        );
        if(resultSet.next()) {
            while (resultSet.next()) {
                commonFriendsIds.add(resultSet.getInt("f1.user_id"));
                commonFriendsIds.add(resultSet.getInt("f1.friend_id"));
                commonFriendsIds.add(resultSet.getInt("f2.user_id"));
                commonFriendsIds.add(resultSet.getInt("f2.friend_id"));
            }
            commonFriendsIds.remove(id);
            commonFriendsIds.remove(otherId);
            log.info("Total common friends found: {}", commonFriendsIds.size());
            commonFriendsIds.forEach(someId -> commonFriends.add(userStorage.get(someId)));
            return commonFriends;
        } else {
            log.info("No common friends found");
            return Collections.emptySet();
        }
    }
}
