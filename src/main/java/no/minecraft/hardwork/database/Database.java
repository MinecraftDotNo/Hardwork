package no.minecraft.hardwork.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class Database {
    private final JavaPlugin plugin;

    private final DataConsumer consumer;

    private final String hostname;
    private final int port;
    private final String username;
    private final String password;

    private Connection connection;

    private PreparedStatement testSelect;

    public Database(JavaPlugin plugin, DataConsumer consumer, String hostname, int port, String username, String password) {
        this.plugin = plugin;

        this.consumer = consumer;

        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() {
        if (this.connection != null) {
            try {
                if (this.connection.isClosed())
                    throw new SQLException("An existing connection was closed.");

                ResultSet result = this.testSelect.executeQuery();
                if (result.next())
                    return this.connection;
            } catch (SQLException ignored) { }

            // It wasn't null, but we couldn't use it. Be gone, evildoer!
            this.connection = null;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                "jdbc:mysql://" + this.hostname + ":" + this.port,
                this.username,
                this.password
            );

            this.testSelect = this.connection.prepareStatement("SELECT 1");

            if (this.consumer != null)
                this.consumer.prepareStatements();
        } catch (ClassNotFoundException exception) {
            this.connection = null;
            this.plugin.getLogger().warning("ClassNotFoundException while connecting to database!");
        } catch (SQLException exception) {
            this.connection = null;
            this.plugin.getLogger().warning("SQLException while connecting to database!");
            exception.printStackTrace();
        }

        return this.connection;
    }
}
