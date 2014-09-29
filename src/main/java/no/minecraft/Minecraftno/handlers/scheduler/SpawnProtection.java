package no.minecraft.Minecraftno.handlers.scheduler;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import java.util.logging.Level;

public class SpawnProtection {

    private final Minecraftno plugin;

    public SpawnProtection(Minecraftno instance) {
        this.plugin = instance;
    }

    public class Run implements Runnable {
        public void run() {
            if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
                checkEntityPos();
            }
        }
    }

    public void checkEntityPos() {
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity e : world.getEntities()) {
                if (e instanceof Monster) {
                    if (e.getWorld().getSpawnLocation().distance(e.getLocation()) < 20) {
                        e.remove();
                    }
                }
            }
        }
    }

    public void scheduleSpawnProtection() {
        if (this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Run(), 200L, 200L) > 0) {
            Minecraftno.log.log(Level.INFO, "SpawnProtection startet.");
        }
    }
}