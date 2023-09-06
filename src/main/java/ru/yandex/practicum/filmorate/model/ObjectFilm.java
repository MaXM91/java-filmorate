package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class ObjectFilm {
    private int id;

    public ObjectFilm(int id) {
        this.id = id;
    }

}
