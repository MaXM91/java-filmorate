package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
// Work with films
///////////////////////////////////////////////////////////////////////////////////////////////////
    private int filmId = 1;
    private static final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Integer id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }

    @Override
    public Film found(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        return null;
    }

    @Override
    public List<Film> get() {
        return new ArrayList<>(films.values());
    }
}
