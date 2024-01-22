package ru.flamexander.december.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private UserService userService;
    private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/december_chat";
    private Connection connection;
    private Statement statement;

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
        // TODO homework
        for (ClientHandler receiver : clients) {
            sender.sendMessage("whisper to " + receiverUsername + ": " + message);
            receiver.sendMessage("whisper from " + sender.getUsername() + ": " + message);
            return;
        }
    }

    public synchronized ClientHandler kickUser(ClientHandler user, String username) {
        if (user.isAdmin()) {
            for (ClientHandler c : clients) {
                if (c.getUsername().equals(username)) {
                    return c;
                }
            }
            user.sendMessage("user not found");
            return null;
        }
        user.sendMessage("admin command, permission denied");
        return null;
    }

    public void closeDbConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean dbConnection() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, "postgres", "postgres");
            statement = connection.createStatement();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dbRegistration(String login, String password, String registrationUsername) {
        String insertQuery = "insert into users (user_login, user_password, user_username, user_isAdmin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, registrationUsername);
            preparedStatement.setBoolean(4, false);
            preparedStatement.executeUpdate();
            closeDbConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public InMemoryUserService.User dbAuthorization(String login, String password) {
        String selectQuery = "select user_login, user_username, user_password, user_isAdmin from users where user_login=? and user_password=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String dbLogin = resultSet.getString("user_login");
                    String dbUsername = resultSet.getString("user_username");
                    String dbPassword = resultSet.getString("user_password");
                    boolean dbUserIsAdmin = resultSet.getBoolean("user_isAdmin");
                    closeDbConnection();
                    return new InMemoryUserService.User(dbLogin, dbPassword, dbUsername, dbUserIsAdmin);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean isLoginAlreadyExists(String login) {
        String selectQuery = "select exists(select 1 from users where user_login=?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, login);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("exists");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsernameAlreadyExists(String username) {
        String selectQuery = "select exists(select 1 from users where user_username=?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("exists");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
