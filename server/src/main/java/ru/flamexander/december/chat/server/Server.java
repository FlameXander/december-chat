package ru.flamexander.december.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private UserService userService;
    private UserRole userRole;

    public UserService getUserService() {
        return userService;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new InMemoryUserService();
            userRole = new InMemoryUserRole();
            System.out.println("Запущен сервис для работы с пользователями");
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandler(this, socket);
                } catch (IOException e) {
                    System.out.println("Не удалось подключить клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("Подключился новый клиент " + clientHandler.getUsername());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Отключился клиент " + clientHandler.getUsername());
    }

    public synchronized boolean isUserBusy(String username) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(receiverUsername)) {
                clientHandler.sendMessage("/pvt " + sender.getUsername() + ": " + message);
                sender.sendMessage("/pvt " + sender.getUsername() + ": " + message);
                break;
            }
        }
    }

    public synchronized void kickUserFromChat(ClientHandler author, String message) {

        String[] elements = message.split(" "); // /kick username
        if (elements.length != 2) {
            author.sendMessage("СЕРВЕР: некорректная команда");
            return;
        }
        String victim = elements[1];
        String role = getUserService().getRoleByUserName(author.getUsername());
        if (getUserRole().getAccessByRoleNameAndAccessName(role, "kick")) {
            for (ClientHandler clientHandler : clients) {
                if (clientHandler.getUsername().equals(victim)) {
                    clientHandler.disconnect();
                    getUserService().setUserBlock(victim);
                    return;
                }
            }
        }
        author.sendMessage("СЕРВЕР: нет прав на выполнение данной команды");
    }
}
