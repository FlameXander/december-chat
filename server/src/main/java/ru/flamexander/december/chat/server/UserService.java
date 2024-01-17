package ru.flamexander.december.chat.server;

public interface UserService {
    //String getUserRole(String userRole);
    void setUserDisable(String username, boolean disable);
    boolean isUserEnable(String username);
    String getUsernameByLoginAndPassword(String login, String password);
    void createNewUser(String login, String password, String username);
    boolean isLoginAlreadyExist(String login);
    boolean isUsernameAlreadyExist(String username);
    boolean isUserAdmin(String login);
}
