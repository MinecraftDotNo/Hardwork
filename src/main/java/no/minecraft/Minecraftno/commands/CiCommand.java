package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CiCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;

    public CiCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.getInventory().clear();
            player.sendMessage(getOkChatColor() + " Poof, inventory er nå borte i all evighet!");
            return true;
        } else if (args.length == 1) {
            Player victim = this.plugin.playerMatch(args[0]);
            if (victim != null) {
                victim.getInventory().clear();
                victim.sendMessage(getOkChatColor() + " Poof, der slettet " + player.getDisplayName() + getOkChatColor() + " inventory din!");
                player.sendMessage(getOkChatColor() + " Du slettet inventorien til " + victim.getName() + " din!");
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}