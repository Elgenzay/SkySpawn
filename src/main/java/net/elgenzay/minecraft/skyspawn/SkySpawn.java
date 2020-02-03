package net.elgenzay.minecraft.skyspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SkySpawn extends JavaPlugin implements Listener {

    private FileConfiguration config = this.getConfig();
    private double radius = 1000;
    private int maxTriesForLand = 20;

    @Override
    public void onEnable() {
        config.addDefault("radius", radius);
        config.addDefault("maxTriesForLand", maxTriesForLand);
        config.options().copyDefaults(true);
        saveConfig();
        radius = config.getDouble("radius");
        maxTriesForLand = config.getInt("maxTriesForLand");
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("SkySpawn loaded");
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (!event.isBedSpawn()) {
            Player player = event.getPlayer();
            World respawnWorld = event.getRespawnLocation().getWorld();
            Location respawnLocation = createSpawnLocation(respawnWorld);

            event.setRespawnLocation(respawnLocation);
            giveSlowFall(player, 400);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if(!event.getPlayer().hasPlayedBefore()) {
            Player player = event.getPlayer();
            Location spawnLocation = createSpawnLocation(player.getWorld());

            player.teleport(spawnLocation);
            giveSlowFall(player,500);
        }
    }

    private Location createSpawnLocation(World world){
        int tries = 0;
        Location respawnLocation;
        do {
            respawnLocation = rollLocation(world);
            tries++;
        } while (respawnLocation.getBlock().getBiome().name().contains("OCEAN") && tries < maxTriesForLand);
        return respawnLocation;
    }

    private Location rollLocation(World world){
        double a = Math.random() * 2 * Math.PI;
        double r = radius * Math.sqrt(Math.random());
        double x = r * Math.cos(a);
        double z = r * Math.sin(a);
        return new Location(world, x, 256, z);
    }

    private void giveSlowFall(Player player, int duration){
        Bukkit.getScheduler().scheduleSyncDelayedTask(this,
                () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, 0)), 1);
    }
}