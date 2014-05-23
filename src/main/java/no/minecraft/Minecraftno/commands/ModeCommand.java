package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ModeCommand extends MinecraftnoCommand {

    public ModeCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = this.plugin.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                if (player.isOp()) {
                    player.performCommand("inv on");
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(getOkChatColor() + "Game mode: " + getConfirmChatColor() + "Creative");
                } else {
                    player.sendMessage("Kun OP har adgang til denne kommandoen");
                }
            } else if (player.getGameMode() == GameMode.CREATIVE) {
                player.performCommand("inv off");
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(getOkChatColor() + "Game mode: " + getConfirmChatColor() + "Survival");
            }
            return true;
        } else {
            return false;
        }
    }
}
