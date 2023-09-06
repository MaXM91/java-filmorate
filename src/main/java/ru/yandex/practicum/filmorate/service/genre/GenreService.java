package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Service @RequiredArgsConstructor public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Genre getGenreById(Integer id) {
        return genreDbStorage.found(id);
    }

    public List<Genre> get() {
        return genreDbStorage.get();
    }
}
