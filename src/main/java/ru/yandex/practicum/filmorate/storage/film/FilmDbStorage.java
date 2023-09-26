package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.WorkDBException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO films (name," + " description, releaseDate, duration) values (?, ?, ?, ?)",
                    new String[]{"id"});
                preparedStatement.setString(1, film.getName());
                preparedStatement.setString(2, film.getDescription());
                preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
                preparedStatement.setInt(4, (int) film.getDuration());
                return preparedStatement;
            }, keyHolder);

            film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

            if (film.getGenres() != null) {
                film.setGenres(changeGenre(film));
            }

            if (film.getMpa() != null) {
                film.setMpa(changeMpa(film));
            }

            log.info("FilmService/create: film with id - {} was created", film.getId());

            return film;
        } catch (DataAccessException exp) {
            log.info("FilmDbStorage/create: DB problem of creating a film");
            throw new WorkDBException("create film: problems with DB on creating a film");
        }
    }

    @Override
    public Film update(Film film) {
        try {
            jdbcTemplate.update(
                "UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ? WHERE id =" + " ?",
                film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(),
                film.getId());

            if (film.getGenres() != null) {
                jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
                film.setGenres(changeGenre(film));
            } else {
                jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
            }

            if (film.getMpa() != null) {
                jdbcTemplate.update("DELETE FROM film_mpa WHERE film_id = ?", film.getId());
                film.setMpa(changeMpa(film));
            } else {
                jdbcTemplate.update("DELETE FROM film_mpa WHERE film_id = ?", film.getId());
            }

            return film;
        } catch (DataAccessException exp) {
            log.info("FilmDbStorage/update: DB problem of updating a film with id - {}", film.getId());
            throw new WorkDBException("update film: problems with DB on updating a film");
        }
    }

    @Override
    public Film delete(Integer id) {
        try {
            Film film = found(id);

            jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);

            log.info("FilmDbStorage/delete: film with id - {} was deleted", id);

            return film;
        } catch (DataAccessException exp) {
            log.info("FilmDbStorage/delete: DB problem of deleting a film with id - {}", id);
            throw new WorkDBException("delete film: problems with DB on deleting a film id - " + id);
        }
    }

    @Override
    public Film found(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT f.id, f.name, f.description, f.releasedate, f.duration, COUNT(DISTINCT l.user_id) AS likes\n" +
                    "FROM films AS f\n" + "LEFT JOIN likes AS l ON f.ID = l.FILM_ID\n" + "WHERE f.id = ?" +
                    "GROUP BY f.id",
                (rs, rowNum) -> new Film(rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                    FilmDbStorage.this.readGenre(rs.getInt("id")), FilmDbStorage.this.readMpa(rs.getInt("id")),
                    rs.getInt("likes"), rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration")), id);
        } catch (EmptyResultDataAccessException exc) {
            log.info("FilmDbStorage/found: DB problem of founding a film with id - {}", id);
            throw new ObjectNotFoundException("FilmDbStorage/found: film id - " + id + " not found!");
        } catch (DataAccessException exp) {
            log.info("FilmDbStorage/found: DB problem of founding a film with id - {}", id);
            throw new WorkDBException("found film: problems with DB on founding a film id - " + id);
        }
    }

    @Override
    public List<Film> get() {
        try {
            Map<Integer, Film> films = new HashMap<>();

            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT f.id, f.name, f.description, f.releasedate, f.duration, fg.genre_id, " +
                    "g.genre_name, fm.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS likes\n" + "FROM films AS f\n" +
                    "LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                    "LEFT JOIN film_mpa AS fm ON f.id = fm.film_id\n" + "LEFT JOIN mpa AS m ON fm.mpa_id = m.mpa_id\n" +
                    "LEFT JOIN likes AS l ON f.id = l.film_id\n" + "GROUP BY f.id , fg.genre_id, fm.mpa_id");

            rs.next();

            for (int i = 0; i < rs.getRow(); i++) {
                List<Genre> genres;
                Genre genre;
                Mpa mpa;

                if (films.containsKey(rs.getInt("id"))) {
                    if (rs.getString("genre_name") == null || rs.getInt("genre_id") == 0) {
                        continue;
                    }
                    genres = films.get(rs.getInt("id")).getGenres();
                    genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
                    genres.add(genre);
                    films.get(rs.getInt("id")).setGenres(genres);
                    rs.next();
                } else {

                    genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
                    genres = new ArrayList<>();
                    genres.add(genre);
                    mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));

                    if (rs.getString("genre_name") == null || rs.getInt("genre_id") == 0) {
                        genres = new ArrayList<>();
                    }

                    if (rs.getString("mpa_name") == null || rs.getInt("mpa_id") == 0) {
                        mpa = null;
                    }

                    Film film = new Film(rs.getInt("id"), rs.getString("name"), rs.getString("description"), genres,
                        mpa, rs.getInt("likes"), Objects.requireNonNull(rs.getDate("releaseDate")).toLocalDate(),
                        rs.getInt("duration"));
                    films.put(rs.getInt("id"), film);
                    rs.next();
                }
            }

            return new ArrayList<>(films.values());
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/get: DB problem of getting a films");
            throw new WorkDBException("get films: problems with DB on getting a films");
        }
    }

    @Override
    public List<Film> popularFilms(long count) {
        try {

            return jdbcTemplate.query(
                "SELECT f.id, f.name, f.description, f.releasedate, f.duration, COUNT(l.user_id) " + "AS rate\n" +
                    "FROM films AS f\n" + "LEFT JOIN likes AS l ON f.ID = l.film_id\n" + "GROUP BY f.id\n" +
                    "ORDER BY rate DESC\n" + "LIMIT ? ",
                (rs, rowNum) -> new Film(rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                    readGenre(rs.getInt("id")), readMpa(rs.getInt("id")), rs.getInt("rate"),
                    rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration")), (int) count);

        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/popularFilms: DB problem of getting a pop films");
            throw new WorkDBException("get popularFilms: problems with DB on getting a pop films");
        }
    }

    private List<Genre> changeGenre(Film film) {
        try {
            List<Genre> uniqueGenres = film.getGenres().stream().distinct().collect(Collectors.toList());

            jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, uniqueGenres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return uniqueGenres.size();
                    }
                });

            return readGenre(film.getId());
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/changeGenre: DB problem with changeGenre");
            throw new WorkDBException("changeGenre: problems with changeGenre");
        }
    }

    private List<Genre> readGenre(Integer id) {
        try {
            return jdbcTemplate.query(
                "SELECT fg.genre_id, g.genre_name FROM film_genre AS fg LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                    "WHERE fg.film_id = ?", (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")),
                id);
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/readGenre: DB problem with readGenre, id - {}", id);
            throw new WorkDBException("readGenre: problems with readGenre, id - " + id);
        }
    }

    private Mpa changeMpa(Film film) {
        try {
            jdbcTemplate.update("INSERT INTO film_mpa (film_id, mpa_id) VALUES(?, ?)", film.getId(), film.getMpa().getId());

            return readMpa(film.getId());
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/changeMpa: DB problem with changeMpa, id - {}", film.getId());
            throw new WorkDBException("changeMpa: problems with changeMpa, id - " + film.getId());
        }
    }

    private Mpa readMpa(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM film_mpa AS fm LEFT JOIN mpa AS m ON fm.mpa_id = m.mpa_id " + "WHERE fm.film_id = ?", (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/readMpa: DB problem with readMpa, id - {}", id);
            throw new WorkDBException("readMpa: problems with readMpa, id - " + id);
        }
    }
}