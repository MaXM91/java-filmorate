package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.like.LikeService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.ValidationException;

import java.util.List;

@Slf4j
@Service
public class FilmService {
// Work with films
///////////////////////////////////////////////////////////////////////////////////////////////////

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeService likeService;

    @Autowired
    public FilmService(
        @Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService, LikeService likeService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likeService = likeService;
    }

    public Film create(Film film) {
        if (film.getId() >= 1) {
            log.info("FilmService/create: bad try create film with id - {}", film.getId());
            throw new ValidationException("create film: film should not have an id!");
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() <= 0) {
            log.info("FilmService/update: bad try update film with id - {}", film.getId());
            throw new ValidationException("update film: film have bad id - " + film.getId() + "!");
        }

        if (filmStorage.found(film.getId()) == null) {
            log.info("FilmService/update: film id - {} not found", film.getId());
            throw new ObjectNotFoundException("update film: film id - " + film.getId() + " not found!");
        }
        return filmStorage.update(film);
    }

    public Film delete(Integer id) {
        if (id == null || id <= 0) {
            log.info("FilmService/delete: film id - {} (null or <= 0)", id);
            throw new ValidationException("delete film: film id - " + id + " null or <= 0");
        }

        if (filmStorage.found(id) == null) {
            log.info("FilmService/delete: film id - {} not found", id);
            throw new ObjectNotFoundException("delete film: film id - " + id + " not found!");
        }
        return filmStorage.delete(id);
    }

    public Film getFilm(Integer id) {

        if (filmStorage.found(id) == null) {
            log.info("FilmService/getFilm: film with id - {} not found!", id);
            throw new ObjectNotFoundException("getFilm film: film id - " + id + " not found!");
        }

        log.info("FilmService/getFilm: film with id - {} was found!", id);

        return filmStorage.found(id);
    }

    public List<Film> getFilms() {
        return filmStorage.get();
    }

// Work with likes
///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addLike(Integer filmId, Integer userId) {
        if (filmStorage.found(filmId) == null) {
            log.info("FilmService/addLike: film id - {} not found!", filmId);
            throw new ObjectNotFoundException("addLike film: film id - " + filmId + " not found!");
        }

        if (userService.found(userId) == null) {
            log.info("FilmService/addLike: user id - {} not found!", userId);
            throw new ObjectNotFoundException("addLike film: user id - " + userId + " not found!");
        }

        likeService.addLike(filmId, userId);
        return true;
    }

    public boolean removeLike(Integer filmId, Integer userId) {
        if (filmStorage.found(filmId) == null) {
            log.info("FilmService/removeLike: film id - {} not found!", filmId);
            throw new ObjectNotFoundException("removeLike film: film id - " + filmId + " not found!");
        }

        if (userService.found(userId) == null) {
            log.info("FilmService/removeLike: user id - {} not found!", userId);
            throw new ObjectNotFoundException("removeLike film: user id - " + userId + " not found!");
        }

        likeService.removeLike(filmId, userId);
        return true;
    }

    public List<Film> popularFilms(long count) {
        return filmStorage.popularFilms(count);
    }
}
