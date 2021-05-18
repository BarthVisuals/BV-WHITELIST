package pl.barthvisuals.whitelist;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.barthvisuals.whitelist.commands.WhitelistCommand;
import pl.barthvisuals.whitelist.listeners.PlayerJoinListener;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Main extends JavaPlugin {
    private Connection connection;
    public String host, database, username, password, table;
    public int port;

    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    Date date = new Date();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Ładowanie eventów...");
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getLogger().info("Ładowanie komend...");
        getCommand("whitelist").setExecutor(new WhitelistCommand(this));
        mysqlSetup();
    }

    public void mysqlSetup() {
        host = getConfig().getString("db_host");
        port = getConfig().getInt("db_port");
        database = getConfig().getString("db_name");
        username = getConfig().getString("db_user");
        table = getConfig().getString("db_table");
        password = getConfig().getString("db_password");

        try {
            getLogger().info("Próba połączenia z MYSQL...");
            synchronized (this) {
                if (getConnection() != null && !(getConnection().isClosed())) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", username, password));

                getLogger().info("Połączono!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            getLogger().info(" ");
            getLogger().info("Nie można połączyć z MYSQL!");
            getLogger().info("Wyłączanie pluginu...");
            getLogger().info(" ");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection){
        this.connection = connection;
    }
}