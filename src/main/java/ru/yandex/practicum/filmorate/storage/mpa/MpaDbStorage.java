package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.validators.exceptions.WorkDBException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa found(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE mpa_id = ?",
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);
        } catch (EmptyResultDataAccessException exc) {
            log.info("MpaDbStorage/found: DB problem of founding a mpa with id - {}", id);
            throw new ObjectNotFoundException("found mpa: problems with founding a mpa id - " + id);
        } catch (DataAccessException exp) {
            log.info("MpaDbStorage/found: DB problem of founding a mpa with id - {}", id);
            throw new WorkDBException("found mpa: problems with DB on founding a mpa id - " + id);
        }
    }

    public List<Mpa> get() {
        try {
            return jdbcTemplate.query("SELECT * FROM mpa GROUP BY mpa_id",
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        } catch (EmptyResultDataAccessException exc) {
            log.info("MpaDbStorage/get: problem with getting a mpa");
            throw new ObjectNotFoundException("get mpa: problems with getting mpa");
        } catch (DataAccessException exp) {
            log.info("MpaDbStorage/found: DB problem of getting a mpa");
            throw new WorkDBException("get mpa: problems with DB getting a mpa");
        }
    }
}
