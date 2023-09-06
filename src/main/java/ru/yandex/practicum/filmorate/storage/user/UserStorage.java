package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
	User create(User user);

	User update(User user);

	User delete(Integer id);

	User found(Integer id);

	List<User> getFriends(Integer id);

	List<User> get();

	void addFriend(Integer id1, Integer id2);

	void deleteFriend(Integer id1, Integer id2);

	List<User> getMutualFriends(Integer id, Integer otherId);
}
