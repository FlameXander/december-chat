package ru.flamexander.december.chat.server;

public interface UserService {
    String getUsernameByLoginAndPassword(String login, String password);
    void createNewUser(String login, String password, String username, UserRole userRole);
    void deleteUser(String username);
    boolean isLoginAlreadyExist(String login);
    boolean isUsernameAlreadyExist(String username);
    UserRole getRoleByLoginAndPassword(String login, String password);
}