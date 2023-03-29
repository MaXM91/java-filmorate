package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
// Work with films
///////////////////////////////////////////////////////////////////////////////////////////////////
    FilmStorage filmStorage;
    UserService userService;

    @Autowired
    FilmService(InMemoryFilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) throws ValidationException {
        if (film.getId() >= 1) {
            log.info("Неудачная попытка создания фильма с ид {}", film.getId());
            throw new ValidationException("create: film have id!");
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) throws ObjectNotFoundException {
        if (filmStorage.found(film.getId()) == null) {
            log.info("При обновлении не найден фильм с ид {}", film.getId());
            throw new ObjectNotFoundException("update: film not found!");
        }
        return filmStorage.update(film);
    }

    public Film delete(Integer id) throws ObjectNotFoundException {

        if (filmStorage.found(id) == null) {
            log.info("При удалении не найден фильм с ид {}", id);
            throw new ObjectNotFoundException("FilmService/delete: film not found!");
        }
        return filmStorage.delete(id);
    }

    public Film getFilm(Integer id) {

        if (filmStorage.found(id) == null) {
            log.info("При поиске не найден фильм с ид {}", id);
            throw new ObjectNotFoundException("FilmService/delete: film not found!");
        }

        return filmStorage.found(id);
    }

    public List<Film> get() {
        return filmStorage.get();
    }

// Work with likes
///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addLike(Integer filmId, Integer userId) throws ObjectNotFoundException {
        if (filmStorage.found(filmId) == null) {
            log.info("При добавлении лайка не найден фильм с ид {}", filmId);
            throw new ObjectNotFoundException("film/addLike: film not found!");
        }

        if (userService.found(userId) == null) {
            log.info("При добавлении лайка не найден юзер с ид {}", userId);
            throw new ObjectNotFoundException("film/addLike: user not found!");
        }

        filmStorage.found(filmId).setUsersLike(userId);
        return true;
    }

    public boolean removeLike(Integer filmId, Integer userId) throws ObjectNotFoundException {
        if (filmStorage.found(filmId) == null) {
            log.info("При удалении лайка не найден фильм с ид {}", filmId);
            throw new ObjectNotFoundException("film/removeLike: film not found!");
        }

        if (userService.found(userId) == null) {
            log.info("При удалении лайка не найден юзер с ид {}", userId);
            throw new ObjectNotFoundException("film/removeLike: user not found!");
        }

        filmStorage.found(filmId).deleteUsersLike(userId);
        return true;
    }

    public List<Film> popularFilms(long count) {
        if (count <= 0) {
            log.info("При выводе популярных фильмов не верное значение count {}", count);
            throw new ValidationException("FilmService/popularFilms: bad count");
        }

        return filmStorage.get().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getUsersLike().size(), s1.getUsersLike().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
