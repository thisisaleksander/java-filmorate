package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.storage.Constants.*;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userStorage;
    private final FeedDbStorage feedDbStorage;
    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorage userStorage, FeedDbStorage feedDbStorage,
                       JdbcTemplate jdbcTemplate, FilmDbStorage filmStorage) {
        this.userStorage = userStorage;
        this.feedDbStorage = feedDbStorage;
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * method to accept friend request from user (table friends, updates status_id to STATUS_ACTIVE)
     * @param id -> int from request string, id of user who may accept a friend request
     * @param friendId -> int from request string, id of user who have sent a friend request
     * @return User -> user who accepts friend request
     */
    public User acceptFriend(Integer id, Integer friendId) {
        User user = userStorage.get(id);
        userStorage.get(friendId);
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM friends " +
                        "WHERE (user_id = ? AND friend_id = ?) " +
                        "AND (status_id = ?)",
                friendId,
                id,
                STATUS_REQUEST
        );
        if (resultSet.next()) {
            String sqlQuery = "UPDATE friends SET " +
                    "status_id = ?" +
                    " WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery,
                    STATUS_ACTIVE,
                    friendId,
                    id
            );
            log.info("Accept friend {} to user {} with status {}", friendId, id, 2);
            return user;
        } else {
            throw new DoNotExistException(String.format(
                    "No friend request for user %s and user %s",
                    id,
                    friendId
            ));
        }
    }

    /**
     * method to send friend request from user (table friends, sets status_id to STATUS_REQUEST)
     * @param id -> int from request string, id of user who sends a friend request
     * @param friendId -> int from request string, id of user who will receive a friend request
     * @return User -> user who sends friend request
     */
    public User addFriend(Integer id, Integer friendId) {
        User user = userStorage.get(id);
        userStorage.get(friendId);
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "SELECT * FROM friends " +
                        "WHERE (user_id = ? AND friend_id = ?) " +
                        "AND (status_id = ?)",
                id,
                friendId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            throw new AlreadyExistException(String.format(
                    "Friend request or from user id %s to user %s already exist, or active users are already friends",
                    id,
                    friendId
            ));
        } else {
            String sqlQuery = "INSERT INTO friends (user_id, friend_id, status_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    friendId,
                    STATUS_ACTIVE
            );
            log.info("Added friendship of user {} with user {}", friendId, id);
            if (feedDbStorage.addFriendSaveToFeed(friendId, id) == 0) {
                log.warn("'Add Friend' operation from user {} to friend {} was not saved to Feed", id, friendId);
            }
            return user;
        }
    }

    /**
     * method to remove friend (table friends, sets status_id to STATUS_DELETED)
     * @param id -> int from request string, id of user who deletes friend
     * @param friendId -> int from request string, id of a user to delete friendship with
     * @return User -> user who sends friend request
     */
    public User removeFriend(Integer id, Integer friendId) {
        User user = userStorage.get(id);
        userStorage.get(friendId);
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "SELECT * FROM friends " +
                        "WHERE (user_id = ? AND friend_id = ?) " +
                        "AND (status_id = ?)",
                id,
                friendId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            String sqlQuery1 = "UPDATE friends SET " +
                    "status_id = ? " +
                    "WHERE (user_id = ? AND friend_id = ?)";
            jdbcTemplate.update(sqlQuery1,
                    STATUS_DELETED,
                    id,
                    friendId
            );
            log.info("Removed friendship of user {} with user {}", friendId, id);
            if (feedDbStorage.removeFriendSaveToFeed(friendId, id) == 0) {
                log.warn("'Remove Friend' operation from user {} to friend {} was not saved to Feed", id, friendId);
            }
            return user;
        } else {
            throw new DoNotExistException(String.format(
                    "User with id %s not a friend to user %s",
                    id,
                    friendId
            ));
        }
    }

    /**
     * method that returns all friends (user objects) of a user with id from request
     * @param id -> int from request string, id of user whose friends will be found
     * @return List<User> -> List of user objects who have active friendship status with user (@param id)
     */
    public List<User> getFriends(Integer id) {
        SqlRowSet sqlValidate = jdbcTemplate.queryForRowSet("select * from users where id = " + id);
        if (!sqlValidate.next()) {
            throw new NotFoundException("This user does not exist");
        }
        List<User> friends = new ArrayList<>();
        Set<Integer> friendsIds = new HashSet<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select distinct friend_id from friends where user_id = " +
                id + " and status_id = " + STATUS_ACTIVE
        );
        while (resultSet.next()) {
            friendsIds.add(resultSet.getInt("friend_id"));
        }
        log.info("Total friends of user {} found: {}", id, friendsIds.size());
        friendsIds.forEach(someId -> {
            friends.add(userStorage.get(someId));
        });
        if (friends.isEmpty()) {
            log.info("No friends found");
        }
        return friends;
    }

    /**
     * method that returns all mutual friends if users with ids : @param id and @param otherId
     * @param id -> int from request string, id of a user
     * @param otherId -> int from request string, id of a user
     * @return Set<User> -> set of unique user objects who are friends of users with id and otherId
     */
    public Set<User> getMutualFriends(Integer id, Integer otherId) {
        Set<User> commonFriends = new HashSet<>();
        Set<Integer> commonFriendsIds = new HashSet<>();
        Set<Integer> friendsOfUserOne = new HashSet<>();
        Set<Integer> friendsOfUserTwo = new HashSet<>();
        SqlRowSet resultSetOfUserOne = jdbcTemplate.queryForRowSet(
                "SELECT DISTINCT friend_id FROM friends WHERE user_id = " + id + " AND status_id = " + STATUS_ACTIVE
        );
        SqlRowSet resultSetOfUserTwo = jdbcTemplate.queryForRowSet(
                "SELECT DISTINCT friend_id FROM friends WHERE user_id = " + otherId + " AND status_id = " + STATUS_ACTIVE
        );
        while (resultSetOfUserOne.next()) {
            friendsOfUserOne.add(resultSetOfUserOne.getInt("friend_id"));
        }
        while (resultSetOfUserTwo.next()) {
            friendsOfUserTwo.add(resultSetOfUserTwo.getInt("friend_id"));
        }
        for (Integer someId : friendsOfUserOne) {
            if (friendsOfUserTwo.contains(someId)) {
                commonFriendsIds.add(someId);
            }
        }
        commonFriendsIds.remove(id);
        commonFriendsIds.remove(otherId);
        log.info("Total common friends found of users {} and {} found: {}", id, otherId, commonFriendsIds.size());
        commonFriendsIds.forEach(someId -> {
            commonFriends.add(userStorage.get(someId));
        });
        if (commonFriends.isEmpty()) {
            log.info("No common friends found of users {} and {}", id, otherId);
            return Collections.emptySet();
        }
        return commonFriends;
    }

    public User get(@NonNull Integer id) {
        return userStorage.get(id);
    }

    public List<Film> getRecommendations(Integer userId) {
        List<Film> recommendationsFilms = new ArrayList<>();
        SqlRowSet sql = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE STATUS_ID != 3");
        HashMap<Integer, List<Integer>> likes = new HashMap<>();
        while (sql.next()) {
            int user = sql.getInt("user_id");
            if (!likes.containsKey(sql.getInt("user_id"))) {
                likes.put(user, new ArrayList<>());
                likes.get(user).add(sql.getInt("film_id"));
            } else {
                likes.get(user).add(sql.getInt("film_id"));
            }
        }
        if (!likes.containsKey(userId) || likes.size() == 1) {
            return new ArrayList<>();
        } else {
            int commonLikes = 0;
            int commonUser = 0;
            for (int i = 1; i <= likes.size(); i++) {
                List<Integer> commonValues = new ArrayList<>(likes.get(userId));
                if (i == userId) {
                    continue;
                }
                commonValues.retainAll(likes.get(i));
                if (commonValues.size() > commonLikes) {
                    commonLikes = commonValues.size();
                    commonUser = i;
                }
            }
            List<Integer> recommendationsId = new ArrayList<>(likes.get(commonUser));
            recommendationsId.removeAll(likes.get(userId));
            for (Integer id : recommendationsId) {
                recommendationsFilms.add(filmStorage.get(id));
            }
            return recommendationsFilms;
        }
    }

    public Collection<Feed> getUsersActionFeed(Integer id) {
        userStorage.get(id);
        return feedDbStorage.getUsersActionFeed(id);
    }
}