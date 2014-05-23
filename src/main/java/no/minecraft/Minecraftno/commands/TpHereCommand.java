package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TpHereCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public TpHereCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player victim = plugin.getServer().getPlayer(args[0]);
            if (victim != null) {
                this.userHandler.setTeleportBackLocation(victim, player.getLocation());
                victim.teleport(player);
            } else {
                player.sendMessage(getErrorChatColor() + "Fant ikke spilleren: \"" + args[0] + "\"");
            }
            return true;
        } else {
            return false;
        }
    }
}