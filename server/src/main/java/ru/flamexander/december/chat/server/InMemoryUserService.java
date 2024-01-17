package ru.flamexander.december.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryUserService implements UserService {
    class User {
        private String login;
        private String password;
        private String username;
        private String userRole;

        private boolean userEnable;

        public User(String login, String password, String username, String userRole, boolean userEnable) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.userRole = userRole;
            this.userEnable = userEnable;
        }
    }

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    // По моему, этот метод нужно приватным делать
    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pass1", "Admin", "ADMIN", true),
                new User("login2", "pass2", "user2", "USER", true),
                new User("login3", "pass3", "user3", "USER", true)
        ));
    }

    @Override
    public void setUserDisable(String username, boolean disable) {
        for (User u : users) {
            if (u.username.equals(username)) {
                u.userEnable = disable;
            }
        }
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.username;
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        // Есть админ. Все остальные, по умолчанию, будут простыми пользователями и включенные true
        users.add(new User(login, password, username, "USER", true)); // Есть админ. Все остальные будут простыми пользователями
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User user : users) {
            if (user.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        for (User user : users) {
            if (user.username.equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserAdmin(String username) {
        for (User user : users) {
            if (user.username.equals(username)) {
                if (user.userRole.equals("ADMIN")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isUserEnable(String username) {
        for (User user : users) {
            if (user.username.equals(username)) {
                if (user.userEnable) {
                    return true;
                }
            }
        }
        return false;
    }
}
