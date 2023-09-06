package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa found(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE mpa_id = ?",
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")), id);
        } catch (RuntimeException ecx) {
            throw new ObjectNotFoundException("MpaDbStorage/mpa not found");
        }
    }

    public List<Mpa> get() {
        return jdbcTemplate.query("SELECT * FROM mpa",
            (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")));
    }
}
