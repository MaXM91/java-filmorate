package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.MinimumDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private static final LocalDate DATE_BIRTH_CINEMA = LocalDate.of(1895, 12, 25);
    private Set<Integer> usersLike = new HashSet<>();
    private int id;

    @NotBlank(message = "film: name is blank!")
    @NotEmpty(message = "film: name is empty!")
    private String name;

    @NotBlank(message = "film: description is blank")
    @NotEmpty(message = "film: description is empty!")
    @Size(max = 200, message = "film: description contains more then 200 char!")
    private String description;

    private Genre genre;
    private Rating rating;

    @MinimumDate(message = "film: release date is before then date_birth_cinema date!")
    private LocalDate releaseDate;

    @Positive(message = "film: duration has a negative value")
    private long duration;

    public void setUsersLike(Integer id) {
        usersLike.add(id);
    }

    public List<Integer> getUsersLike() {
        return new ArrayList<>(usersLike);
    }
    public void deleteUsersLike(Integer id) {
        usersLike.remove(id);
    }

}
