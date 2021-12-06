package br.com.alura.ecommerce.database;

import java.sql.*;

public class LocalDatabase {
    private static final String JDBC_CONNECTION_PART = "jdbc:sqlite:video5.3/ecommerce";
    private final Connection connection;

    public LocalDatabase(String folderForDatabase, String databaseName) throws SQLException {
        this.connection = DriverManager.getConnection(JDBC_CONNECTION_PART + "/" + folderForDatabase + "/" + databaseName + ".db");
    }

    public boolean executeStatement(String statement) throws SQLException {
        return connection.createStatement().execute(statement);
    }

    public boolean save(String sql, String... params) throws SQLException {
        return getPreparedStatement(sql, params).execute();
    }

    public ResultSet query(String sql, String... params) throws SQLException {
        return getPreparedStatement(sql, params).executeQuery();
    }

    private PreparedStatement getPreparedStatement(String sql, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(sql);

        for(int i = 0; i < params.length; i++ ){
            preparedStatement.setString(i + 1, params[ i ]);
        }
        return preparedStatement;
    }
}
