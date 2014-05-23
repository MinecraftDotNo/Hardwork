package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends MinecraftnoCommand {

    public SetSpawnCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {

        if (args.length == 0) {
            Location newSpawn = player.getLocation();
            player.getWorld().setSpawnLocation((int) newSpawn.getX(), (int) newSpawn.getY(), (int) newSpawn.getZ());
            player.sendMessage(getInfoChatColor() + "Ny spawn ble satt.");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                player.getWorld().setSpawnLocation(0, 127, 0);
                player.sendMessage(getInfoChatColor() + "Spawn ble reset.");
            }
        } else {
            return false;
        }
        return true;
    }
}