package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {

    private static final String URL  = "jdbc:mysql://80.190.74.152:3306/autotech_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "autotech_user";
    private static final String PASS = "AutoTech#2026Taller!";

    private static Connection cn = null;

    public static Connection getConexion() throws SQLException {
        if (cn == null || cn.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                cn = DriverManager.getConnection(URL, USER, PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL no encontrado", e);
            }
        }
        return cn;
    }

    public static void cerrar() {
        try {
            if (cn != null && !cn.isClosed()) {
                cn.close();
            }
        } catch (SQLException ignored) {
        }
    }
}