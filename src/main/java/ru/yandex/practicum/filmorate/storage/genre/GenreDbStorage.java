package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.WorkDBException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Genre found(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id = ?",
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), id);
        } catch (EmptyResultDataAccessException exc) {
            log.info("GenreDbStorage/found: problem of founding a genre with id - {}", id);
            throw new ObjectNotFoundException("found genre: problems with founding a genre id - " + id);
        } catch (DataAccessException exp) {
            log.info("GenreDbStorage/found: DB problem of founding a genre with id - {}", id);
            throw new WorkDBException("found genre: problems with DB on founding a genre id - " + id);
        }
    }

    public List<Genre> get() {
        try {
            return jdbcTemplate.query("SELECT * FROM genre GROUP BY genre_id",
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
        } catch (EmptyResultDataAccessException exc) {
            log.info("GenreDbStorage/get: problem with getting a genre");
            throw new ObjectNotFoundException("found genre: problems with getting genre");
        } catch (DataAccessException exp) {
            log.info("GenreDbStorage/get: DB problem of getting a genre");
            throw new WorkDBException("found genre: problems with DB getting a genre");
        }
    }

}
