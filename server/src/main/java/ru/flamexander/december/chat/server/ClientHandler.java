package ru.flamexander.december.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import static java.sql.DriverManager.getConnection;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;
    private String pass;
    private UserRole role;
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String SELECT_USER_SQL = "SELECT u.login, u.password from Users u";
   private static final String SELECT_ROLES_FOR_USER = "SELECT r.id, r.name from usertorole left join roles r on r.id = UserToRole.role_id where user_id = ?";


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
            if (message.startsWith("/")) {
                if (message.equals("/exit")) {
                    sendMessage("/exit confirmed");
                    break;
                } else if (message.startsWith("/kick ")) {
                    kickUser(message);
                } else {
                    server.broadcastMessage(message);
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

    public void kickUser(String message) {
        String[] elements = message.split(" ", 2);
        if (elements.length != 2) {
            sendMessage("СЕРВЕР: некорректная команда");
        } else if (role != UserRole.ADMIN) {
            sendMessage("СЕРВЕР: у Вас нет прав отключать пользователей");
        } else {
            String nameToKick = elements[1];
            ClientHandler clientToKick = server.getClientHandlerByUsername(nameToKick);
            if (clientToKick != null) {
                clientToKick.sendMessage("СЕРВЕР: Вы были отключены администратором.");
                clientToKick.disconnect();
            } else {
                sendMessage("СЕРВЕР: Пользователь с именем '" + nameToKick + "' не найден.");
            }
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

    private boolean tryToAuthenticate(String message) {
        String[] elements = message.split(" "); // /auth login1 pass1
        if (elements.length != 3) {
            sendMessage("СЕРВЕР: некорректная команда аутентификации");
            return false;
        }
        String login = elements[1];
        String password = elements[2];

        try (Connection connection = getConnection(DATABASE_URL, "postgres", "pass")) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(SELECT_USER_SQL)) {
                    if (resultSet.next()) {
                        username = resultSet.getString(1);
                        pass = resultSet.getString(2);
                        if (login.equals(username) && password.equals(pass)){
                        server.subscribe(this);
                        Set<Role> roles =  getUserRoleByUsername(username);
                            assert roles != null;
                            for (Role role1 : roles) {
                                System.out.println(role1.getName());
                            }
                            sendMessage("/authok " + username);
                        sendMessage("СЕРВЕР: " + username + ", добро пожаловать в чат!");
                        return true;
                        }
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sendMessage("СЕРВЕР: пользователя с указанным логин/паролем не существует");
                return false;
            }


    private  Set<Role> getUserRoleByUsername (String username){
        try (Connection connection = getConnection(DATABASE_URL, "postgres", "pass")){
              try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLES_FOR_USER)) {
                  preparedStatement.setString(1, username);
                  try(ResultSet rs = preparedStatement.executeQuery()) {
                      Set<Role> roles = new HashSet<>();
                      while (rs.next()) {
                          Integer id = rs.getInt(1);
                          String name = rs.getString(2);
                          Role role = new Role(id, name);
                          roles.add(role);
                      }
                     return roles;
                  }

          }
    } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean register(String message) {
        String[] elements = message.split(" "); // /auth login1 pass1 user1
        if (elements.length != 5) {
            sendMessage("СЕРВЕР: некорректная команда аутентификации");
            return false;
        }
        String login = elements[1];
        String password = elements[2];
        String registrationUsername = elements[3];
        UserRole userRole = UserRole.valueOf(elements[4]);

        if (server.getUserService().isLoginAlreadyExist(login)) {
            sendMessage("СЕРВЕР: указанный login уже занят");
            return false;
        }
        if (server.getUserService().isUsernameAlreadyExist(registrationUsername)) {
            sendMessage("СЕРВЕР: указанное имя пользователя уже занято");
            return false;
        }
        server.getUserService().createNewUser(login, password, registrationUsername, userRole);
        username = registrationUsername;
        role = userRole;
        sendMessage("/authok " + username);
        sendMessage("СЕРВЕР: " + username + ", вы успешно прошли регистрацию, добро пожаловать в чат!");
        server.subscribe(this);
        return true;
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            boolean isSucceed = false;
            if (message.startsWith("/auth ")) {
                isSucceed = tryToAuthenticate(message);
            } else if (message.startsWith("/register ")) {
                isSucceed = register(message);
            } else {
                sendMessage("СЕРВЕР: требуется войти в учетную запись или зарегистрироваться");
            }
            if (isSucceed) {
                break;
            }
        }
    }
}