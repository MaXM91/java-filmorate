package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Genre getGenreById(Integer id) {
        log.info("GenreService/getGenreById: genre Id - {} founded", id);
        return genreDbStorage.found(id);
    }

    public List<Genre> get() {
        log.info("GenreService/get: genres got");
        return genreDbStorage.get();
    }

}
