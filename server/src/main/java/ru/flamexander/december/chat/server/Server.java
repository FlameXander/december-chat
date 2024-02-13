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

    public UserService getUserService() {
        return userService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new InMemoryUserService();
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
        boolean flag = false;
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(receiverUsername)) {
                clientHandler.sendMessage("From " + sender.getUsername() + ": " + message);
                sender.sendMessage(sender.getUsername() + ": " + message);
                flag = true;
                break;
            }
        }
        if (!flag) {
            sender.sendMessage("user \"" + receiverUsername + "\" undefined");
        }
    }

    public synchronized boolean kickUser(String message, ClientHandler admin) {
        if (getUserService().isUserAdmin(admin.getUsername())) {
            String userNameForKick = message.split(" ")[1];
            for (ClientHandler cli : clients) {
                if (cli.getUsername().equals(userNameForKick)) {
                    sendPrivateMessage(admin, cli.getUsername(), "Вы кикнуты");
                    broadcastMessage("Клиент " + cli.getUsername() + " был кикнут админом чата");
                    cli.sendMessage("/exit_confirmed");
                    cli.disconnect();
                    return true;
                }
            }
            admin.sendMessage("Пользователя с именем " + userNameForKick + " не существует");
            return false;
        } else {
            admin.sendMessage("Нет прав на /kick");
        }
        return false;
    }
}