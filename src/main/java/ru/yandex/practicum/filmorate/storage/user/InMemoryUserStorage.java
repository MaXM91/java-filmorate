package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
//Work with users
///////////////////////////////////////////////////////////////////////////////////////////////////
    private int userId = 1;
    private static final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Integer id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    @Override
    public User found(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }

        return null;
    }

    @Override
    public List<User> getFriends(Integer id) {
        List<User> friends = new ArrayList<>();

        if (users.get(id).getUserFriends().isEmpty()) {
            return null;
        }
        for (Integer friendId : users.get(id).getUserFriends()) {
            friends.add(users.get(friendId));
        }
        return friends;
    }

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

// Work with friends list
///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void addFriend(Integer id, Integer idFriend) {
        users.get(id).setUserFriends(idFriend);
        users.get(idFriend).setUserFriends(id);
    }

    @Override
    public void deleteFriend(Integer id, Integer idFriend) {
        users.get(id).deleteUserFriends(idFriend);
        users.get(idFriend).deleteUserFriends(id);
    }
}
