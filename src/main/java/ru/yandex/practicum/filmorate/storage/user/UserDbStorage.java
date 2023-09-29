package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.WorkDBException;

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
        try {
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

            log.info("UserService/create: user with id - {} has been created", user.getId());

            return user;
        } catch (DataAccessException exp) {
            log.info("UserDbStorage/create: DB problem of creating a user");
            throw new WorkDBException("create user: problems with DB on creating a user");
        }
    }

    @Override
    public User update(User user) {
        try {
            jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

            log.info("UserService/update: user with id - {} has been update", user.getId());

            return user;
        } catch (DataAccessException exp) {
            log.info("UserDbStorage/update: DB problem of updating a user with id - {}", user.getId());
            throw new WorkDBException("update user: problems with DB on updating a user");
        }
    }

    @Override
    public User delete(Integer id) {
        User user = found(id);

        try {
            jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);

            log.info("UserDbStorage/delete: user with id - {} was deleted", id);

            return user;
        } catch (DataAccessException exp) {
            log.info("UserDbStorage/delete: DB problem of deleting a user with id - {}", id);
            throw new WorkDBException("delete user: problems with DB on deleting a user id - " + id);
        }
    }

    @Override
    public User found(Integer id) {
        try {
            Set<Integer> friends = new HashSet<>();

            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT *\n" +
                    "FROM users AS u\n" +
                    "LEFT JOIN friends AS f ON u.id = f.user_id_from\n" +
                    "WHERE u.id = ?\n" +
                    "GROUP BY u.id, f.user_id_to", id);

            rs.next();

            for (int i = 0; i < rs.getRow(); i++) {
                friends.add(rs.getInt("user_id_to"));
                rs.next();
            }

            rs.previous();

            return new User(rs.getInt("id"),
                friends,
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate());
        } catch (ArrayIndexOutOfBoundsException exp) {
            log.info("UserDbStorage/found: DB problem of founding a user with id - {}", id);
            throw new ObjectNotFoundException("found user: user with id - " + id + " not found!");
        } catch (DataAccessException exp) {
            log.info("UserDbStorage/found: DB problem of founding a user with id - {}", id);
            throw new WorkDBException("found user: problems with DB on founding a user id - " + id);
        }
    }

    @Override
    public List<User> get() {
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT *\n" +
                    "FROM users AS u\n" +
                    "LEFT JOIN friends AS f ON u.id = f.user_id_from\n" +
                    "GROUP BY u.id, f.user_id_to");

            List<User> users = creatUsersFromRows(rs, "id");

            log.info("UserDbStorage/get: users were got");

            return users;
        } catch (DataAccessException exc) {
            log.info("UserDbStorage/get: DB problem of getting a users");
            throw new WorkDBException("get user: problems with DB on getting a users");
        }
    }

    @Override
    public List<User> getFriends(Integer id) {
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT f.user_id_to, u.email, u.login, u.name, u.birthday, fr.user_id_to\n" +
                    "FROM friends AS f\n" +
                    "LEFT JOIN users AS u ON f.user_id_to = u.id\n" +
                    "LEFT JOIN friends AS fr ON u.id = fr.user_id_from \n" +
                    "WHERE f.user_id_from = ?\n" +
                    "GROUP BY f.user_id_to,  u.email, u.login, u.name, u.birthday, fr.user_id_to", id);

            List<User> users = creatUsersFromRows(rs, "user_id_to");

            log.info("UserDbStorage/getFriends: user friends have been got, id - {}", id);

            return users;
        } catch (DataAccessException exc) {
            log.info("UserDbStorage/getFriends: DB problem of getting user friends with id - {}", id);
            throw new WorkDBException("getFriends user: problems with DB getting user - " + id + " friends");
        }
    }

    @Override
    public void addFriend(Integer id, Integer idFriend) {
        try {
            jdbcTemplate.update("INSERT INTO friends (user_id_from, user_id_to) VALUES(?,?)", id, idFriend);

            log.info("UserDbStorage/deleteFriend: friend was added, ids - {}, {}",id, idFriend);

        } catch (DataAccessException exc) {
            log.info("UserDbStorage/addFriend: DB problem of adding friends, ids - {}, {}", id, idFriend);
            throw new WorkDBException("getFriends user: problems with DB adding friends, ids - " + id + "," + idFriend);
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer idFriend) {
        try {
            jdbcTemplate.update("DELETE FROM friends WHERE user_id_from = ? AND user_id_to = ?", id, idFriend);

            log.info("UserDbStorage/deleteFriend: friend was deleted, ids - {}, {}",id, idFriend);

        } catch (DataAccessException exc) {
            log.info("UserDbStorage/deleteFriend: DB problem of deleting friends, ids - {}, {}", id, idFriend);
            throw new WorkDBException(
                "deleteFriend user: problems with DB deleting friends, ids - " + id + "," + idFriend);
        }
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                ("SELECT f.user_id_to, u.email, u.login, u.name, u.birthday, friends.user_id_to\n" +
                    "FROM friends AS f\n" +
                    "LEFT JOIN users AS u ON f.user_id_to = u.id\n" +
                    "LEFT JOIN friends ON u.id = friends.user_id_from\n" +
                    "WHERE f.user_id_from = ? AND f.user_id_to IN (SELECT user_id_to\n" +
                    "                                              FROM friends \n" +
                    "                                              WHERE user_id_from = ?\n" +
                    "                                              ) \n" +
                    "GROUP BY f.user_id_to, u.email, u.login, u.name, u.birthday, friends.user_id_to"), id, otherId);

            List<User> users = creatUsersFromRows(rs, "user_id_to");

            log.info("UserDbStorage/getMutualFriends: getting mutual friends, ids - {}, {}",id, otherId);

            return users;
        } catch (DataAccessException exc) {
            log.info("UserDbStorage/getMutualFriends: DB problem of getting mutual friends, ids - {}, {}",id, otherId);
            throw new WorkDBException(
                "getMutualFriends user: problems with DB getting mutual friends, ids - " + id + "," + otherId);
        }
    }

    private List<User> creatUsersFromRows(SqlRowSet rs, String id) {
        Map<Integer, User> users = new HashMap<>();
        Set<Integer> friends;

        rs.next();

        for (int i = 0; i < rs.getRow(); i++) {
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
