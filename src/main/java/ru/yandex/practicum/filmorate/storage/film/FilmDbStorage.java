package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO films (name," +
                    " description, releaseDate, duration) values (?, ?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, (int) film.getDuration());
            return preparedStatement;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null) {
            film.setGenres(changeFilmGenre(film));
        }

        if (film.getMpa() != null) {
            film.setMpa(changeFilmMpa(film));
        }

        return film;
    }

    @Override
    public Film update(Film film) {

        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, releasedate = ?," +
                " duration = ? WHERE id = ?", film.getName(), film.getDescription(),
            Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getId());

        if (film.getGenres() != null) {
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
            film.setGenres(changeFilmGenre(film));
        } else {
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        }

        if (film.getMpa() != null) {
            jdbcTemplate.update("DELETE FROM film_mpa WHERE film_id = ?", film.getId());
            film.setMpa(changeFilmMpa(film));
        } else {
            jdbcTemplate.update("DELETE FROM film_mpa WHERE film_id = ?", film.getId());
        }

        return film;
    }

    @Override
    public Film delete(Integer id) {
        Film film = found(id);

        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);

        return film;
    }

    @Override
    public Film found(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?",
                (rs, rowNum) -> new Film(rs.getInt("id"), rs.getString("name"),
                    rs.getString("description"), readFilmGenre(rs.getInt("id")),
                    readFilmMpa(rs.getInt("id")), readUsersLike(id),
                    rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration")), id);
        } catch (RuntimeException exc) {
            throw new ObjectNotFoundException("FilmDbStorage/found: film not found!");
        }
    }

    @Override
    public List<Film> get() {

        return jdbcTemplate.query("SELECT * FROM films",
            (rs, rowNum) -> new Film(rs.getInt("id"), rs.getString("name"),
                rs.getString("description"), readFilmGenre(rs.getInt("id")),
                readFilmMpa(rs.getInt("id")), readUsersLike(rs.getInt("id")),
                rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration")));
    }

    @Override
    public List<Film> popularFilms(long count) {
        try {

            return jdbcTemplate.query(
                "SELECT f.id, f.name, f.description, f.releasedate, f.duration, COUNT(l.user_id) " +
                    "AS rate\n" + "FROM films AS f\n" +
                    "LEFT JOIN likes AS l ON f.ID = l.film_id\n" + "GROUP BY f.id\n" +
                    "ORDER BY rate DESC\n" + "LIMIT ? ",
                (rs, rowNum) -> new Film(rs.getInt("id"), rs.getString("name"),
                    rs.getString("description"), readFilmGenre(rs.getInt("id")),
                    readFilmMpa(rs.getInt("id")), rs.getInt("rate"),
                    rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration")), (int) count);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<Genre> changeFilmGenre(Film film) {
        List<Genre> newList = new ArrayList<>();
        List<Integer> changeRepeat = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            if (changeRepeat.contains(genre.getId())) {    // Обойти ошибку повторов
                continue;                                  //
            } else {                                       //
                changeRepeat.add(genre.getId());           //
            }                                              //
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)",
                film.getId(), genre.getId()); // Ввести пару

            Genre fullGenre = jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id = ?",
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")),
                genre.getId());

            newList.add(fullGenre);
        }
        changeRepeat.clear();
        return newList;
    }

    private List<Genre> readFilmGenre(Integer id) {
        try {
            List<Genre> genres = jdbcTemplate.query(
                "SELECT fg.genre_id, g.name \n" + "FROM films AS f\n" +
                    "LEFT JOIN film_genre AS fg ON f.id=fg.film_id \n" +
                    "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id\n" + "WHERE f.id = ?\n" +
                    "GROUP BY g.genre_id", (rs, rowNum) -> {
                    if (rs.getInt("genre_id") == 0) {
                        return null;
                    } else {
                        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
                    }
                }, id);

            if (genres.contains(null)) {
                genres.clear();
            }

            return genres;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Mpa changeFilmMpa(Film film) {
        jdbcTemplate.update("INSERT INTO film_mpa (film_id, mpa_id) VALUES(?, ?)", film.getId(),
            film.getMpa().getId());

        return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE mpa_id = ?",
            (rs, rowNum) -> new Mpa(rs.getInt("Mpa_id"), rs.getString("name")),
            film.getMpa().getId());
    }

    private Mpa readFilmMpa(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT fm.mpa_id, m.name \n" + "FROM films AS f\n" +
                    "LEFT JOIN film_mpa AS fm ON f.id=fm.film_id \n" +
                    "LEFT JOIN mpa AS m ON fm.mpa_id=m.mpa_id\n" + "WHERE f.id = ?\n" +
                    "GROUP BY fm.mpa_id, m.name",
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")), id);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Integer readUsersLike(Integer filmId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT COUNT(user_id) AS c FROM likes WHERE film_id = ?",
                (rs, rowNum) -> (rs.getInt("c")), filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}