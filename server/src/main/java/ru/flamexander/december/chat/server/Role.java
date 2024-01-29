package ru.flamexander.december.chat.server;

public class Role {
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private Integer id;
    private String name;

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
