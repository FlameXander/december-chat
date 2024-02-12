package ru.flamexander.december.chat.server;

public enum Queries {
    SELECT_USERS ("select login, password, username from registered_users"),
    USERNAME_BY_LOGIN_AND_PASSWORD ("select username from registered_users where login = ? and password = ?"),
    INSERT_NEW_USER ("insert into registered_users (login, password, username) values (?, ?, ?)"),
    INSERT_NEW_USER_ROLE ("insert into user_role (user_id) " +
            "select id from registered_users order by 1 desc limit 1"),
    SELECT_LOGIN_BY_VALUE ("select login from registered_users where login = ?"),
    SELECT_USERNAME_BY_VALUE ("select username from registered_users where username = ?"),
    INSERT_ROLE_BY_USER ("insert into user_role (user_id, role_id) " +
            "select id, (select id from roles where name = ?) " +
            "from registered_users where username = ?"),
    SELECT_ROLE_BY_USER ("select r.name from roles r " +
            "join user_role ur on r.id = ur.role_id " +
            "join registered_users ru on ru.id = ur.user_id " +
            "where username = ?");

    private String value;
    Queries(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
