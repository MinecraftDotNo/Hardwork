package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MinecraftnoWeatherListener implements Listener {

    private final Minecraftno plugin;

    public MinecraftnoWeatherListener(Minecraftno instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getWorld());
        if (!wcfg.Weather) {
            event.setCancelled(true);
        }
    }
        
        /*
        @EventHandler(priority=EventPriority.NORMAL)
        public void onThunderChange(ThunderChangeEvent event) {

        }
        
        @EventHandler(priority=EventPriority.NORMAL)
        public void onLightningStrike(LightningStrikeEvent event) {
         
        }*/
}