package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.exceptions.IdNotNullException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int userId = 1;
    private static final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody @Valid User user) throws ValidationException, IdNotNullException {
        if (new ArrayList<>(users.values()).stream().anyMatch(u -> u.getLogin().equals(user.getLogin())) && !users.isEmpty()) {
            log.debug("Пользователь с таким логином {} зарегистрирован", user.getLogin());
            throw new ValidationException("Пользователь с таким логином зарегистрирован");
        } else if (user.getId() != 0) {
            log.debug("Фильм при регистрации должен быть равен 0, а не {}", user.getId());
            throw new IdNotNullException("Регистрация пользователя с аномальным значением поля id");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(userId++);
        users.put(user.getId(), user);
        return user;

    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User not found");
        }

        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
