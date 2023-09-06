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

        return userStorage.delete(id);
    }

    public User found(Integer id) throws ObjectNotFoundException {
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
    public boolean addFriend(Integer id, Integer idFriend) throws ObjectNotFoundException {

        if (userStorage.found(id) == null || userStorage.found(idFriend) == null) {
            log.info("Не найден один из юзеров при добавлении в друзья {} {}", id, idFriend);
            throw new ObjectNotFoundException("UserService/addFriend: user not found!");
        }

        userStorage.addFriend(id, idFriend);
        return true;
    }

    public boolean deleteFriend(Integer id, Integer idFriend) throws ObjectNotFoundException, ValidationException {
        if (id == null || id <= 0 || idFriend == null || idFriend <= 0) {
            log.info("Удаление из друзей с неформатными ид {} {}", id, idFriend);
            throw new ValidationException("UserService/deleteFriends: id user null or <= 0");
        }

        if (userStorage.found(id) == null || userStorage.found(idFriend) == null) {
            log.info("Не найден один из юзеров при удалении из друзей {} {}", id, idFriend);
            throw new ObjectNotFoundException("UserService/deleteFriend: user not found!");
        }

        userStorage.deleteFriend(id, idFriend);
        return true;
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) throws ObjectNotFoundException, ValidationException {

        if (id == null || id <= 0 || otherId == null || otherId <= 0) {
            log.info("Вывод общих друзей с неформатными ид {} {}", id, otherId);
            throw new ValidationException("UserService/getMutualFriends: id user null or <= 0");
        }

        if (userStorage.found(id) == null || userStorage.found(otherId) == null) {
            log.info("Не найден один из юзеров при выводе общих друзей {} {}", id, otherId);
            throw new ObjectNotFoundException("UserService/getMutualFriends: user not found!");
        }

       return userStorage.getMutualFriends(id, otherId);
    }
}
