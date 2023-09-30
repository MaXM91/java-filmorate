package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
@Qualifier("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (email, login, name, birthday) values (?, ?, ?, ?)", new String[]{"id"});
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getLogin());
                preparedStatement.setString(3, user.getName());
                preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
                return preparedStatement;
            }, keyHolder);

            user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

            log.info("UserService/create: user with id - {} created", user.getId());

            return user;
    }

    @Override
    public User update(User user) {
            jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

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
            Set<Integer> friends = new HashSet<>();
            int numberRows;

            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT *\n" +
                    "FROM users AS u\n" +
                    "LEFT JOIN friends AS f ON u.id = f.user_id_from\n" +
                    "WHERE u.id = ?\n" +
                    "GROUP BY u.id, f.user_id_to", id);

            numberRows = checkNumberRows(rs);

            if (numberRows == 0) {
                throw new ObjectNotFoundException(" user with id - " + id + " not found!");
            }

            rs.first();

            if (numberRows == 1) {
                friends.add(rs.getInt("user_id_to"));
            } else {
                for (int i = 0; i < numberRows; i++) {
                    friends.add(rs.getInt("user_id_to"));
                    rs.next();
                }
            rs.last();
            }

            return new User(rs.getInt("id"),
                friends,
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate());
    }

    @Override
    public List<User> get() {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT *\n" +
                    "FROM users AS u\n" +
                    "LEFT JOIN friends AS f ON u.id = f.user_id_from\n" +
                    "GROUP BY u.id, f.user_id_to");

            return creatUsersFromRows(rs, checkNumberRows(rs), "id");
    }

    @Override
    public List<User> getFriends(Integer id) {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT f.user_id_to, u.email, u.login, u.name, u.birthday, fr.user_id_to\n" +
                    "FROM friends AS f\n" +
                    "LEFT JOIN users AS u ON f.user_id_to = u.id\n" +
                    "LEFT JOIN friends AS fr ON u.id = fr.user_id_from \n" +
                    "WHERE f.user_id_from = ?\n" +
                    "GROUP BY f.user_id_to,  u.email, u.login, u.name, u.birthday, fr.user_id_to", id);

            List<User> users = creatUsersFromRows(rs, checkNumberRows(rs), "user_id_to");

            log.info("UserDbStorage/getFriends: user friends have been got, id - {}", id);

            return users;
    }

    @Override
    public void addFriend(Integer id, Integer idFriend) {
            jdbcTemplate.update("INSERT INTO friends (user_id_from, user_id_to) VALUES(?,?)", id, idFriend);
    }

    @Override
    public void deleteFriend(Integer id, Integer idFriend) {
            jdbcTemplate.update("DELETE FROM friends WHERE user_id_from = ? AND user_id_to = ?", id, idFriend);
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                ("SELECT f.user_id_to, u.email, u.login, u.name, u.birthday, fri.user_id_to\n" +
                    "FROM friends AS f\n" +
                    "LEFT JOIN users AS u ON u.id = f.user_id_to\n" +
                    "LEFT JOIN friends AS fr ON f.user_id_to = fr.user_id_to \n" +
                    "LEFT JOIN friends AS fri ON f.user_id_to = fri.user_id_from\n" +
                    "WHERE f.user_id_from = ? AND fr.user_id_from = ?"), id, otherId);

            List<User> users = creatUsersFromRows(rs, checkNumberRows(rs), "user_id_to");

            log.info("UserDbStorage/getMutualFriends: getting mutual friends, ids - {}, {}",id, otherId);

            return users;
    }

    private Integer checkNumberRows(SqlRowSet rs) {
        rs.last();
        return rs.getRow();
    }

    private List<User> creatUsersFromRows(SqlRowSet rs, Integer numberRows, String id) {
        Map<Integer, User> users = new HashMap<>();
        Set<Integer> friends;

        if (numberRows == 0) {
            return new ArrayList<>();
        }

        rs.first();

        for (int i = 0; i < numberRows; i++) {
            if (users.containsKey(rs.getInt(id))) {
                friends = users.get(rs.getInt(id)).getUserFriends();
                friends.add(rs.getInt("user_id_to"));
                users.get(rs.getInt(id)).setUserFriends(friends);
            } else {
                User user = new User(rs.getInt(id),
                    new HashSet<>(rs.getInt("user_id_to")),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    Objects.requireNonNull(rs.getDate("birthday")).toLocalDate());

                users.put(rs.getInt(id), user);
            }
            rs.next();
        }
        return new ArrayList<>(users.values());
    }
}
