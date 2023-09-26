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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Genre found(Integer id) {
        return giveGenre("SELECT * FROM genre WHERE genre_id = ?", id, "found").get(0);
    }

    public List<Genre> get() {
        return giveGenre("SELECT * FROM genre GROUP BY genre_id", null, "get");
    }

    private List<Genre> giveGenre(String sql, Integer id, String type) {
        try {
            if (id == null) {
                return jdbcTemplate.query(sql,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
            } else {
                List<Genre> genreList = new ArrayList<>();
                Genre genre = jdbcTemplate.queryForObject(sql,
                        (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), id);
                genreList.add(genre);
                return genreList;
            }
        } catch (EmptyResultDataAccessException exc) {
                log.info("GenreDbStorage/" + type + ": ids- {}", id);
                throw new ObjectNotFoundException(type + " genre: ids - " + id);
        } catch (DataAccessException exp) {
                log.info("GenreDbStorage/" + type + ": DB problem, ids - {}", id);
                throw new WorkDBException(type + " genre:DB problem, ids - " + id);
        }
    }

}
