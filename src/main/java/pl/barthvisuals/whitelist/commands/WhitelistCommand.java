package pl.barthvisuals.whitelist.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.barthvisuals.whitelist.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

public class WhitelistCommand implements CommandExecutor {
    Main plugin;
    public WhitelistCommand(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("whitelist.all")){
            if (args.length > 1) {
                switch (args[0]) {
                    case "dodaj":
                    case "add":{
                        try{
                            PreparedStatement statement = plugin.getConnection()
                                    .prepareStatement("INSERT INTO " + plugin.table + " (Nick, Admin) VALUES ('" + args[1] +"', '" + sender.getName() + "')");
                            int results = statement.executeUpdate();
                            for(Player player : Bukkit.getOnlinePlayers()){
                                if(player.hasPermission("whitelist.view") || player.isOp()){
                                    player.sendMessage(plugin.getConfig().getString("wl-add-log-message").replace("&", "§").replace("{PLAYER}", args[1]).replace("{ADMIN}", sender.getName()));
                                }
                            }
                        }
                        catch (SQLSyntaxErrorException see){
                            try {
                                PreparedStatement statement = plugin.getConnection()
                                        .prepareStatement("CREATE TABLE `" + plugin.table +"` (`Nick` text NOT NULL, `Admin` text NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                                int results = statement.executeUpdate();
                                PreparedStatement statement2 = plugin.getConnection()
                                        .prepareStatement("INSERT INTO " + plugin.table + " (Nick, Admin) VALUES ('" + args[1] +"', '" + sender.getName() + "')");
                                int results2 = statement2.executeUpdate();
                                sender.sendMessage("§cNie znaleziono tabeli: " + plugin.table + " więc została ona utworzona!");
                                sender.sendMessage(plugin.getConfig().getString("wl-command-add").replace("&", "§").replace("&", "§").replace("{PLAYER}", args[1]).replace("{ADMIN}", sender.getName()));
                                plugin.getLogger().info(sender.getName() + " dodał do whitelisty gracza: " + args[1]);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                        }
                        catch (SQLException es){
                            es.printStackTrace();
                            break;
                        }
                        sender.sendMessage(plugin.getConfig().getString("wl-command-add").replace("&", "§").replace("&", "§").replace("{PLAYER}", args[1]).replace("{ADMIN}", sender.getName()));
                        plugin.getLogger().info(sender.getName() + " dodał do whitelisty gracza: " + args[1]);
                        break;
                    }
                    case "usun":
                    case "remove":{
                        try{
                            PreparedStatement statement = plugin.getConnection()
                                    .prepareStatement("DELETE FROM " + plugin.table + " WHERE Nick='" + args[1] + "'");
                            int results = statement.executeUpdate();
                            for(Player player : Bukkit.getOnlinePlayers()){
                                if(player.hasPermission("whitelist.view") || player.isOp()){
                                    player.sendMessage(plugin.getConfig().getString("wl-remove-log-message").replace("&", "§").replace("&", "§").replace("{PLAYER}", args[1]).replace("{ADMIN}", sender.getName()));
                                }
                            }
                        }
                        catch (SQLSyntaxErrorException see){
                            try {
                                PreparedStatement statement = plugin.getConnection()
                                        .prepareStatement("CREATE TABLE `" + plugin.table +"` (`Nick` text NOT NULL, `Admin` text NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                                int results = statement.executeUpdate();
                                sender.sendMessage("§cNie znaleziono tabeli: " + plugin.table + " więc została ona utworzona!");
                                sender.sendMessage("§cUżyj komendy ponownie.");
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                        }
                        catch (SQLException es){
                            es.printStackTrace();
                            break;
                        }
                        sender.sendMessage(plugin.getConfig().getString("wl-command-remove").replace("&", "§").replace("&", "§").replace("{PLAYER}", args[1]).replace("{ADMIN}", sender.getName()));
                        plugin.getLogger().info(sender.getName() + " usunął z whitelisty gracza: " + args[1]);
                        break;
                    }
                    default:{
                        sender.sendMessage(plugin.getConfig().getString("wl-command-error").replace("&", "§").replace("&", "§").replace("{PLAYER}", args[1]).replace("{ADMIN}", sender.getName()));
                        break;
                    }
                }
            }
            else
            {
                sender.sendMessage(plugin.getConfig().getString("wl-command-error").replace("&", "§"));
            }
        }
        else
        {
            sender.sendMessage(plugin.getConfig().getString("wl-command-nopermissions").replace("&", "§"));
        }
        return false;
    }
}
