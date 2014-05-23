package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TpCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public TpCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player victim = this.plugin.getServer().getPlayer(args[0]);
            if (victim != null) {
                if (victim.getWorld() != player.getWorld() && victim.getWorld().getEnvironment() == Environment.NETHER) {
                    player.sendMessage(getErrorChatColor() + " Spiller er i nether. Du må først reise til nether før du kan bruke TP!");
                    return true;
                } else {
                    this.userHandler.setTeleportBackLocation(player, player.getLocation());
                    player.teleport(victim);
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Fant ikke spilleren: " + args[0]);
            }
            return true;
        } else {
            return false;
        }
    }
}