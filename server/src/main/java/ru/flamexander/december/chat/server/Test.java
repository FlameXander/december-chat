package ru.flamexander.december.chat.server;

import java.sql.*;

public class Test {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/db_hw18";
        String user_name_db = "user_hw18";
        String password_user_db = "1234";
        String login = "admin";
        String password = "adminpass";

//        String sql = "SELECT COUNT(*) " +
//                "FROM public.user_roles ur " +
//                "JOIN public.roles r ON ur.role_id = r.role_id " +
//                "JOIN public.users u ON ur.user_id = u.user_id " +
//                "WHERE u.login = ? AND r.role_name = 'administrator'";
//
//        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//
//            preparedStatement.setString(1, login);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            if (resultSet.next()) {
//                int count = resultSet.getInt(1);
//                return count > 0; // Если количество больше 0, то пользователь является администратором
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return false;

    }
}
