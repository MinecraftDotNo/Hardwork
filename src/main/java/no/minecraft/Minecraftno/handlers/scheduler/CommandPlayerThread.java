package no.minecraft.Minecraftno.handlers.scheduler;

import org.bukkit.entity.Player;

public class CommandPlayerThread extends Thread {
    private Player player;
    private String command;

    public CommandPlayerThread(Player player, String command) {
        this.player = player;
        this.command = command;
    }

    public void run() {
        player.performCommand(command);
    }
}
