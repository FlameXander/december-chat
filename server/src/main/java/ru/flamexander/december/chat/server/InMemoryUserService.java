package ru.flamexander.december.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryUserService implements UserService {

    private List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pass1", "user1", User.Role.USER),
                new User("login2", "pass2", "user2", User.Role.USER),
                new User("login3", "pass3", "user3", User.Role.USER),
                new User("admin", "admin", "admin", User.Role.ADMIN)
        ));
    }

    @Override
    public User getUserByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public User createNewUser(String login, String password, String username) {
        User newUser = new User(login, password, username);
        users.add(newUser);
        return newUser;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
