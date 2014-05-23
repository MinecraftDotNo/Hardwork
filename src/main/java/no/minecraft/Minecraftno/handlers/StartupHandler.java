package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import org.bukkit.World;

public class StartupHandler {

    private final Minecraftno plugin;

    public StartupHandler(Minecraftno instance) {
        this.plugin = instance;
    }

    public void checkSpace() {
        Minecraftno.log.info("Free disk space for server: " + Util.byteCountToDisplaySize(this.plugin.getDataFolder().getUsableSpace()) + "/" + Util.byteCountToDisplaySize(this.plugin.getDataFolder().getTotalSpace()));

        for (World world : this.plugin.getServer().getWorlds()) {
            Minecraftno.log.info("Free disk space for world \"" + world.getName() + "\" (" + world.getWorldFolder().getAbsolutePath() + "):" + Util.byteCountToDisplaySize(world.getWorldFolder().getUsableSpace()) + "/" + Util.byteCountToDisplaySize(world.getWorldFolder().getTotalSpace()));
        }
    }

    public void weather() {
        for (World world : this.plugin.getServer().getWorlds()) {
            ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
            ConfigurationWorld wcfg = cfg.get(world);
            if (!wcfg.Thunder) {
                world.setStorm(false);
                world.setThundering(false);
            }
            if (!wcfg.Weather) {
                world.setWeatherDuration(0);
            }
        }
    }
}
