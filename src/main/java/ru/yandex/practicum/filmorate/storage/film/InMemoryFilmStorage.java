package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final HashMap<Integer, Film> films = new HashMap<>();
// Work with films
///////////////////////////////////////////////////////////////////////////////////////////////////
    private int filmId = 1;

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

    @Override
    public List<Film> popularFilms(long count) {
        if (count <= 0) {
            log.info("При выводе популярных фильмов не верное значение count {}", count);
            throw new ValidationException("FilmService/popularFilms: bad count");
        }

        return get().stream()
            .sorted((s1, s2) -> Integer.compare(s2.getRate(), s1.getRate()))
            .limit(count)
            .collect(Collectors.toList());
    }

    public void addLike(Integer filmId, Integer userId) {
        films.get(filmId)
            .setRate(films.get(filmId)
                .getRate() + 1);
    }

    public void removeLike(Integer filmId, Integer userId) {
        films.get(filmId)
            .setRate(films.get(filmId)
                .getRate() - 1);
    }

}
