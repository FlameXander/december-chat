package ru.flamexander.december.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryUserService implements UserService {
    private Server server;

    class User {
        private String login;
        private String password;
        private String username;
        private UserRole userRole;

        public User(String login, String password, String username, UserRole userRole) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.userRole = userRole;
        }
    }

    private List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pass1", "user1", UserRole.ADMIN),
                new User("login2", "pass2", "user2", UserRole.USER),
                new User("login3", "pass3", "user3", UserRole.USER)
        ));
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
    public UserRole getRoleByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.userRole;
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username, UserRole userRole) {
        users.add(new User(login, password, username, userRole));
    }
    @Override
    public void deleteUser(String username){
        if(isUsernameAlreadyExist(username)){
            for (User u : users) {
                if (u.username.equals(username)){
                    users.remove(u);
                    server.broadcastMessage("Пользователь: " + username+ "удален");
                }
            }
        } else{
            System.out.println("Пользователя с таким именем нет");
        }
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
}
