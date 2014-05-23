package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ServerInfoCommand extends MinecraftnoCommand {

    public ServerInfoCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage(getInfoChatColor() + "Craftbukkit version: " + getCommandChatColor() + plugin.getServer().getBukkitVersion());
            player.sendMessage(getInfoChatColor() + "Server versjon: " + getCommandChatColor() + plugin.getServer().getVersion());
            if (plugin.getServer().getIp() != null) {
                player.sendMessage(getInfoChatColor() + "Server IP: " + getCommandChatColor() + "mc.hardwork.no");
            }
            player.sendMessage(getInfoChatColor() + "Server port: " + getCommandChatColor() + plugin.getServer().getPort());
            player.sendMessage(getInfoChatColor() + "Server spillemodus: " + getCommandChatColor() + plugin.getServer().getDefaultGameMode().toString());
            player.performCommand("plugins");
        } else {
            return false;
        }
        return true;
    }
}