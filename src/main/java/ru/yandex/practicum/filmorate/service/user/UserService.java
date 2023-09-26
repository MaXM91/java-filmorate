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

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

    return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getId() <= 0) {
            log.info("UserService/update: bad try update user with id - {}", user.getId());
            throw new ValidationException("update user: user have bad id - " + user.getId() + "!");
        }

        if (userStorage.found(user.getId()) == null) {
            log.info("UserService/update: user id - {} not found", user.getId());
            throw new ObjectNotFoundException("update user: user id - " + user.getId() + " not found!");
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

    return userStorage.update(user);
    }

    public User delete(Integer id) {
        if (id == null || id <= 0) {
            log.info("UserService/delete: user id - {} (null or <= 0)", id);
            throw new ValidationException("delete user: user id - " + id + " null or <= 0");
        }

        if (userStorage.found(id) == null) {
            log.info("UserService/update: user with id - {} not found!", id);
            throw new ObjectNotFoundException("update user: user id - " + id + " not found!");
        }

    return userStorage.delete(id);
    }

    public User found(Integer id) {
        User user = userStorage.found(id);
        if (user == null) {
            log.info("UserService/found: user with id - {} not found!", id);
            throw new ObjectNotFoundException("found user: user id - " + id + " not found!");
        }

        log.info("UserService/found: user with id - {} was found!", id);
    return user;
    }

    public List<User> getUsers() {
        return userStorage.get();
    }

    public List<User> getFriends(Integer id) {
        if (id == null || id <= 0) {
            log.info("UserService/getFriends: user id - {} (null or <= 0)", id);
            throw new ValidationException("getFriends user: user id - " + id + " null or <= 0");
        }

        if (userStorage.found(id) == null) {
            log.info("UserService/getFriends: user with id - {} not found!", id);
            throw new ObjectNotFoundException("getFriends user: user id - " + id + " not found!");
        }

    return userStorage.getFriends(id);
    }

    //Work with friend list of users
///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addFriend(Integer id, Integer idFriend) {

        if (userStorage.found(id) == null || userStorage.found(idFriend) == null) {
            log.info("UserService/addFriend: one or both from users {}, {} not found!", id, idFriend);
            throw new ObjectNotFoundException("addFriend user: one or both from users ids - " + id + ", " + idFriend +
                " not found!");
        }

        userStorage.addFriend(id, idFriend);

    return true;
    }

    public boolean deleteFriend(Integer id, Integer idFriend) {
        if (id == null || id <= 0 || idFriend == null || idFriend <= 0) {
            log.info("UserService/deleteFriends: bad id/ids of users - {}, {} (null or <= 0)", id, idFriend);
            throw new ValidationException("deleteFriends user: user id - " + id + ", " + idFriend + " null or <= 0");
        }

        if (userStorage.found(id) == null || userStorage.found(idFriend) == null) {
            log.info("UserService/deleteFriend: one or both from users {}, {} not found!", id, idFriend);
            throw new ObjectNotFoundException("deleteFriend user: one or both from users ids - " + id + ", " + idFriend +
                " not found!");
        }

        userStorage.deleteFriend(id, idFriend);

    return true;
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {

        if (id == null || id <= 0 || otherId == null || otherId <= 0) {
            log.info("UserService/getMutualFriends: bad id/ids of users - {}, {} (null or <= 0)", id, otherId);
            throw new ValidationException("getMutualFriends user: user id - " + id + ", " + otherId + " null or <= 0");
        }

        if (userStorage.found(id) == null || userStorage.found(otherId) == null) {
            log.info("UserService/getMutualFriends: one or both from users {}, {} not found!", id, otherId);
            throw new ObjectNotFoundException("getMutualFriends user: one or both from users ids - " + id + ", " + otherId +
                " not found!");
        }

    return userStorage.getMutualFriends(id, otherId);
    }
}
