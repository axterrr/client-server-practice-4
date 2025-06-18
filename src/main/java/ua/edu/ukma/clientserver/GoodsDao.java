package ua.edu.ukma.clientserver;

import java.sql.Connection;
import java.sql.SQLException;

public class GoodsDao implements AutoCloseable {

    private final Connection connection;

    public GoodsDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
