package no.minecraft.Minecraftno.handlers.player;

import no.minecraft.Minecraftno.Minecraftno;

public class MessageTask implements Runnable {
    private int playerID;
    private String message;
    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public MessageTask(int playerID, String message, Minecraftno instance) {
        super();
        this.playerID = playerID;
        this.message = message;
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public void run() {
        plugin.getServer().getPlayer(this.userHandler.getNameFromId(playerID)).sendMessage(message);
    }
}
