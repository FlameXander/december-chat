package ru.flamexander.december.chat.server;

import java.util.*;

public class InMemoryUserRole implements UserRole {
    /**
     * В название роли в K, а список доступов в V
     * Тут пока один доступ но могут быть и другие
     */
    private Map<String, HashSet<String>> roles = new HashMap<String, HashSet<String>>();;

    public InMemoryUserRole() {
        roles.put("Admin",new HashSet<String>());
        roles.put("User",new HashSet<String>());
        roles.get("Admin").add("kick");
        roles.get("Admin").add("sendPvt");
        roles.get("Admin").add("sendPvt");
    }

    @Override
    public boolean getAccessByRoleNameAndAccessName(String role, String access) {
        try {
            return roles.get(role).contains(access);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void addRole(String role) {
        roles.put(role, new HashSet<String>());
    }

    @Override
    public void addAccess(String role, String accessName) {
        //roles.put(role);
    }

}
