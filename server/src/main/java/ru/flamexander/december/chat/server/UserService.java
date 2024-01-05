package ru.flamexander.december.chat.server;

public interface UserService {
    User getUserByLoginAndPassword(String login, String password);
    User createNewUser(String login, String password, String username);
    boolean isLoginAlreadyExist(String login);
    boolean isUsernameAlreadyExist(String username);
}