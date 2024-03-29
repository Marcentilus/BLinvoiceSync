import lombok.Getter;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;
@Getter

public class DBUtil {

    private String JDBC_URL;
    private String USER;
    private String PASSWORD;


    public DBUtil(String JDBC_URL, String USER, String PASSWORD) {
        this.JDBC_URL = JDBC_URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
    }

    public static DBUtil configBuilder(Configuration configuration){
        return  new DBUtil(
                configuration.getUrl().trim(),
                configuration.getLogin().trim(),
                configuration.getPassword().trim()
        );
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

}
