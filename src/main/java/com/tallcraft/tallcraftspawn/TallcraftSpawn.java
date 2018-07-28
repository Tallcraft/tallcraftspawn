package com.tallcraft.tallcraftspawn;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public final class TallcraftSpawn extends JavaPlugin implements Listener {

    private final Logger logger = Logger.getLogger(this.getName());

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console commands not supported");
            return true;
        }
        Player player = (Player) sender;

        if (cmd.getName().equals("spawn")) {
            if (!player.hasPermission("tallcraftspawn.spawn")) {
                player.sendMessage(cmd.getPermissionMessage());
                return true;
            }
            player.sendMessage("Teleporting");
            toSpawn(player);
            return true;
        }

        if (cmd.getName().equals("setspawn")) {
            if (!player.hasPermission("tallcraftspawn.setspawn")) {
                player.sendMessage(cmd.getPermissionMessage());
                return true;
            }
            setSpawn(player.getLocation());
            logger.info("Set spawn location " + player.getLocation().toString());
            player.sendMessage("Set spawn");
            player.sendMessage(player.getLocation().toString());
            return true;
        }

        return false;
    }

    private void toSpawn(Player player) {
        player.teleport(player.getWorld().getSpawnLocation());
    }

    private void setSpawn(Location location) {
        location.getWorld().setSpawnLocation(location);
    }
}