package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TpBackCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;

    public TpBackCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (this.userHandler.getTeleportBackLocation(player) != null) {
                player.teleport(this.userHandler.getTeleportBackLocation(player));
            }
        } else {
            return false;
        }
        return true;
    }
}