package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class Film {
    private static final LocalDate DATE_BIRTH_CINEMA = LocalDate.of(1895, 12, 25);
    private int id;

    @NotBlank(message = "film: name is blank/empty!")
    private String name;

    @NotBlank(message = "film: description is blank/empty!")
    @Size(max = 200, message = "film: description contains more then 200 char!")
    private String description;

    private List<Genre> genres;

    private Mpa mpa;

    private Integer rate;

    @MinimumDate(message = "film: release date is before then date_birth_cinema date!")
    private LocalDate releaseDate;

    @Positive(message = "film: duration has a negative value")
    private long duration;
}
