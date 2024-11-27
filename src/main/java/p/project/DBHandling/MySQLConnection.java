package p.project.DBHandling;

import java.sql.Connection;        // Import for Connection class
import java.sql.DriverManager;    // Import for DriverManager class
import java.sql.PreparedStatement; // Import for PreparedStatement class
import java.sql.ResultSet;         // Import for ResultSet class
import java.sql.SQLException;      // Import for SQLException class
import java.sql.Statement;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/db1";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // Connect to the database
    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database.");
        }
    }

    // Execute a query that returns a ResultSet (for SELECT statements)
    public static ResultSet executeQuery(String query) {
        try {
            Connection connection = connect();
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute query.");
        }
    }

    // Execute a prepared query that returns a ResultSet
    public static ResultSet executePreparedQuery(String query, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute prepared query.");
        } finally {
            // Properly close resources in the requester method
            // ResultSet should be closed by the caller
            // Connection and PreparedStatement will be managed by the caller
        }
    }

    // Execute an update (INSERT, UPDATE, DELETE statements)
    public static int executeUpdate(String query, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute update query.");
        } finally {
            // Closing resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}