package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (email, login, name, birthday)values (?, ?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {

        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        return user;
    }

    @Override
    public User delete(Integer id) {
        User user = found(id);

        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);

        return user;
    }

    @Override
    public User found(Integer id) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", (rs, rowNum) -> new User(rs.getInt("id"), rs.getString("email"), rs.getString("login"),
                    rs.getString("name"), rs.getDate("birthday").toLocalDate()), id);

            List<Integer> friends = foundFriendsIds(id);

            for (Integer friendId : friends) {
                assert user != null;
                user.setUserFriends(friendId);
            }

            return user;
        } catch (RuntimeException exc) {
            throw new ObjectNotFoundException("UserService/found: user not found!");
        }
    }

    @Override
    public List<User> get() {

        return jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> {
            User user = new User(rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"), rs.getString("name"),
                    rs.getDate("birthday").toLocalDate());

            List<Integer> friends = foundFriendsIds(rs.getInt("id"));

            for (Integer friendId : friends) {
                user.setUserFriends(friendId);
            }

            return user;
        });
    }

    @Override
    public List<User> getFriends(Integer id) {
        List<User> friends = new ArrayList<>();
        List<Integer> friendsIds = foundFriendsIds(id);

        for (Integer idFriend : friendsIds) {
            friends.add(found(idFriend));
        }
        return friends;
    }

    @Override
    public void addFriend(Integer id, Integer idFriend) {
        jdbcTemplate.update("INSERT INTO friends (user_id_from, user_id_to) VALUES(?,?)",
                id,
                idFriend
        );

        if (checkFriends(id, idFriend)) {
            jdbcTemplate.update("UPDATE friends SET approved_by_to = 1 WHERE user_id_from = ? AND user_id_to = ?", id, idFriend);
            jdbcTemplate.update("UPDATE friends SET approved_by_to = 1 WHERE user_id_from = ? AND user_id_to = ?", idFriend, id);
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer idFriend) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id_from = ? AND user_id_to = ?", id, idFriend);

        if (checkFriends(id, idFriend)) {
            jdbcTemplate.update("UPDATE friends SET approved_by_to = 0 WHERE user_id_from = ? AND user_id_to = ?", id, idFriend);
            jdbcTemplate.update("UPDATE friends SET approved_by_to = 0 WHERE user_id_from = ? AND user_id_to = ?", idFriend, id);
        }
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        List<User> friendsId = getFriends(id);
        List<User> friendsOtherId = getFriends(otherId);
        List<User> mutualFriends = new ArrayList<>();

        for (User user : friendsId) {
            if (friendsOtherId.contains(user)) {
                mutualFriends.add(user);
            }
        }

        return mutualFriends;
    }

    private List<Integer> foundFriendsIds(Integer id) {
        return jdbcTemplate.query("SELECT user_id_to FROM friends WHERE user_id_from = ?", (rs, rowNum) -> rs.getInt("user_id_to"), id);
    }

    private boolean checkFriends(Integer id, Integer idFriend) {
        List<User> checkFriend = getFriends(idFriend);

        for (User user : checkFriend) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }

}
