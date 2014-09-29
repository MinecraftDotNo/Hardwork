package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SavePlayerDataCommand extends MinecraftnoCommand {

    public SavePlayerDataCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            Collection<? extends Player> onlinePlayers = this.plugin.getServer().getOnlinePlayers();
            player.sendMessage(getInfoChatColor() + "Lagrer data for: " + onlinePlayers.size() + " spillere...");
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