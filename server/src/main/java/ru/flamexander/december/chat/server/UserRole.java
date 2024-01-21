package ru.flamexander.december.chat.server;

public interface UserRole {
    void addRole(String role);

    void addAccess(String role, String accessName);

    boolean getAccessByRoleNameAndAccessName(String role, String access);
}
