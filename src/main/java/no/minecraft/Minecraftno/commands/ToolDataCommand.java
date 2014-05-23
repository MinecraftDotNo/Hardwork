package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ToolDataCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;

    public ToolDataCommand(Minecraftno instance) {
        super(instance);
        this.userHandler = instance.getUserHandler();
        setAccessLevel(3);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (this.userHandler.getUserId(args[0]) == -1) {
                player.sendMessage(getErrorChatColor() + "Brukeren eksisterer ikke!");
            } else {
                this.userHandler.setToolData(player, args[0]);
                player.sendMessage(getDefaultChatColor() + "Satte verktøy-data til: " + args[0]);
            }

            return true;
        } else {
            return false;
        }
    }
}
