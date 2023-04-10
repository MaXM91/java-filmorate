package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
public class User {

    private int id;

    private HashMap<Integer, Boolean> userFriends = new HashMap<>();

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
        userFriends.put(id, false);
    }

    public List<Integer> getUserFriends() {
        return new ArrayList<>(userFriends.keySet());
    }

    public void deleteUserFriends(Integer id) {
        userFriends.remove(id);
    }

}
