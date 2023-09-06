package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final MpaDbStorage mpaStorage;
	private final LikeDbStorage likeStorage;
	private final GenreDbStorage genreStorage;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///	UserDbStorage///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testCreateUser() {
		LocalDate user1LocalDate = LocalDate.of(2016, 1, 22);

		Optional<User> userOptional = Optional.ofNullable(userStorage.create(new User(0,
			"777@bk.ru", "login777", "name777", user1LocalDate)));

		assertThat(userOptional)
			.isPresent()
			.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 10)
			);
	}

	@Test
	public void testUpdateUser() {
		LocalDate user1LocalDate = LocalDate.of(2016, 1, 22);

		Optional<User> userOptional = Optional.ofNullable(userStorage.update(new User(1,
			"777update@bk.ru", "update777", "update777", user1LocalDate)));

		assertThat(userOptional)
			.isPresent()
			.hasValueSatisfying(user -> {
					assertThat(user).hasFieldOrPropertyWithValue("id", 1);
					assertThat(user).hasFieldOrPropertyWithValue("email", "777update@bk.ru");
					assertThat(user).hasFieldOrPropertyWithValue("login", "update777");
					assertThat(user).hasFieldOrPropertyWithValue("name", "update777");
					assertThat(user).hasFieldOrPropertyWithValue("birthday", user1LocalDate);
				}
			);
	}

	@Test
	public void testDeleteUser() {
		userStorage.delete(5);

		ObjectNotFoundException exc = assertThrows(ObjectNotFoundException.class, () -> userStorage.found(5));
		assertEquals("UserService/found: user not found!", exc.getMessage());
	}

	@Test
	public void testFoundUser() {
		LocalDate user5LocalDate = LocalDate.of(1990, 1, 5);

		Optional<User> userOptional = Optional.ofNullable(userStorage.found(5));

		assertThat(userOptional)
			.isPresent()
			.hasValueSatisfying(user -> {
					assertThat(user).hasFieldOrPropertyWithValue("id", 5);
					assertThat(user).hasFieldOrPropertyWithValue("email", "email5");
					assertThat(user).hasFieldOrPropertyWithValue("login", "login5");
					assertThat(user).hasFieldOrPropertyWithValue("name", "name5");
					assertThat(user).hasFieldOrPropertyWithValue("birthday", user5LocalDate);
				}
			);
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
			assertEquals(1, friendsOptional.get().size(),
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

		Optional<User> userOptional = Optional.ofNullable(userStorage.getMutualFriends(1, 3).get(0));

		assertThat(userOptional)
			.isPresent()
			.hasValueSatisfying(user -> {
					assertThat(user).hasFieldOrPropertyWithValue("id", 2);
					assertThat(user).hasFieldOrPropertyWithValue("email", "email2");
					assertThat(user).hasFieldOrPropertyWithValue("login", "login2");
					assertThat(user).hasFieldOrPropertyWithValue("name", "name2");
					assertThat(user).hasFieldOrPropertyWithValue("birthday", user2LocalDate);
				}
			);
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////FilmDbStorage///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testCreateFilm() {
		LocalDate film13LocalDate = LocalDate.of(2016, 1, 22);

		Optional<Film> userOptional = Optional.ofNullable(filmStorage.create(new Film(0, "film13",
			"film13", null, null, null, film13LocalDate, 120)));

		assertThat(userOptional)
			.isPresent()
			.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 13)
			);
	}

	@Test
	public void testUpdateFilm() {
		LocalDate film1LocalDate = LocalDate.of(1995, 1, 22);

		Optional<Film> userOptional = Optional.ofNullable(filmStorage.update(new Film(1, "777update@bk.ru",
			"update777", null, null, null, film1LocalDate, 777)));

		assertThat(userOptional)
			.isPresent()
			.hasValueSatisfying(user -> {
					assertThat(user).hasFieldOrPropertyWithValue("id", 1);
					assertThat(user).hasFieldOrPropertyWithValue("name", "777update@bk.ru");
					assertThat(user).hasFieldOrPropertyWithValue("description", "update777");
					assertThat(user).hasFieldOrPropertyWithValue("genres", null);
					assertThat(user).hasFieldOrPropertyWithValue("mpa", null);
					assertThat(user).hasFieldOrPropertyWithValue("rate", null);
					assertThat(user).hasFieldOrPropertyWithValue("releaseDate", film1LocalDate);
					assertThat(user).hasFieldOrPropertyWithValue("duration", 777L);
				}
			);
	}

	@Test
	public void testDeleteFilm() {
		filmStorage.delete(5);

		ObjectNotFoundException exc = assertThrows(ObjectNotFoundException.class, () -> filmStorage.found(5));
		assertEquals("FilmDbStorage/found: film not found!", exc.getMessage());
	}

	@Test
	public void testFoundFilm() {
		LocalDate film7LocalDate = LocalDate.of(1990, 2, 7);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.found(7));
		List<Genre> genre7 = new ArrayList<>();
		genre7.add(new Genre(6, "Боевик"));
		Mpa mpa7 = new Mpa(5, "NC-17");

		assertThat(filmOptional)
			.isPresent()
			.hasValueSatisfying(film -> {
					assertThat(film).hasFieldOrPropertyWithValue("id", 7);
					assertThat(film).hasFieldOrPropertyWithValue("name", "name7");
					assertThat(film).hasFieldOrPropertyWithValue("description", "description7");
					assertEquals(film.getGenres(), genre7, "Список жанров не совпадает");
					assertThat(film).hasFieldOrPropertyWithValue("mpa", mpa7);
					assertThat(film).hasFieldOrPropertyWithValue("rate", 1);
					assertThat(film).hasFieldOrPropertyWithValue("releaseDate", film7LocalDate);
					assertThat(film).hasFieldOrPropertyWithValue("duration", 107L);
				}
			);
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

		assertThat(filmOptional)
			.isPresent()
			.hasValueSatisfying(new Consumer<List<Film>>() {
				@Override
				public void accept(List<Film> film) {
					Film filmTop = film.get(0);
					assertThat(filmTop).hasFieldOrPropertyWithValue("id", 12);
					assertThat(filmTop).hasFieldOrPropertyWithValue("name", "name12");
					assertThat(filmTop).hasFieldOrPropertyWithValue("description", "description12");
					assertEquals(filmTop.getGenres(), genre12, "Список жанров не совпадает");
					assertThat(filmTop).hasFieldOrPropertyWithValue("mpa", mpa12);
					assertThat(filmTop).hasFieldOrPropertyWithValue("rate", 3);
					assertThat(filmTop).hasFieldOrPropertyWithValue("releaseDate", film12LocalDate);
					assertThat(filmTop).hasFieldOrPropertyWithValue("duration", 112L);
				}
			});
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////MpaDbStorage///////////////////////////////////////////////////////////////////////////////////////////////////////
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

		assertThat(mpaOptional)
			.isPresent()
			.hasValueSatisfying(new Consumer<List<Mpa>>() {
				@Override
				public void accept(List<Mpa> mpa) {
					Mpa mpaId1 = mpa.get(0);
					assertThat(mpaId1).hasFieldOrPropertyWithValue("id", 1);
					assertThat(mpaId1).hasFieldOrPropertyWithValue("name", "G");
				}
			});
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////GenreDbStorage///////////////////////////////////////////////////////////////////////////////////////////////////////
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

		assertThat(genreOptional)
			.isPresent()
			.hasValueSatisfying(new Consumer<List<Genre>>() {
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
		likeStorage.remove(6, 1);

		Optional<Film> filmAfter = Optional.ofNullable(filmStorage.found(6));

		if (filmBefore.isPresent() && filmAfter.isPresent()) {
			assertEquals(filmBefore.get().getRate() - 1, filmAfter.get().getRate(),
				"Количество лайков не совпадает");
		}
	}
}