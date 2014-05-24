package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class GotoCommand extends MinecraftnoCommand {

    public GotoCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 3) {
            double x = 0, y = 0, z = 0;
            try {
                ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
                ConfigurationWorld wcfg = cfg.get(player.getWorld());
                if (wcfg.restrictWorldXYZ) {
                    Location l = player.getLocation();
                    
                    x = clampValue(Double.parseDouble(args[0]), wcfg.restrictWorldXminus, wcfg.restrictWorldXpluss);
                    //Pass på at brukeren ikke teleporterer over eller under verden.
                    y = clampValue(Double.parseDouble(args[1]), 1.0, l.getWorld().getMaxHeight());
                    z = clampValue(Double.parseDouble(args[2]), wcfg.restrictWorldZminus, wcfg.restrictWorldZpluss);

                    if (args.length < 4 || (args.length == 4 && !args[3].equalsIgnoreCase("f"))) {
                        //Put us at the middle of the block
                        l.setX(Math.floor(x) + 0.5);
                        l.setY(Math.floor(y));
                        l.setZ(Math.floor(z) + 0.5);
                    } else {
                        l.setX(x);
                        l.setY(y);
                        l.setZ(z);
                    }
                    this.userHandler.setTeleportBackLocation(player, player.getLocation());
                    player.teleport(l);
                }
            } catch (NumberFormatException e) {
                player.sendMessage(getErrorChatColor() + "En eller flere av argumentene var ikke et gyldig tall.");
                return false;
            }
            player.sendMessage(getDefaultChatColor() + "Du ble teleportert til: " + getOkChatColor() + "koordinater: " + getErrorChatColor() + x + " " + y + " " + z +
                getDefaultChatColor() + ".");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Setter i til å være maksimalt max, og minst min.
     *
     * @param i
     * @param min
     * @param max
     *
     * @return
     */
    private static double clampValue(double i, final double min, final double max) {
        if (i > max) {
            i = max;
        } else if (i < min) {
            i = min;
        }
        return i;
    }
}
