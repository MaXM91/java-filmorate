package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
                    "INSERT INTO films (name, description, releaseDate, duration) values (?, ?, ?, ?)",
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
                changeMpa(film);
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
                "UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ? WHERE id = ?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getId());

            if (film.getGenres() != null) {
                jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
                film.setGenres(changeGenre(film));
            } else {
                jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
            }

            if (film.getMpa() != null) {
                changeMpa(film);
            } else {
                jdbcTemplate.update("UPDATE films SET mpa_id = null WHERE id = ?", film.getId());
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

            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, fg.genre_id, g.genre_name," +
                    "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS likes\n" +
                    "FROM films AS f\n" +
                    "LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id\n" +
                    "LEFT JOIN likes AS l ON f.id = l.film_id\n" +
                    "WHERE f.id = ?\n" +
                    "GROUP BY f.id , fg.genre_id, f.mpa_id", id);

            List<Film> response = createFilmsFromRows(rs);

            if (response.size() == 0) {
                return null;
            }

            return response.get(0);
        } catch (ArrayIndexOutOfBoundsException exp) {
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
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT f.id, f.name, f.description, f.releasedate, f.duration, fg.genre_id, " +
                    "g.genre_name, f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS likes\n" +
                    "FROM films AS f\n" +
                    "LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id\n" +
                    "LEFT JOIN likes AS l ON f.id = l.film_id\n" +
                    "GROUP BY f.id , fg.genre_id, f.mpa_id");

            return createFilmsFromRows(rs);
        } catch (ArrayIndexOutOfBoundsException exp) {
            return new ArrayList<>();
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/get: DB problem of getting a films");
            throw new WorkDBException("get films: problems with DB on getting a films");
        }
    }

    @Override
    public List<Film> popularFilms(long count) {
        try {
            List<Film> response;
            int num = (int) count * 2;

            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(l.user_id) AS likes, f.id, f.name, f.description, f.mpa_id, m.mpa_name, fg.genre_id,\n" +
                    "g.genre_name, f.releaseDate, f.duration\n" +
                    "FROM films AS f\n" +
                    "LEFT JOIN likes AS l ON l.film_id = f.id\n" +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id\n" +
                    "LEFT JOIN film_genre AS fg ON fg.film_id = f.id\n" +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                    "GROUP BY f.id, fg.genre_id\n" +
                    "ORDER BY likes DESC\n" +
                    "LIMIT ?", num);

            response = createFilmsFromRows(rs);

            if (response.size() == 0) {
                return new ArrayList<>();
            } else if (response.size() <= count || (response.size() == 1 && count == 1)) {
                return response;
            } else {
                return new ArrayList<>(response.subList(0, (int) count));
            }
        } catch (ArrayIndexOutOfBoundsException exp) {
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
            return uniqueGenres;
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/changeGenre: DB problem with changeGenre");
            throw new WorkDBException("changeGenre: problems with changeGenre");
        }
    }

    private void changeMpa(Film film) {
        try {
            jdbcTemplate.update("UPDATE films SET mpa_id = ? WHERE id = ?", film.getMpa().getId(), film.getId());
        } catch (DataAccessException exc) {
            log.info("FilmDbStorage/changeMpa: DB problem with changeMpa, id - {}", film.getId());
            throw new WorkDBException("changeMpa: problems with changeMpa, id - " + film.getId());
        }
    }

    private List<Film> createFilmsFromRows(SqlRowSet rs) {
        Map<Integer, Film> films = new HashMap<>();
        List<Integer> idsList = new ArrayList<>();
        List<Film> filmsList = new ArrayList<>();

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

                Film film = new Film(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    genres,
                    mpa,
                    rs.getInt("likes"),
                    Objects.requireNonNull(rs.getDate("releaseDate")).toLocalDate(),
                    rs.getInt("duration"));
                films.put(rs.getInt("id"), film);
                idsList.add(rs.getInt("id"));
            }
            rs.next();
        }

        for (Integer id : idsList) {
            filmsList.add(films.get(id));
        }

        return filmsList;
    }
}