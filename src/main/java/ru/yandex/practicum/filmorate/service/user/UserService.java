package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.ValidationException;

import java.util.List;

@Slf4j
@Service
public class UserService {
//Work with users
///////////////////////////////////////////////////////////////////////////////////////////////////
    UserStorage userStorage;

    UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        if (user.getId() >= 1) {
            log.info("UserService/create: bad try create user with id - {}", user.getId());
            throw new ValidationException("create user: user should not have an id!");
        }

        checkName(user);

        return userStorage.create(user);
    }

    public User update(User user) {
        checkIds(user.getId(), "update");

        checkName(user);

    return userStorage.update(user);
    }

    public User delete(Integer id) {
        checkIds(id, "delete");

    return userStorage.delete(id);
    }

    public User found(Integer id) {
        checkIds(id, "found");

        log.info("UserService/found: user with id - {} was found!", id);
    return userStorage.found(id);
    }

    public List<User> getUsers() {
        return userStorage.get();
    }

    public List<User> getFriends(Integer id) {
        checkIds(id, "getFriends");

    return userStorage.getFriends(id);
    }

    //Work with friend list of users
///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addFriend(Integer id, Integer idFriend) {
        if (userStorage.found(id) == null) {
            log.info("UserService/addFriend: user id - {} not found", id);
            throw new ObjectNotFoundException("addFriend user: user id - " + id + " not found!");
        }

        if (userStorage.found(idFriend) == null) {
            log.info("UserService/addFriend: user id - {} not found", idFriend);
            throw new ObjectNotFoundException("addFriend user: user id - " + idFriend + " not found!");
        }

        userStorage.addFriend(id, idFriend);

    return true;
    }

    public boolean deleteFriend(Integer id, Integer idFriend) {
        if (userStorage.found(id) == null) {
            log.info("UserService/deleteFriend: user id - {} not found", id);
            throw new ObjectNotFoundException("deleteFriend user: user id - " + id + " not found!");
        }

        if (userStorage.found(idFriend) == null) {
            log.info("UserService/deleteFriend: user id - {} not found", idFriend);
            throw new ObjectNotFoundException("deleteFriend user: user id - " + idFriend + " not found!");
        }

        userStorage.deleteFriend(id, idFriend);

    return true;
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        checkIds(id, "getMutualFriends");
        checkIds(otherId, "getMutualFriends");

    return userStorage.getMutualFriends(id, otherId);
    }

    private void checkIds(Integer id, String type) {
        if (id <= 0) {
            log.info("UserService/" + type + ": bad id - {}", id);
            throw new ValidationException(type + " user: bad id - " + id);
        }

        if (userStorage.found(id) == null) {
            log.info("UserService/" + type + ": user id - {} not found", id);
            throw new ObjectNotFoundException(type + " user: user id - " + id + " not found!");
        }
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
