package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.MinimumDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class Film {
    private static final LocalDate DATE_BIRTH_CINEMA = LocalDate.of(1895, 12, 25);
    private int id;


    @NotBlank(message = "film: name is blank!")
    @NotEmpty(message = "film: name is empty!")
    private String name;

    @NotBlank(message = "film: description is blank")
    @NotEmpty(message = "film: description is empty!")
    @Size(max = 200, message = "film: description contains more then 200 char!")
    private String description;

    private List<Genre> genres;

    private Mpa mpa;

    private Integer rate;

    @MinimumDate(message = "film: release date is before then date_birth_cinema date!")
    private LocalDate releaseDate;

    @Positive(message = "film: duration has a negative value")
    private long duration;

    public Film(int id, String name, String description, List<Genre> genres, Mpa mpa, Integer rate,
                LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.genres = genres;
        this.mpa = mpa;
        this.rate = rate;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
