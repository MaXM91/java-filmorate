package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeDbStorage likeDbStorage;
    private final FilmService filmService;
    private final UserService userService;

    public boolean addLike(Integer filmId, Integer userId) {
        checkIds(filmId, userId);

        likeDbStorage.add(filmId, userId);

        log.info("LikeService/addLike: like added filmId - {}, userId - {}", filmId, userId);
        return true;
    }

    public boolean removeLike(Integer filmId, Integer userId) {
        checkIds(filmId, userId);

        likeDbStorage.remove(filmId, userId);

        log.info("LikeService/removeLike: like removed filmId - {}, userId - {}", filmId, userId);
        return true;
    }

    private void checkIds(Integer filmId, Integer userId) {
        filmService.getFilm(filmId);
        userService.found(userId);
    }
}