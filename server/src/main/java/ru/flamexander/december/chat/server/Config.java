package ru.flamexander.december.chat.server;

public class Config {
    public static final String BD_LOGIN = "postgres";
    public static final String BD_PASS = "postgres";
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/db_console_chat";


    public static String getBdLogin() {
        return BD_LOGIN;
    }

    public static String getBdPass() {
        return BD_PASS;
    }

    public static String getDbUrl() {
        return DB_URL;
    }
}
