package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Genre found(Integer id) {
        List<Genre> genre = jdbcTemplate.query("SELECT * FROM genre WHERE genre_id = ?",
            (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), id);
        if (genre.size() == 0) {
            throw new ObjectNotFoundException(" genre id - " + id + " not found");
        } else {
            return genre.get(0);
        }
    }

    public List<Genre> get() {
        return jdbcTemplate.query("SELECT * FROM genre GROUP BY genre_id",
            (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
    }
}
