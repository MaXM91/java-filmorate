package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            "INSERT INTO films (name, description, releaseDate, duration)values (?, ?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, (int) film.getDuration());
            return preparedStatement;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        if (film.getGenres() != null) {
            film.setGenres(changeFilmGenre("INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)", film));
        }

        if (film.getMpa() != null) {
            film.setMpa(changeFilmMpa("INSERT INTO film_mpa (film_id, mpa_id) VALUES(?, ?)", film));
        }

        return film;
    }

    @Override
    public Film update(Film film) {

        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ? WHERE id = ?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getId()
        );

        //   try {
        if (film.getGenres() != null) {
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
            film.setGenres(changeFilmGenre("INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)", film));
        } else {
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        }

        if (film.getMpa() != null) {
            jdbcTemplate.update("DELETE FROM film_mpa WHERE film_id = ?", film.getId());
            film.setMpa(changeFilmMpa("INSERT INTO film_mpa (film_id, mpa_id) VALUES(?, ?)", film));
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
            return jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?", new RowMapper<Film>() {
                @Override
                public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Film(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            readFilmGenre(rs.getInt("id")),
                            readFilmMpa(rs.getInt("id")),
                            readUsersLike(id),
                            rs.getDate("releaseDate").toLocalDate(),
                            rs.getInt("duration"));
                }
            }, id);
        } catch (RuntimeException exc) {
            throw new ObjectNotFoundException("FilmDbStorage/found: film not found!");
        }
    }

    @Override
    public List<Film> get() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Film(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        readFilmGenre(rs.getInt("id")),
                        readFilmMpa(rs.getInt("id")),
                        readUsersLike(rs.getInt("id")),
                        rs.getDate("releaseDate").toLocalDate(),
                        rs.getInt("duration")
                );
            }
        });

        return films;
    }

    @Override
    public List<Film> popularFilms(long count) {
        try {
            List<Film> descFilms = jdbcTemplate.query(
                    "SELECT f.id, f.name, f.description, f.releasedate, f.duration, COUNT(l.user_id) AS rate\n" +
                            "FROM films AS f\n" +
                            "LEFT JOIN likes AS l ON f.ID = l.film_id\n" +
                            "GROUP BY f.id\n" +
                            "ORDER BY rate DESC\n" +
                            "LIMIT ? ", new RowMapper<Film>() {
                        @Override
                        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new Film(rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    readFilmGenre(rs.getInt("id")),
                                    readFilmMpa(rs.getInt("id")),
                                    rs.getInt("rate"),
                                    rs.getDate("releaseDate").toLocalDate(),
                                    rs.getInt("duration"));
                        }
                    }, (int) count);

            return descFilms;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<Genre> changeFilmGenre(String query, Film film) {
        List<Genre> newList = new ArrayList<>();
        List<Integer> changeRepeat = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            if (changeRepeat.contains(genre.getId())) {    // Обойти ошибку повторов
                continue;                                  //
            } else {                                       //
                changeRepeat.add(genre.getId());           //
            }                                              //
            jdbcTemplate.update(query, film.getId(), genre.getId()); // Ввести пару

            Genre fullGenre = jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id = ?", new RowMapper<Genre>() {
                @Override
                public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Genre(rs.getInt("genre_id"),
                            rs.getString("name"));
                }
            }, genre.getId());

            newList.add(fullGenre);
        }
        changeRepeat.clear();
        return newList;
    }

    private List<Genre> readFilmGenre(Integer id) {
        try {
            List<Genre> genres = jdbcTemplate.query("SELECT fg.genre_id, g.name \n" +
                    "FROM films AS f\n" +
                    "LEFT JOIN film_genre AS fg ON f.id=fg.film_id \n" +
                    "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id\n" +
                    "WHERE f.id = ?\n" +
                    "GROUP BY g.genre_id", new RowMapper<Genre>() {
                @Override
                public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                    if (rs.getInt("genre_id") == 0) {
                        return null;
                    } else {
                        return new Genre(rs.getInt("genre_id"),
                                rs.getString("name"));
                    }
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

    private Mpa changeFilmMpa(String query, Film film) {
        jdbcTemplate.update(query, film.getId(), film.getMpa().getId());

        Mpa fullMpa = jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE mpa_id = ?", new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Mpa(rs.getInt("Mpa_id"), rs.getString("name"));
            }
        }, film.getMpa().getId());
        return fullMpa;
    }

    private Mpa readFilmMpa(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT fm.mpa_id, m.name \n" +
                    "FROM films AS f\n" +
                    "LEFT JOIN film_mpa AS fm ON f.id=fm.film_id \n" +
                    "LEFT JOIN mpa AS m ON fm.mpa_id=m.mpa_id\n" +
                    "WHERE f.id = ?\n" +
                    "GROUP BY fm.mpa_id", new RowMapper<Mpa>() {
                @Override
                public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Mpa(rs.getInt("mpa_id"),
                            rs.getString("name"));
                }
            }, id);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Integer readUsersLike(Integer filmId) {
        try {
            Integer rate = jdbcTemplate.queryForObject("SELECT COUNT(user_id) AS c FROM likes WHERE film_id = ?", new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return (rs.getInt("c"));
                }
            }, filmId);
            return rate;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
