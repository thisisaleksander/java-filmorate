package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> acceptFriend(long id, long friendId) {
        Optional<User> optionalUser = userStorage.get(id);
        Optional<User> optionalFriend = userStorage.get(friendId);
        User user = null;
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
                        "and (status = 1)",
                friendId,
                id);
        if (resultSet.next()) {
            String sqlQuery1 = "update friends set " +
                    "status_end = ?" +
                    " where (user_id = ? and friend_id = ?) and (status = 1)";
            jdbcTemplate.update(sqlQuery1,
                    friendId,
                    id,
                    Instant.now()
            );
            String sqlQuery2 = "insert into friends (user_id, friend_id, status, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery2,
                    friendId,
                    id,
                    2,
                    Instant.now(),
                    Instant.parse("9999-12-31")
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

    public Optional<User> addFriend(long id, long friendId) {
        Optional<User> optionalUser = userStorage.get(id);
        Optional<User> optionalFriend = userStorage.get(friendId);
        User user = null;
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
                        "and (status = 1)",
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
            String sqlQuery = "insert into friends (user_id, friend_id, status, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    friendId,
                    1,
                    Instant.now(),
                    Instant.parse("9999-12-31")
            );
            log.info("Add friend request from user {} to user {} with status {}", friendId, id, 1);
            return Optional.of(user);
        }
    }

    public Optional<User> removeFriend(long id, long friendId) {
        Optional<User> optionalUser = userStorage.get(id);
        Optional<User> optionalFriend = userStorage.get(friendId);
        User user = null;
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
                        "and (status = 2)",
                friendId,
                id,
                id,
                friendId);
        if (resultSet.next()) {
            String sqlQuery1 = "update friends set " +
                    "status_end = ?" +
                    "where (user_id = ? and friend_id = ?) " +
                    "or (user_id = ? and friend_id = ?) " +
                    "and (status = 2)";
            jdbcTemplate.update(sqlQuery1,
                    Instant.now(),
                    id,
                    friendId,
                    friendId,
                    id
            );
            String sqlQuery2 = "insert into friends (user_id, friend_id, status, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery2,
                    id,
                    friendId,
                    3,
                    Instant.now(),
                    Instant.parse("9999-12-31")
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

    public List<Optional<User>> getFriends(long id) {
        List<Optional<User>> friends = new ArrayList<>();
        Set<Long> friendsdIds = new HashSet<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select distinct user_id, friend from friends where (user_id = " +
                id + "or friend_id = " + id + ") and (status = 2)");
        if(resultSet.next()) {
            while (resultSet.next()) {
                friendsdIds.add(resultSet.getLong("user_id"));
                friendsdIds.add(resultSet.getLong("friend_id"));
            }
            friendsdIds.remove(id);
            log.info("Total friends found: {}", friendsdIds.size());
            friendsdIds.forEach(someId -> friends.add(userStorage.get(someId)));
            return friends;
        } else {
            log.info("No friends found");
            return Collections.emptyList();
        }
    }

    public Set<Optional<User>> getMutualFriends(long id, long otherId) {
        Set<Optional<User>> commonFriends = new HashSet<>();
        Set<Long> comonFriendsdIds = new HashSet<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "select distinct f1.user_id, f1.friend_id, f2.user_id, f2.friend_id from friends as f1 where (user_id = " +
                        id + "or friend_id = " + id + ") and (status = 2)" +
                        "left join (select distinct user_id, friend from friends where (user_id = " +
                        otherId + " or friend_id = " + otherId + ") and (status = 2)) as f2" +
                        "on f2.user_id = f2.user_id or f1.friend_id = f2.friend_id"
        );
        if(resultSet.next()) {
            while (resultSet.next()) {
                comonFriendsdIds.add(resultSet.getLong("f1.user_id"));
                comonFriendsdIds.add(resultSet.getLong("f1.friend_id"));
                comonFriendsdIds.add(resultSet.getLong("f2.user_id"));
                comonFriendsdIds.add(resultSet.getLong("f2.friend_id"));
            }
            comonFriendsdIds.remove(id);
            comonFriendsdIds.remove(otherId);
            log.info("Total common friends found: {}", comonFriendsdIds.size());
            comonFriendsdIds.forEach(someId -> commonFriends.add(userStorage.get(someId)));
            return commonFriends;
        } else {
            log.info("No common friends found");
            return Collections.emptySet();
        }
    }
}
