package no.minecraft.hardwork.listeners;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.handlers.UserHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {
    private Hardwork hardwork;

    public PlayerListener(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UserHandler userHandler = this.hardwork.getUserHandler();

        userHandler.clearCachedUser(userHandler.getUser(event.getPlayer().getUniqueId()));
    }
}
