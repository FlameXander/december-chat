package ru.flamexander.december.chat.server;

import java.sql.*;

public class JdbcUserService implements UserService {
    public JdbcUserService() {
        try {
            getDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection cn;
    private PreparedStatement prSt;
    private ResultSet rs;
    private Statement st;

    private void getDBConnection() throws SQLException {
        cn = DriverManager.getConnection(Config.DB_URL, Config.BD_LOGIN, Config.BD_PASS);
    }

    private void getPreparedStatement(String query, String[] creds) {
        try {
            prSt = cn.prepareStatement(query);
            for (int i = 1; i <= creds.length; i++) {
                prSt.setString(i, creds[i - 1]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getStatement() {
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        getPreparedStatement(Queries.USERNAME_BY_LOGIN_AND_PASSWORD.getValue(), new String[]{login, password});
        try {
            rs = prSt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        getPreparedStatement(Queries.INSERT_NEW_USER.getValue(), new String[]{login, password, username});
        try {
            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getStatement();
        try {
            st.executeUpdate(Queries.INSERT_NEW_USER_ROLE.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        getPreparedStatement(Queries.SELECT_LOGIN_BY_VALUE.getValue(), new String[]{login});
        try {
            rs = prSt.executeQuery();
            if (rs.next() && rs.getString(1).equals(login)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        getPreparedStatement(Queries.SELECT_USERNAME_BY_VALUE.getValue(), new String[]{username});
        try {
            rs = prSt.executeQuery();
            if (rs.next() && rs.getString(1).equals(username)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setRole(String role, String registrationUsername) {
        getPreparedStatement(Queries.INSERT_ROLE_BY_USER.getValue(), new String[]{"ADMIN", registrationUsername});
        try {
            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isUserAdmin(String username) throws SQLException {
        getPreparedStatement(Queries.SELECT_ROLE_BY_USER.getValue(), new String[]{username});
        rs = prSt.executeQuery();
        while (rs.next()) {
            if (rs.getString(1).equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void disconnect() {
        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (prSt != null) {
                prSt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (cn != null) {
                cn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
