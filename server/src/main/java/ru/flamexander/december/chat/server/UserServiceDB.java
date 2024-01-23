package ru.flamexander.december.chat.server;

import java.sql.*;


public class UserServiceDB implements UserService {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASS = "pass";
    private static final String SELECT_ROLES_FOR_USER = "SELECT r.name FROM roles r " +
            "JOIN user_role ur on r.id = ur.role_id " +
            "JOIN users u on ur.user_id = u.login " + "WHERE u.username ?";

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        try(Connection connection = DriverManager.getConnection(DATABASE_URL, USER,PASS)) {
            String queryUserPass = "SELECT username FROM users WHERE login = ? AND password = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(queryUserPass)) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                    return resultSet.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        try(Connection connection = DriverManager.getConnection(DATABASE_URL, USER,PASS)){
            connection.setAutoCommit(false);

            String insertUserQuery = "INSERT INTO users (login, password, username) VALUES (?, ?, ?)";
            try (PreparedStatement insertUser = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertUser.setString(1, login);
                insertUser.setString(2, password);
                insertUser.setString(3, username);
                insertUser.executeUpdate();

                try (ResultSet genKeys = insertUser.getGeneratedKeys()){
                    if (genKeys.next()) {
                        int userID = genKeys.getInt(1);

                        String insertUserRoleQuery = "INSERT INTO usertorole (user_id, role_id) VALUES (?," +
                        "SELECT role_id FROM roles WHERE name = 'USER'))";
                        try (PreparedStatement insertUserRole = connection.prepareStatement(insertUserRoleQuery)){
                            insertUserRole.setInt(1, userID);
                            insertUserRole.executeUpdate();
                        }
                        connection.commit();
                    } else {
                        connection.rollback();
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER,PASS)){
            String query = "SELECT COUNT(*) AS count FROM users WHERE login = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, login);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count") > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER,PASS)){
            String query = "SELECT COUNT(*) AS count FROM users WHERE username=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count") > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getRoleByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER,PASS)){
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLES_FOR_USER)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
