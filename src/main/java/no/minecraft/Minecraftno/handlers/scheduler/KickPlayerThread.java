package no.minecraft.Minecraftno.handlers.scheduler;

import org.bukkit.entity.Player;

public class KickPlayerThread extends Thread {
    private Player player;
    private String reason;

    public KickPlayerThread(Player player, String reason) {
        this.player = player;
        this.reason = reason;
    }

    public void run() {
        player.kickPlayer(reason);
    }
}
