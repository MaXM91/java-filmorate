package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
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

    @Autowired
    public FilmService(
        @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) {
        if (film.getId() >= 1) {
            log.info("FilmService/create: bad try create film with id - {}", film.getId());
            throw new ValidationException("create film: film should not have an id!");
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        checkIds(film.getId(), "update");

        return filmStorage.update(film);
    }

    public Film delete(Integer id) {
        checkIds(id, "delete");

        return filmStorage.delete(id);
    }

    public Film getFilm(Integer id) {
        checkIds(id, "getFilm");

        log.info("FilmService/getFilm: film with id - {} was found!", id);

        return filmStorage.found(id);
    }

    public List<Film> getFilms() {
        return filmStorage.get();
    }

    public List<Film> popularFilms(long count) {
        return filmStorage.popularFilms(count);
    }

    private void checkIds(Integer id, String methodName) {
        if (id <= 0) {
            log.info("FilmService/" + methodName + ": bad id - {}", id);
            throw new ValidationException(methodName + " film: bad id - " + id + "!");
        }

        if (filmStorage.found(id) == null) {
            log.info("FilmService/" + methodName + ": id - {} not found", id);
            throw new ObjectNotFoundException(methodName + " film: film id - " + id + " not found!");
        }
    }
}
