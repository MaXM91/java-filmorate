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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa found(Integer id) {
        return giveMpa("SELECT * FROM mpa WHERE mpa_id = ?", id, "found").get(0);
    }

    public List<Mpa> get() {
        return  giveMpa("SELECT * FROM mpa GROUP BY mpa_id", null, "get");
    }

    private List<Mpa> giveMpa(String sql, Integer id, String type) {
        try {
            if (id == null) {
                return jdbcTemplate.query(sql,
                    (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            } else {
                List<Mpa> mpaList = new ArrayList<>();
                Mpa mpa = jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);
                mpaList.add(mpa);
                return mpaList;
            }
        } catch (EmptyResultDataAccessException exc) {
            log.info("MpaDbStorage/" + type + ": ids- {}", id);
            throw new ObjectNotFoundException(type + " mpa: ids - " + id);
        } catch (DataAccessException exp) {
            log.info("MpaDbStorage/" + type + ": DB problem, ids - {}", id);
            throw new WorkDBException(type + " mpa:DB problem, ids - " + id);
        }
    }

}
