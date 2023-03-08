package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.exceptions.IdNotNullException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int filmId = 1;
    private static final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) throws IdNotNullException {
        if (film.getId() != 0) {
            log.debug("Фильм при регистрации должен быть равен 0, а не {}", film.getId());
            throw new IdNotNullException("Регистрация фильма с аномальным значением поля id");
        }

        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;

    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Фильм с id = {} не зарегистрирован", film.getId());
            throw new ValidationException("Film not found");
        }

        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
