package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
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
            throw new ValidationException(" film should not have an id!");
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        checkIds(film.getId());

        return filmStorage.update(film);
    }

    public Film delete(Integer id) {
        checkIds(id);

        return filmStorage.delete(id);
    }

    public Film getFilm(Integer id) {
        checkIds(id);

        return filmStorage.found(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public List<Film> popularFilms(long count) {
        return filmStorage.popularFilms(count);
    }

    private void checkIds(Integer id) {
        if (id <= 0) {
            throw new ValidationException(" film bad id - " + id);
        }

        filmStorage.found(id);
    }
}
