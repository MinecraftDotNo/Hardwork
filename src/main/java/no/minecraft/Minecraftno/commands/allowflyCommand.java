package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class allowflyCommand extends MinecraftnoCommand {

    public allowflyCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.performCommand("inv on");
            player.performCommand("dynmap hide");
            player.sendMessage(getOkChatColor() + "Fly mode: " + getConfirmChatColor() + "ON");
            return true;
        } else {
            player.performCommand("inv off");
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(getOkChatColor() + "Fly mode: " + getConfirmChatColor() + "OFF");
            player.performCommand("dynmap show");
            return true;
        }
    }
}