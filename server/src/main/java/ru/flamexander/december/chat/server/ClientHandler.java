package ru.flamexander.december.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                authentication();
                listenUserChatMessages();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    private void listenUserChatMessages() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (!server.getUserService().isUserEnable(username)) {
                // Если пользователь заблокирован, игнорируем его сообщения
                sendMessage("СЕРВЕР: Вы заблокированы и не можете читать и отправлять сообщения в чат");
                continue;
            }
            if (message.startsWith("/")) {
                if (message.equals("/exit")) {
                    break;
                }

                // TODO homework - Приватные сообщения
                if (message.startsWith("/w ")) {
                    String[] parts = message.split(" ", 3);
                    if (parts.length != 3) {
                        sendMessage("СЕРВЕР: Некорректная команда");
                        continue;
                    } else {
                        server.sendPrivateMessage(this, parts[1], parts[2]);
                    }
                }

                // TODO homework - Роли пользователей
                if (message.startsWith("/kick ") && server.getUserService().isUserAdmin(username)) {
                    String[] parts = message.split(" ", 2);
                    if (parts.length != 2) {
                        sendMessage("СЕРВЕР: Некорректная команда");
                    } else {
                        server.userDisable(this, parts[1]);
                    }
                } else {
                    // Здесь повторно выводится сообщение при приватке
                    sendMessage("СЕРВЕР: У вас не достаточно прав для выполнения этой команды");
                }

            } else {
                server.broadcastMessage(username + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean tryAuthenticate(String message) {
        String[] elements = message.split(" "); // /auth login1 pass1
        if (elements.length != 3) {
            sendMessage("СЕРВЕР: Некорректная команда аунтификации");
            return false;
        }
        String login = elements[1];
        String password = elements[2];
        String usernameFromUserService = server.getUserService().getUsernameByLoginAndPassword(login, password);
        // Проверяем, этот пользователь заблокирован или нет
        if (!server.getUserService().isUserEnable(usernameFromUserService)) {
            sendMessage("СЕРВЕР: Вы заблокированы!");
            return false;
        }
        if (usernameFromUserService == null) {
            sendMessage("СЕРВЕР: Пользователя с указанным логином/паролем не существует");
            return false;
        }
        if (server.isUserBusy(usernameFromUserService)) {
            sendMessage("СЕРВЕР: Учетная запись уже занята");
            return false;
        }
        username = usernameFromUserService;
        server.subscribe(this);
        sendMessage("/authok " + username); // Служебное сообщение
        sendMessage("СЕРВЕР: " + username + ", добро пожаловать в чат!");
        return true;
    }

    private boolean register(String message) {
        String[] elements = message.split(" "); // /register login1 pass1 user1
        if (elements.length != 4) {
            sendMessage("СЕРВЕР: Некорректная команда регистрации");
            return false;
        }
        String login = elements[1];
        String password = elements[2];
        String registrationUsername = elements[3];
        if (server.getUserService().isLoginAlreadyExist(login)) {
            sendMessage("СЕРВЕР: Указанный login уже занят!");
            return false;
        }
        if (server.getUserService().isUsernameAlreadyExist(registrationUsername)) {
            sendMessage("СЕРВЕР: Указанное имя пользователя уже занято!");
            return false;
        }
        server.getUserService().createNewUser(login, password, registrationUsername);
        username = registrationUsername;
        sendMessage("/authok " + username); // Служебное сообщение
        sendMessage("СЕРВЕР: " + username + ", вы успешно прошли регистрацию, добро пожаловать в чат!");
        server.subscribe(this);
        return true;
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            boolean isSucceed = false;
            if (message.startsWith("/auth ")) {
                isSucceed = tryAuthenticate(message);
            } else if (message.startsWith("/register ")) {
                isSucceed = register(message);
            } else {
                sendMessage("СЕРВЕР: Для работы требуется авторизация или регистрация");
            }
            if (isSucceed) {
                break;
            }
        }
    }
}
