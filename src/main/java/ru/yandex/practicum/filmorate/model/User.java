package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
public class User {
    private int id;
    private Set<Integer> userFriends;

    @Email(message = "user: bad mail")
    private String email;

    @NotBlank(message = "user: login is blank/empty!")
    @Pattern(regexp = "\\S*", message = "user: incorrect login!")
    private String login;

    private String name;

    @PastOrPresent(message = "user: bad data of birthday!")
    private LocalDate birthday;

    public void deleteUserFriends(Integer idFriend) {
        userFriends.remove(idFriend);
    }
}
