package pl.barthvisuals.whitelist.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.barthvisuals.whitelist.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

public class PlayerJoinListener implements Listener {
    Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM " + plugin.table + " WHERE Nick='" + e.getPlayer().getName() + "'");
            ResultSet results = statement.executeQuery();
            if (!(results.next())) {
                e.getPlayer().kickPlayer(plugin.getConfig().getString("wl-kick-message").replace("&", "§"));
            }
        } catch (SQLSyntaxErrorException see) {
            try {
                PreparedStatement statement = plugin.getConnection()
                        .prepareStatement("CREATE TABLE `" + plugin.table + "` (`Nick` text NOT NULL, `Admin` text NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                int results = statement.executeUpdate();
                PreparedStatement statement2 = plugin.getConnection()
                        .prepareStatement("SELECT * FROM " + plugin.table + " WHERE Nick='" + e.getPlayer().getName() + "'");
                ResultSet results2 = statement.executeQuery();
                if (!(results2.next())) {
                    e.getPlayer().kickPlayer(plugin.getConfig().getString("wl-kick-message").replace("&", "§"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
