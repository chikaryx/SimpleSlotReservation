package net.claimworld.simpleslotreservation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static org.bukkit.Bukkit.*;

public final class Simpleslotreservation extends JavaPlugin implements Listener, CommandExecutor {

    private int reservedSlots;
    private String message;

    private void updateData(CommandSender sender) {
        getScheduler().runTaskAsynchronously(this, () -> {
            reloadConfig();
            FileConfiguration config = getConfig();

            reservedSlots = config.getInt("settings.reserved-slots");
            message = config.getString("settings.message");

            getLogger().log(Level.INFO, "Data has been updated to values: [reserved-slots: " + reservedSlots + "], [message: " + message + "].");
            if (sender instanceof Player) sender.sendMessage("Data bas been successfully reloaded. Check console for details.");
        });
    }

    @EventHandler
    public void loginEvent(PlayerLoginEvent event) {
        int maxPlayers = getServer().getMaxPlayers();
        if (reservedSlots > maxPlayers) return;
        if (getOnlinePlayers().size() < maxPlayers - reservedSlots) return;

        Player player = event.getPlayer();
        if (player.isOp()) return;
        if (player.hasPermission("claimworld.simpleslotreservation.vip")) return;

        player.kickPlayer(message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        updateData(sender);
        return true;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("reload-simpleslotreservation").setExecutor(this);
        getPluginManager().registerEvents(this, this);

        updateData(getConsoleSender());
    }
}
