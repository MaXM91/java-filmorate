package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa found(Integer id) {
        List<Mpa> mpa = jdbcTemplate.query("SELECT * FROM mpa WHERE mpa_id = ?",
            (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);

        if (mpa.size() == 0) {
            throw new ObjectNotFoundException(" mpa id - " + id + " not found");
        } else {
            return mpa.get(0);
        }
    }

    public List<Mpa> get() {
        return  jdbcTemplate.query("SELECT * FROM mpa GROUP BY mpa_id",
            (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }

}