package no.minecraft.hardwork.listeners;

import java.sql.SQLException;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.handlers.UserHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {
    private Hardwork hardwork;

    public PlayerListener(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UserHandler userHandler = this.hardwork.getUserHandler();

        userHandler.clearCachedUser(userHandler.getUser(event.getPlayer().getUniqueId()));
        
        
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        // Fill missing UUID in login event.
        // TODO: Add namechanging in no.hardwork and call it here after method under.
        try {
            this.hardwork.getUserHandler().updateMissingUUID(event.getPlayer());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
