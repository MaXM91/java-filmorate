package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.MinimumDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private static final LocalDate DATE_BIRTH_CINEMA = LocalDate.of(1895, 12, 25);

    private int id;

    @NotBlank
    @NotEmpty
    private String name;

    @NotEmpty
    @NotBlank
    @Size(max = 200)
    private String description;

    @MinimumDate
    private LocalDate releaseDate;

    @Positive
    private long duration;

}
