import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;

public class DBUtil {

    private static final String JDBC_URL = "jdbc:sqlserver://10.221.132.148\\SQLEXPRESS;database=Kopia_Leduvel;encrypt=true;trustServerCertificate=true";
    private static final String USER = "raf";
    private static final String PASSWORD = "l3e.%TGB12!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

}
