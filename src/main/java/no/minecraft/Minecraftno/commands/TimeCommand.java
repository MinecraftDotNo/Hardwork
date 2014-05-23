package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TimeCommand extends MinecraftnoCommand {

    public TimeCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        World world = player.getWorld();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);
        if (args.length == 1 && wcfg.canChangeTime) {
            long curtime = player.getPlayerTime();
            long newtime = curtime - curtime % 24000L;
            if (args[0].equalsIgnoreCase("dag")) {
                newtime += 6000L;
                player.sendMessage(getWarningChatColor() + "Din tid har blitt forandret til Dag");
            } else if (args[0].equalsIgnoreCase("natt")) {
                newtime += 14000L;
                player.sendMessage(getWarningChatColor() + "Din tid har blitt forandret til Natt");
            } else if (args[0].equalsIgnoreCase("kveld")) {
                newtime += 12500L;
                player.sendMessage(getWarningChatColor() + "Din tid har blitt forandret til kveld");
            } else if (args[0].equalsIgnoreCase("morgen")) {
                newtime += 23000L;
                player.sendMessage(getWarningChatColor() + "Din tid har blitt forandret til morgen");
            } else if (args[0].equalsIgnoreCase("normal")) {
                newtime = player.getWorld().getTime();
                player.sendMessage(getWarningChatColor() + "Din tid er nå servertid");
                player.setPlayerTime(0, true);
            } else if (args[0].equalsIgnoreCase("info")) {
                player.sendMessage(getWarningChatColor() + "/time dag");
                player.sendMessage(getWarningChatColor() + "/time natt");
                player.sendMessage(getWarningChatColor() + "/time morgen");
                player.sendMessage(getWarningChatColor() + "/time kveld");
                player.sendMessage(getWarningChatColor() + "/time normal");
            } else {
                try {
                    newtime += Integer.parseInt(args[0]);
                    player.sendMessage(getWarningChatColor() + "Din tid har blitt forandret til" + getOkChatColor() + newtime);
                } catch (Exception e) {
                    return false;
                }
            }
            player.setPlayerTime(newtime, false);
            return true;
        } else {
            player.sendMessage(getWarningChatColor() + "/time dag");
            player.sendMessage(getWarningChatColor() + "/time natt");
            player.sendMessage(getWarningChatColor() + "/time morgen");
            player.sendMessage(getWarningChatColor() + "/time kveld");
            player.sendMessage(getWarningChatColor() + "/time normal");
            return true;
        }
    }
}