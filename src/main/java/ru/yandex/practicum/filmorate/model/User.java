package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class User {

    private int id;

    private Set<Integer> userFriends = new HashSet<>();

    @Email(message = "user: bad mail")
    private String email;

    @NotEmpty(message = "user: login is empty!")
    @NotBlank(message = "user: login is blank!")
    @Pattern(regexp = "\\S*", message = "user: incorrect login!")
    private String login;

    private String name;

    @PastOrPresent(message = "user: bad data of birthday!")
    private LocalDate birthday;

    public void setUserFriends(Integer id) {
        userFriends.add(id);
    }

    public List<Integer> getUserFriends() {
        return new ArrayList<>(userFriends);
    }

    public void deleteUserFriends(Integer id) {
        userFriends.remove(id);
    }

}
