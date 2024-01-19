package ru.flamexander.december.chat.server;

import java.sql.*;
import java.util.List;

/**
 * Класс для работы с БД Postgres
 */
public class InJdbcUserService implements UserService {
    private String jdbcUrl = "jdbc:postgresql://localhost:5432/db_hw18";
    private String user_name_db = "user_hw18";
    private String password_user_db = "1234";

    @Override
    public void setUserDisable(String username, boolean disable) {
        String sql = "UPDATE public.users SET user_enabled = ? WHERE user_name = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBoolean(1, disable);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isUserEnable(String username) {
        String sql = "SELECT user_enabled FROM public.users WHERE user_name = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBoolean("user_enabled");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Ищем, есть такой пользователь или нет
    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        String sql = "SELECT user_name FROM public.users WHERE login = ? AND user_password = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("user_name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        String createUserSql = "INSERT INTO public.users (login, user_password, user_name, user_enabled) VALUES (?, ?, ?, true)";
        String getUserSql = "SELECT user_id FROM public.users WHERE login = ?";
        String insertUserRoleSql = "INSERT INTO public.user_roles (user_id, role_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
             PreparedStatement createUserStatement = connection.prepareStatement(createUserSql);
             PreparedStatement getUserStatement = connection.prepareStatement(getUserSql);
             PreparedStatement insertUserRoleStatement = connection.prepareStatement(insertUserRoleSql)) {

            // Вставка данных в таблицу public.users
            createUserStatement.setString(1, login);
            createUserStatement.setString(2, password);
            createUserStatement.setString(3, username);
            createUserStatement.executeUpdate();

            // Получение user_id из только что вставленной записи
            getUserStatement.setString(1, login);
            ResultSet userResultSet = getUserStatement.executeQuery();

            if (userResultSet.next()) {
                int userId = userResultSet.getInt("user_id");

                // Вставка данных в таблицу public.user_roles
                insertUserRoleStatement.setInt(1, userId);

                // Предположим, что у вас есть роль 'user' и ее id равно, например, 1
                int roleIdForUser = 3;
                insertUserRoleStatement.setInt(2, roleIdForUser);

                insertUserRoleStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean isLoginAlreadyExist(String login) {
        String sql = "SELECT COUNT(*) FROM public.users WHERE login = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Если количество больше 0, то логин уже существует
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        String sql = "SELECT COUNT(*) FROM public.users WHERE user_name = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Если количество больше 0, то имя пользователя уже существует
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean isUserAdmin(String login) {
        String sql = "SELECT COUNT(*) " +
                "FROM public.user_roles ur " +
                "JOIN public.roles r ON ur.role_id = r.role_id " +
                "JOIN public.users u ON ur.user_id = u.user_id " +
                "WHERE u.user_name = ? AND r.role_name = 'administrator'";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user_name_db, password_user_db);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Если количество больше 0, то пользователь является администратором
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
