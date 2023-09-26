package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ObjectFilm {
    private int filmId;
    private List<Genre> genres;
    private Mpa mpa;
}
