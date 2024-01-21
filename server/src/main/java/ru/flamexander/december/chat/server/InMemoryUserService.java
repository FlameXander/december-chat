package ru.flamexander.december.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InMemoryUserService implements UserService {

    class User {
        private String login;
        private String password;
        private String username;
        private boolean isBlocked;
        private String role;

        public User(String login, String password, String username, boolean isBlocked, String role) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.isBlocked = isBlocked;
            this.role = role;
        }
    }

    private List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pass1", "user1", false, "User"),
                new User("login2", "pass2", "user2", false, "User"),
                new User("login3", "pass3", "user3", false, "User"),
                new User("admin1", "pass4", "admin", false, "Admin")
        ));
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password) && !u.isBlocked) {
                return u.username;
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        users.add(new User(login, password, username, false,"User"));
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
            if (u.username.equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRoleByUserName(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                return u.role;
            }
        }
        return null;
    }

    @Override
    public void setUserBlock(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                u.isBlocked = true;
            }
        }
    }
    public void setUserUnblock(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                u.isBlocked = false;
            }
        }
    }
}
