package ru.yandex.practicum.filmorate.service.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeDbStorage likeDbStorage;
    private final FilmService filmService;
    private final UserService userService;

    public boolean addLike(Integer filmId, Integer userId) {
        checkIds(filmId, userId, "addLike");

        likeDbStorage.add(filmId, userId);
        return true;
    }

    public boolean removeLike(Integer filmId, Integer userId) {
        checkIds(filmId, userId, "removeLike");

        likeDbStorage.remove(filmId, userId);
        return true;
    }

    private void checkIds(Integer filmId, Integer userId, String type) {
        if (filmId <= 0) {
            log.info("LikeService/" + type + ": film id - {} not found!", filmId);
            throw new ObjectNotFoundException(type + " like: film id - " + filmId + " not found!");
        }

        if (userId <= 0) {
            log.info("LikeService/" + type + ": film id - {} not found!", filmId);
            throw new ObjectNotFoundException(type + " like: film id - " + filmId + " not found!");
        }

        if (filmService.getFilm(filmId) == null) {
            log.info("LikeService/" + type + ": film id - {} not found!", filmId);
            throw new ObjectNotFoundException(type + " like: film id - " + filmId + " not found!");
        }

        if (userService.found(userId) == null) {
            log.info("LikeService/" + type + ": user id - {} not found!", userId);
            throw new ObjectNotFoundException(type + " like: user id - " + userId + " not found!");
        }
    }

}
