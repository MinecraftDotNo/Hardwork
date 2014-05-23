package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MinecraftnoServerListener implements Listener {

    private final Minecraftno plugin;

    public MinecraftnoServerListener(Minecraftno instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onServerListPing(ServerListPingEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        int OnlinePlayers = this.plugin.getServer().getOnlinePlayers().length - 1;

        if (OnlinePlayers > cfg.maxplayers) {
            event.setMaxPlayers(OnlinePlayers);
        } else {
            event.setMaxPlayers(cfg.maxplayers);
        }
    }
}