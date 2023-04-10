package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa found(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE mpa_id = ?", new RowMapper<Mpa>() {
                @Override
                public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Mpa(rs.getInt("mpa_id"), rs.getString("name"));
                }
            }, id);
        } catch (RuntimeException ecx) {
            throw new ObjectNotFoundException("MpaDbStorage/mpa not found");
        }
    }

    public List<Mpa> get() {
        List<Mpa> mpaList = jdbcTemplate.query("SELECT * FROM mpa", new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Mpa(rs.getInt("mpa_id"), rs.getString("name"));
            }
        });
        return mpaList;
    }
}
