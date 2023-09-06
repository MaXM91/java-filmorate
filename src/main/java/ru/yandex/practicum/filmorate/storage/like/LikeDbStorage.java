package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeDbStorage {
	private final JdbcTemplate jdbcTemplate;

	public LikeDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void add(Integer filmId, Integer userId) {
		jdbcTemplate.update("INSERT INTO likes(film_id, user_id) VALUES(?, ?)", filmId, userId);
	}

	public void remove(Integer filmId, Integer userId) {
		jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
	}
}
