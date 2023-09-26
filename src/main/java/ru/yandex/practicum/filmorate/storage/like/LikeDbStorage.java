package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.WorkDBException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void add(Integer filmId, Integer userId) {
        try {
            jdbcTemplate.update("INSERT INTO likes(film_id, user_id) VALUES(?, ?)", filmId, userId);
        } catch (EmptyResultDataAccessException exc) {
            log.info("LikeDbStorage/add: problem of adding a genre, ids- {}, {}", filmId, userId);
            throw new ObjectNotFoundException("add like: problems with adding a like, ids - " + filmId + ", " + userId);
        } catch (DataAccessException exp) {
            log.info("LikeDbStorage/add: DB problem of adding a like, ids - {}, {}", filmId, userId);
            throw new WorkDBException("add like: problems with DB on adding a like, ids - " + filmId + ", " + userId);
        }
    }

    public void remove(Integer filmId, Integer userId) {
        try {
            jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        } catch (EmptyResultDataAccessException exc) {
            log.info("LikeDbStorage/remove: problem of removing a genre, ids- {}, {}", filmId, userId);
            throw new ObjectNotFoundException(
                "remove like: problems with adding a like, ids - " + filmId + ", " + userId);
        } catch (DataAccessException exp) {
            log.info("LikeDbStorage/remove: DB problem of removing a like, ids - {}, {}", filmId, userId);
            throw new WorkDBException(
                "remove like: problems with DB on removing a like, ids - " + filmId + ", " + userId);
        }
    }
}
