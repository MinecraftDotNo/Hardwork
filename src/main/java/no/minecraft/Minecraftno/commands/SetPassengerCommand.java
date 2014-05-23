package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SetPassengerCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public SetPassengerCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player target = this.plugin.playerMatch(args[0]);
            if (target != null) {
                target.setPassenger(player);
            }
            return true;
        } else {
            return false;
        }
    }
}
