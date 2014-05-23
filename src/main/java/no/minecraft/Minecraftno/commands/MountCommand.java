package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class MountCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public MountCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player target = this.plugin.playerMatch(args[0]);
            if (target != null) {
                player.performCommand("inv on");
                target.setPassenger(player);
            }
        } else if (args.length == 0) {
            player.eject();
        } else {
            return false;
        }
        return true;
    }
}
