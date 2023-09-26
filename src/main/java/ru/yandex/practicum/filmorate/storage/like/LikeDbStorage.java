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
       changeLikes("INSERT INTO likes(film_id, user_id) VALUES(?, ?)", filmId, userId,"add");
    }

    public void remove(Integer filmId, Integer userId) {
        changeLikes("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId,"delete");
    }

    private void changeLikes(String sql, Integer filmId, Integer userId, String type) {
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (EmptyResultDataAccessException exc) {
            log.info("LikeDbStorage/" + type + ": ids- {}, {}", filmId, userId);
            throw new ObjectNotFoundException(type + " like: ids - " + filmId + ", " + userId);
        } catch (DataAccessException exp) {
            log.info("LikeDbStorage/" + type + ": DB problem, ids - {}, {}", filmId, userId);
            throw new WorkDBException(type + " like:DB problem, ids - " + filmId + ", " + userId);
        }
    }
}
