package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
//Work with users
///////////////////////////////////////////////////////////////////////////////////////////////////
    UserStorage userStorage;

    @Autowired
    UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) throws ValidationException {
        if (user.getId() >= 1) {
            log.info("Неудачная попытка создания юзера с ид {}", user.getId());
            throw new ValidationException("UserService/create: user have id!");
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User update(User user) throws ObjectNotFoundException, ValidationException {
        if (user.getId() <= 0) {
            log.info("Неудачная попытка обновления юзера с ид {}", user.getId());
            throw new ValidationException("UserService/update: user have bad id!");
        }

        if (userStorage.found(user.getId()) == null) {
            log.info("Не найден при обновлении юзер с ид {}", user.getId());
            throw new ObjectNotFoundException("UserService/update: user not found!");
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.update(user);
    }

    public User delete(Integer id) throws ObjectNotFoundException, ValidationException {
        if (id == null || id <= 0) {
            log.info("Неудачная попытка удаления юзера с ид {}", id);
            throw new ValidationException("UserService/delete: id user null or <= 0");
        }

        if (userStorage.found(id) == null) {
            log.info("Не найден при удалении юзер с ид {}", id);
            throw new ObjectNotFoundException("UserService/update: user not found!");
        }

        for (Integer idFriend : userStorage.found(id).getUserFriends()) {
            userStorage.found(idFriend).deleteUserFriends(id);
        }

        return userStorage.delete(id);
    }

    public User found(Integer id) throws ObjectNotFoundException, ValidationException {

        User user = userStorage.found(id);
        if (user == null) {
            log.info("Не найден при поиске юзер с ид {}", id);
            throw new ObjectNotFoundException("UserService/found: user not found!");
        }
        return user;
    }

    public List<User> getFriends(Integer id) throws ObjectNotFoundException, ValidationException {
        if (id == null || id <= 0) {
            log.info("Неудачная попытка извлечь друзей юзера с ид {}", id);
            throw new ValidationException("UserService/getFriends: id user null or <= 0");
        }

        if (userStorage.found(id) == null) {
            log.info("Не найден юзер при извлечении друзей с ид {}", id);
            throw new ObjectNotFoundException("UserService/getFriends: user not found!");
        }
        return userStorage.getFriends(id);
    }

    public List<User> get() {
        return userStorage.get();
    }

//Work with friend list of users
///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addFriend(Integer id1, Integer id2) throws ObjectNotFoundException {

        if (userStorage.found(id1) == null || userStorage.found(id2) == null) {
            log.info("Не найден один из юзеров при добавлении в друзья {} {}", id1, id2);
            throw new ObjectNotFoundException("UserService/addFriend: user not found!");
        }

        userStorage.addFriend(id1, id2);
        return true;
    }

    public boolean deleteFriend(Integer id1, Integer id2) throws ObjectNotFoundException, ValidationException {
        if (id1 == null || id1 <= 0 || id2 == null || id2 <= 0) {
            log.info("Удаление из друзей с неформатными ид {} {}", id1, id2);
            throw new ValidationException("UserService/deleteFriends: id user null or <= 0");
        }

        if (userStorage.found(id1) == null || userStorage.found(id2) == null) {
            log.info("Не найден один из юзеров при удалении из друзей {} {}", id1, id2);
            throw new ObjectNotFoundException("UserService/deleteFriend: user not found!");
        }

        userStorage.deleteFriend(id1, id2);
        return true;
    }

    public List<User> getMutualFriends(Integer id1, Integer id2) throws ObjectNotFoundException, ValidationException {
        List<User> mutualFriends = new ArrayList<>();

        if (id1 == null || id1 <= 0 || id2 == null || id2 <= 0) {
            log.info("Вывод общих друзей с неформатными ид {} {}", id1, id2);
            throw new ValidationException("UserService/getMutualFriends: id user null or <= 0");
        }

        if (userStorage.found(id1) == null || userStorage.found(id2) == null) {
            log.info("Не найден один из юзеров при выводе общих друзей {} {}", id1, id2);
            throw new ObjectNotFoundException("UserService/getMutualFriends: user not found!");
        }

        for (Integer id : userStorage.found(id1).getUserFriends()) {
            if (userStorage.found(id2).getUserFriends().contains(id)) {
                mutualFriends.add(userStorage.found(id));
            }
        }
        return mutualFriends;
    }

}
