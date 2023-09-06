package ru.yandex.practicum.filmorate.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;

@Service
@RequiredArgsConstructor
public class LikeService {
	private final LikeDbStorage likeDbStorage;

	public boolean addLike(Integer filmId, Integer userId) {
		likeDbStorage.add(filmId, userId);
		return true;
	}

	public boolean removeLike(Integer filmId, Integer userId) {
		likeDbStorage.remove(filmId, userId);
		return true;
	}

}
