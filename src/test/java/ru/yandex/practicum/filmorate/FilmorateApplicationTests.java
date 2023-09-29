package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.validators.exceptions.ObjectNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final LikeDbStorage likeStorage;
    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///	UserDbStorage///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @BeforeEach
    public void startDataBase() {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday)\n" +
            "VALUES ('email1', 'login1', 'name1', '1990-01-01'),\n" +
            "('email2', 'login2', 'name2', '1990-01-02'),\n" +
            "('email3', 'login3', 'name3', '1990-01-03'),\n" +
            "('email4', 'login4', 'name4', '1990-01-04'),\n" +
            "('email5', 'login5', 'name5', '1990-01-05'),\n" +
            "('email6', 'login6', 'name6', '1990-01-06'),\n" +
            "('email7', 'login7', 'name7', '1990-01-07'),\n" +
            "('email8', 'login8', 'name8', '1990-01-08'),\n" +
            "('email9', 'login9', 'name9', '1990-01-09');");

        jdbcTemplate.update("INSERT INTO films (name, description, mpa_id, releasedate, duration)\n" +
            "VALUES ('name1', 'description1', 1, '1990-02-01', 101),\n" +
            "('name2', 'description2', 2, '1990-02-02', 102),\n" +
            "('name3', 'description3', 3, '1990-02-03', 103),\n" +
            "('name4', 'description4', 3, '1990-02-04', 104),\n" +
            "('name5', 'description5', 4, '1990-02-05', 105),\n" +
            "('name6', 'description6', 3, '1990-02-06', 106),\n" +
            "('name7', 'description7', 5, '1990-02-07', 107),\n" +
            "('name8', 'description8', 5, '1990-02-08', 108),\n" +
            "('name9', 'description9', 5, '1990-02-09', 109),\n" +
            "('name10', 'description10', 5, '1990-02-10', 110),\n" +
            "('name11', 'description11', 4, '1990-02-11', 111),\n" +
            "('name12', 'description12', 3, '1990-02-12', 112);");

        jdbcTemplate.update(
            "INSERT INTO film_genre\n" +
                "VALUES (1, 1),\n" +
                "       (2, 2),\n" +
                "       (1, 3),\n" +
                "       (3, 3),\n" +
                "       (4, 4),\n" +
                "       (6, 3),\n" +
                "       (5, 5),\n" +
                "       (6, 6),\n" +
                "       (7, 6),\n" +
                "       (8, 5),\n" +
                "       (9, 4),\n" +
                "       (10, 3),\n" +
                "       (11, 2),\n" +
                "       (12, 1);");

        jdbcTemplate.update(
            "INSERT INTO friends (user_id_from, user_id_to)\n" +
                "VALUES (1, 2),\n" +
                "       (2, 3),\n" +
                "       (3, 2),\n" +
                "       (3, 4),\n" +
                "       (4, 5),\n" +
                "       (5, 6),\n" +
                "       (6, 5),\n" +
                "       (2, 7),\n" +
                "       (7, 8),\n" +
                "       (8, 1),\n" +
                "       (7, 1),\n" +
                "       (6, 2),\n" +
                "       (4, 7),\n" +
                "       (3, 1);");

        jdbcTemplate.update(
            "INSERT INTO likes\n" +
                "VALUES (1, 1),\n" +
                "       (3, 1),\n" +
                "       (4, 1),\n" +
                "       (5, 2),\n" +
                "       (7, 3),\n" +
                "       (8, 4),\n" +
                "       (9, 5),\n" +
                "       (12, 5),\n" +
                "       (12, 6),\n" +
                "       (1, 7),\n" +
                "       (12, 8),\n" +
                "       (12, 3),\n" +
                "       (6, 7);");

    }

    @Test
    public void testCreateUser() {
        LocalDate user1LocalDate = LocalDate.of(2016, 1, 22);

        Optional<User> userOptional = Optional.ofNullable(
            userStorage.create(new User(0,null,"777@bk.ru", "login777", "name777", user1LocalDate)));

        assertThat(userOptional).isPresent().hasValueSatisfying(
            user -> assertThat(user).hasFieldOrPropertyWithValue("id", 10));
    }

    @Test
    public void testUpdateUser() {
        LocalDate user1LocalDate = LocalDate.of(2016, 1, 22);

        Optional<User> userOptional = Optional.ofNullable(userStorage.update(
            new User(1, null, "777update@bk.ru", "update777", "update777", user1LocalDate)));

        assertThat(userOptional).isPresent().hasValueSatisfying(user -> {
            assertThat(user).hasFieldOrPropertyWithValue("id", 1);
            assertThat(user).hasFieldOrPropertyWithValue("email", "777update@bk.ru");
            assertThat(user).hasFieldOrPropertyWithValue("login", "update777");
            assertThat(user).hasFieldOrPropertyWithValue("name", "update777");
            assertThat(user).hasFieldOrPropertyWithValue("birthday", user1LocalDate);
        });
    }

    @Test
    public void testDeleteUser() {
        userStorage.delete(5);

        ObjectNotFoundException exc = assertThrows(ObjectNotFoundException.class,
            () -> userStorage.found(5));
        assertEquals("found user: user with id - " + 5 + " not found!", exc.getMessage());
    }

    @Test
    public void testFoundUser() {
        LocalDate user5LocalDate = LocalDate.of(1990, 1, 5);

        Optional<User> userOptional = Optional.ofNullable(userStorage.found(5));

        assertThat(userOptional).isPresent().hasValueSatisfying(user -> {
            assertThat(user).hasFieldOrPropertyWithValue("id", 5);
            assertThat(user).hasFieldOrPropertyWithValue("email", "email5");
            assertThat(user).hasFieldOrPropertyWithValue("login", "login5");
            assertThat(user).hasFieldOrPropertyWithValue("name", "name5");
            assertThat(user).hasFieldOrPropertyWithValue("birthday", user5LocalDate);
        });
    }

    @Test
    public void testGetUser() {
        Optional<List<User>> userOptional = Optional.ofNullable(userStorage.get());

        if (userOptional.isPresent()) {
            Assertions.assertEquals(9, userOptional.get().size(),
                "Количество юзеров не соответствует количеству в БД");
        }
    }

    @Test
    public void testGetFriendsUser() {

        Optional<List<User>> friendsOptional = Optional.ofNullable(userStorage.getFriends(1));
        if (friendsOptional.isPresent()) {
            assertEquals(1, friendsOptional.get().size(),
                "Количество юзеров не соответствует количеству в БД");
        }
    }

    @Test
    public void testAddFriendUser() {
        userStorage.addFriend(1, 9);

        Optional<List<User>> friendsOptional = Optional.ofNullable(userStorage.getFriends(1));
        if (friendsOptional.isPresent()) {
            assertEquals(2, friendsOptional.get().size(),
                "Количество юзеров не соответствует количеству в БД");
        }
    }

    @Test
    public void testDeleteFriendUser() {
        userStorage.deleteFriend(1, 2);

        Optional<List<User>> friendsOptional = Optional.ofNullable(userStorage.getFriends(1));
        if (friendsOptional.isPresent()) {
            assertEquals(0, friendsOptional.get().size(),
                "Количество юзеров не соответствует количеству в БД");
        }
    }

    @Test
    public void testGetMutualFriendsUser() {

        LocalDate user2LocalDate = LocalDate.of(1990, 1, 2);

        Optional<User> userOptional = Optional.ofNullable(
            userStorage.getMutualFriends(1, 3).get(0));

        assertThat(userOptional).isPresent().hasValueSatisfying(user -> {
            assertThat(user).hasFieldOrPropertyWithValue("id", 2);
            assertThat(user).hasFieldOrPropertyWithValue("email", "email2");
            assertThat(user).hasFieldOrPropertyWithValue("login", "login2");
            assertThat(user).hasFieldOrPropertyWithValue("name", "name2");
            assertThat(user).hasFieldOrPropertyWithValue("birthday", user2LocalDate);
        });
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////FilmDbStorage///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateFilm() {
        LocalDate film13LocalDate = LocalDate.of(2016, 1, 22);

        Optional<Film> userOptional = Optional.ofNullable(filmStorage.create(
            new Film(0, "film13", "film13", null, null, null, film13LocalDate, 120)));

        assertThat(userOptional).isPresent().hasValueSatisfying(
            user -> assertThat(user).hasFieldOrPropertyWithValue("id", 13));
    }

    @Test
    public void testUpdateFilm() {
        LocalDate film1LocalDate = LocalDate.of(1995, 1, 22);

        Optional<Film> userOptional = Optional.ofNullable(filmStorage.update(
            new Film(1, "777update@bk.ru", "update777", null, null, null, film1LocalDate, 777)));

        assertThat(userOptional).isPresent().hasValueSatisfying(user -> {
            assertThat(user).hasFieldOrPropertyWithValue("id", 1);
            assertThat(user).hasFieldOrPropertyWithValue("name", "777update@bk.ru");
            assertThat(user).hasFieldOrPropertyWithValue("description", "update777");
            assertThat(user).hasFieldOrPropertyWithValue("genres", null);
            assertThat(user).hasFieldOrPropertyWithValue("mpa", null);
            assertThat(user).hasFieldOrPropertyWithValue("rate", null);
            assertThat(user).hasFieldOrPropertyWithValue("releaseDate", film1LocalDate);
            assertThat(user).hasFieldOrPropertyWithValue("duration", 777L);
        });
    }

    @Test
    public void testDeleteFilm() {
        filmStorage.delete(5);

        ObjectNotFoundException exc = assertThrows(ObjectNotFoundException.class,
            () -> filmStorage.found(5));
        assertEquals("FilmDbStorage/found: film id - " + 5 + " not found!", exc.getMessage());
    }

    @Test
    public void testFoundFilm() {
        LocalDate film7LocalDate = LocalDate.of(1990, 2, 7);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.found(7));
        List<Genre> genre7 = new ArrayList<>();
        genre7.add(new Genre(6, "Боевик"));
        Mpa mpa7 = new Mpa(5, "NC-17");

        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> {
            assertThat(film).hasFieldOrPropertyWithValue("id", 7);
            assertThat(film).hasFieldOrPropertyWithValue("name", "name7");
            assertThat(film).hasFieldOrPropertyWithValue("description", "description7");
            assertEquals(film.getGenres(), genre7, "Список жанров не совпадает");
            assertThat(film).hasFieldOrPropertyWithValue("mpa", mpa7);
            assertThat(film).hasFieldOrPropertyWithValue("rate", 1);
            assertThat(film).hasFieldOrPropertyWithValue("releaseDate", film7LocalDate);
            assertThat(film).hasFieldOrPropertyWithValue("duration", 107L);
        });
    }

    @Test
    public void testGetFilm() {
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmStorage.get());

        if (filmOptional.isPresent()) {
            Assertions.assertEquals(12, filmOptional.get().size(),
                "Количество фильмов не соответствует количеству в БД");
        }
    }

    @Test
    public void testPopularFilms() {
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmStorage.popularFilms(5));

        if (filmOptional.isPresent()) {
            Assertions.assertEquals(5, filmOptional.get().size(),
                "Количество фильмов не соответствует количеству в БД");
        }

        LocalDate film12LocalDate = LocalDate.of(1990, 2, 12);
        List<Genre> genre12 = new ArrayList<>();
        genre12.add(new Genre(1, "Комедия"));
        Mpa mpa12 = new Mpa(3, "PG-13");

        assertThat(filmOptional).isPresent().hasValueSatisfying(new Consumer<List<Film>>() {
            @Override
            public void accept(List<Film> film) {
                Film filmTop = film.get(0);
                assertThat(filmTop).hasFieldOrPropertyWithValue("id", 12);
                assertThat(filmTop).hasFieldOrPropertyWithValue("name", "name12");
                assertThat(filmTop).hasFieldOrPropertyWithValue("description", "description12");
                assertEquals(filmTop.getGenres(), genre12, "Список жанров не совпадает");
                assertThat(filmTop).hasFieldOrPropertyWithValue("mpa", mpa12);
                assertThat(filmTop).hasFieldOrPropertyWithValue("rate", 4);
                assertThat(filmTop).hasFieldOrPropertyWithValue("releaseDate", film12LocalDate);
                assertThat(filmTop).hasFieldOrPropertyWithValue("duration", 112L);
            }
        });
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////MpaDbStorage////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testFoundMpa() {

        Optional<Mpa> mpaOptional = Optional.ofNullable(mpaStorage.found(1));
        Mpa mpaT = new Mpa(1, "G");

        if (mpaOptional.isPresent()) {
            assertEquals(mpaT, mpaOptional.get(), "Данные mpa не соответствуют");
        }
    }

    @Test
    public void testGetMpa() {
        Optional<List<Mpa>> mpaOptional = Optional.ofNullable(mpaStorage.get());

        if (mpaOptional.isPresent()) {
            Assertions.assertEquals(5, mpaOptional.get().size(),
                "Количество категорий рейтинга не соответствует");
        }

        assertThat(mpaOptional).isPresent().hasValueSatisfying(new Consumer<List<Mpa>>() {
            @Override
            public void accept(List<Mpa> mpa) {
                Mpa mpaId1 = mpa.get(0);
                assertThat(mpaId1).hasFieldOrPropertyWithValue("id", 1);
                assertThat(mpaId1).hasFieldOrPropertyWithValue("name", "G");
            }
        });
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////GenreDbStorage//////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testFoundGenre() {

        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.found(1));
        Genre genreT = new Genre(1, "Комедия");

        if (genreOptional.isPresent()) {
            assertEquals(genreT, genreOptional.get(), "Данные mpa не соответствуют");
        }
    }

    @Test
    public void testGetGenre() {
        Optional<List<Genre>> genreOptional = Optional.ofNullable(genreStorage.get());

        if (genreOptional.isPresent()) {
            Assertions.assertEquals(6, genreOptional.get().size(),
                "Количество категорий жанров не соответствует");
        }

        assertThat(genreOptional).isPresent().hasValueSatisfying(new Consumer<List<Genre>>() {
            @Override
            public void accept(List<Genre> genre) {
                Genre genreId3 = genre.get(2);
                assertThat(genreId3).hasFieldOrPropertyWithValue("id", 3);
                assertThat(genreId3).hasFieldOrPropertyWithValue("name", "Мультфильм");
            }
        });
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////LikeDbStorage///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testAddLike() {
        Optional<Film> filmBefore = Optional.ofNullable(filmStorage.found(6));
        likeStorage.add(6, 1);

        Optional<Film> filmAfter = Optional.ofNullable(filmStorage.found(6));

        if (filmBefore.isPresent() && filmAfter.isPresent()) {
            assertEquals(filmBefore.get().getRate() + 1, filmAfter.get().getRate(),
                "Количество лайков не совпадает");
        }
    }

    @Test
    public void testRemoveLike() {
        Optional<Film> filmBefore = Optional.ofNullable(filmStorage.found(6));
        likeStorage.remove(6, 7);

        Optional<Film> filmAfter = Optional.ofNullable(filmStorage.found(6));

        if (filmBefore.isPresent() && filmAfter.isPresent()) {
            assertEquals(0, filmAfter.get().getRate(), "Количество лайков не совпадает");
        }
    }

}