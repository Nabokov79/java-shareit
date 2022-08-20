package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.CustomException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long id;

    public void generateId() {
        if (id == 0) {
            id = 1;
        }
        for (Long key : users.keySet()) {
            id = Math.max(id, key) + 1;
        }
    }

    public User addUser(User user) {
        for (Long key : users.keySet()) {
            if (user.getEmail().equals(users.get(key).getEmail())) {
                throw new CustomException("User " + user.getEmail() + " found");
            }
        }
        generateId();
        user.setId(id);
        users.put(id, user);
        return getUser(id);
    }

    public User getUser(long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("User not found");
        }
        return users.get(id);
    }

    public User updateUser(long userId, User user) {
        user.setId(getUser(userId).getId());
        if (user.getName() == null) {
            user.setName(users.get(userId).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(userId).getEmail());
        } else {
            for (User user1 : users.values()) {
                if (user.getEmail().equals(user1.getEmail())) {
                    throw new CustomException("User " + user.getEmail() + " found");
                }
            }
        }
        users.put(userId, user);
        return getUser(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUser(long userId) {
        getUser(userId);
        users.remove(userId);
    }
}