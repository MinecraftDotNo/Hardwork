package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class StuckCommand extends MinecraftnoCommand {

    public StuckCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!this.userHandler.isRegPlayer(player)) {
                if (player.getWorld().getEnvironment() != World.Environment.NETHER) {
                    Location loc = player.getWorld().getHighestBlockAt(player.getLocation()).getLocation();
                    loc.setPitch(player.getLocation().getPitch());
                    loc.setYaw(player.getLocation().getYaw());
                    if (loc.getBlockY() > player.getLocation().getBlockY()) {
                        player.teleport(loc);
                    } else {
                        player.sendMessage(getErrorChatColor() + "Det er ingen block over deg.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Du kan ikke bruke denne kommandoen i nether!");
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
