package com.tallcraft.tallcraftspawn;


import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public final class TallcraftSpawn extends JavaPlugin implements Listener {

    private final Logger logger = Logger.getLogger(this.getName());
    private FileConfiguration config;

    @Override
    public void onEnable() {
        initConfig();
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

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        if (e.isBedSpawn() && !config.getBoolean("overrideBedSpawn")) {
            return;
        }
        World world = e.getRespawnLocation().getWorld();
        e.setRespawnLocation(getSpawnLocation(world));
    }


    private void toSpawn(Player player) {
        World world = player.getWorld();
        Location location = getSpawnLocation(world);
        logger.info(location.toString());
        player.teleport(location);
    }

    private void setSpawn(Location location) {
        // Also set vanilla world spawn
        location.getWorld().setSpawnLocation(location);

        logger.info(location.toString());

        String path = "spawn.worlds." + location.getWorld().getName();
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
        saveConfig();
    }

    private Location getSpawnLocation(World world) {
        String path = "spawn.worlds." + world.getName();
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    private void initConfig() {
        config = this.getConfig();

        List<World> worlds = Bukkit.getWorlds();

        MemoryConfiguration defaultConfig = new MemoryConfiguration();

        defaultConfig.set("overrideBedSpawn", false);

        for (World world : worlds) {
            Location defaultLocation = world.getSpawnLocation();

            String path = "spawn.worlds." + world.getName();
            defaultConfig.set(path + ".x", defaultLocation.getX());
            defaultConfig.set(path + ".y", defaultLocation.getY());
            defaultConfig.set(path + ".z", defaultLocation.getZ());
            defaultConfig.set(path + ".yaw", 0f);
            defaultConfig.set(path + ".pitch", 0f);
        }

        config.setDefaults(defaultConfig);
        config.options().copyDefaults(true);
        saveConfig();
    }
}