package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
private static final HashMap<Integer, User> users = new HashMap<>();
//Work with users
///////////////////////////////////////////////////////////////////////////////////////////////////
    private int userId = 1;

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
        Set<Integer> friends;
        friends = users.get(id).getUserFriends();
        friends.add(idFriend);
        users.get(id).setUserFriends(friends);
        friends = users.get(idFriend).getUserFriends();
        friends.add(id);
        users.get(idFriend).setUserFriends(friends);
    }

    @Override
    public void deleteFriend(Integer id, Integer idFriend) {
        users.get(id).deleteUserFriends(idFriend);
        users.get(idFriend).deleteUserFriends(id);
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        List<User> mutualFriends = new ArrayList<>();

        for (Integer friendsId : users.get(id).getUserFriends()) {
            if (users.get(otherId).getUserFriends().contains(friendsId)) {
                mutualFriends.add(users.get(friendsId));
            }
        }
        return mutualFriends;
    }

}
