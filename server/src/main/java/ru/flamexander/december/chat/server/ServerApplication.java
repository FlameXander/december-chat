package ru.flamexander.december.chat.server;

public class ServerApplication {


    public static void main(String[] args) {

       Server server = new Server(8181);
       server.start();
    }
}