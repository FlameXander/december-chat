package ru.flamexander.december.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Урок 23.Консольный сетевой чат. Часть 2
 * ДЗ 16: Доработка консольного сетевого чата
 */
public class Server {
    private int port;
    private List<ClientHandler> clients;
    private UserService userService;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public UserService getUserService() {
        return userService;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new InJdbcUserService();
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
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        // TODO homework
        boolean foundReceiver = false;

        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(receiverUsername)) {
                clientHandler.sendMessage(sender.getUsername() + ": " + message);
                sender.sendMessage(sender.getUsername() + ": " + message);
                foundReceiver = true;
                break; // Выходим из цикла, так как мы уже нашли получателя
            }
        }

        if (!foundReceiver) {
            // Отправляем отправителю сообщение об ошибке
            sender.sendMessage("Пользователь " + receiverUsername + " не найден.");
        }
    }

    //TODO homework 2
    public synchronized void userDisable(ClientHandler sender, String username) {
        if (sender.getUsername().equals(username)) {
            sender.sendMessage("Админ не может сам себя заблокировать!");
            return;
        }
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                userService.setUserDisable(username, false); // Выставили флаг, что пользователь отключен
                clientHandler.sendMessage("Вы заблокированы!");
                sender.sendMessage("Вы заблокировали: " + username);
                // Выкидываем юзера из листа клиентов.
                clients.remove(clientHandler);
                // Отключаем юзера от рассылки
                unsubscribe(clientHandler);
                return; // Выходим из цикла, так как мы уже отключили username
            }
        }

        // Если не нашли пользователя, то отправляем отправителю сообщение об ошибке
        sender.sendMessage("Пользователь " + username + " не найден");
    }
}
