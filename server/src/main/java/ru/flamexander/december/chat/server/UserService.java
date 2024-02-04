package ru.flamexander.december.chat.server;

import java.sql.SQLException;

public interface UserService {
    String getUsernameByLoginAndPassword(String login, String password) throws SQLException;
    void createNewUser(String login, String password, String username) throws SQLException;
    boolean isLoginAlreadyExist(String login) throws SQLException;
    boolean isUsernameAlreadyExist(String username) throws SQLException;
    void setRole(String role, String login) throws SQLException;
    boolean isUserAdmin(String login) throws SQLException;
    void disconnect();
}