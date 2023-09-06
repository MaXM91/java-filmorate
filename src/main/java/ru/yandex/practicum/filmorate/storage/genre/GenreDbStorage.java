package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Genre found(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id = ?",
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), id);
        } catch (RuntimeException exc) {
            throw new ObjectNotFoundException("GenreDbStorage/found: genre not found!");
        }
    }

    public List<Genre> get() {
        return jdbcTemplate.query("SELECT * FROM genre",
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

}
