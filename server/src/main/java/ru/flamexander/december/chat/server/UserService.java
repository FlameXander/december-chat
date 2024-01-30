package ru.flamexander.december.chat.server;

public interface UserService {
    String getUsernameByLoginAndPassword(String login, String password);
    void createNewUser(String login, String password, String username);
    boolean isLoginAlreadyExist(String login);
    boolean isUsernameAlreadyExist(String username);
}