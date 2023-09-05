package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.like.LikeService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable Integer id) {
        return filmService.delete(id);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.get();
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> popularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.popularFilms(count);
    }
}
