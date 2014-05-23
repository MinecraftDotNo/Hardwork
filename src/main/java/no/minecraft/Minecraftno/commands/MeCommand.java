package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class MeCommand extends MinecraftnoCommand {

    public MeCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
    }

    /**
     * me remove..
     */
    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {

        return true;
    }
}
