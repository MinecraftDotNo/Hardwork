package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SavePlayerDataCommand extends MinecraftnoCommand {

    public SavePlayerDataCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            Player[] onlinePlayers = this.plugin.getServer().getOnlinePlayers();
            player.sendMessage(getInfoChatColor() + "Lagrer data for: " + onlinePlayers.length + " spillere...");
            for (Player p : onlinePlayers) {
                p.saveData();
            }
            player.sendMessage(getOkChatColor() + "Ferdig");
            return true;
        } else {
            return false;
        }
    }
}