package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SetHomeCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;

    public SetHomeCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(2);
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.userHandler.getOnlineUsers().get(player).setHome(player.getLocation());
            player.sendMessage(getDefaultChatColor() + "Nytt home satt.");
            return true;
        } else {
            return false;
        }
    }
}