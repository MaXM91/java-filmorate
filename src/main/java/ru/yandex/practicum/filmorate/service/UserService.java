package ru.yandex.practicum.filmorate.service;

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
            throw new ValidationException(" user should not have an id!");
        }

        checkName(user);

        return userStorage.create(user);
    }

    public User update(User user) {
        foundObject(user.getId());
        checkName(user);

        log.info("UserService/update: user with id - {} updated!", user.getId());

    return userStorage.update(user);
    }

    public User delete(Integer id) {
        foundObject(id);

        log.info("UserService/delete: user with id - {} deleted!", id);

    return userStorage.delete(id);
    }

    public User found(Integer id) {
        foundObject(id);

        log.info("UserService/found: user with id - {} founded!", id);

    return userStorage.found(id);
    }

    public List<User> getUsers() {
        return userStorage.get();
    }

    public List<User> getFriends(Integer id) {
        foundObject(id);

    return userStorage.getFriends(id);
    }

    //Work with friend list of users
///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addFriend(Integer id, Integer idFriend) {
        foundObject(id);
        foundObject(idFriend);

        userStorage.addFriend(id, idFriend);

        log.info("UserService/addFriend: user with id - {} add friend id - {}", id, idFriend);

    return true;
    }

    public boolean deleteFriend(Integer id, Integer idFriend) {
        foundObject(id);
        foundObject(idFriend);

        userStorage.deleteFriend(id, idFriend);

        log.info("UserService/deleteFriend: user with id - {} delete friend id - {}", id, idFriend);

    return true;
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        foundObject(id);
        foundObject(otherId);

    return userStorage.getMutualFriends(id, otherId);
    }

    private void foundObject(Integer id) {
        if (userStorage.found(id) == null) {
            throw new ObjectNotFoundException(" user id - " + id + " not found!");
        }
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
