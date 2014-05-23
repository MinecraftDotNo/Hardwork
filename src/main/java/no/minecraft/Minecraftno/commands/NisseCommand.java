package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.NisseHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class NisseCommand extends MinecraftnoCommand {
    public NisseCommand(Minecraftno instance) {
        super(instance);

        this.setAccessLevel(2);
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        NisseHandler nh = this.plugin.getNisseHandler();

        if (nh.isNisse(player)) {
            nh.removeNisse(player);

            player.sendMessage(ChatColor.GRAY + "Nissemodus " + ChatColor.RED + "deaktivert" + ChatColor.GRAY + "!");
        } else {
            nh.addNisse(player);

            player.sendMessage(ChatColor.GRAY + "Nissemodus " + ChatColor.GREEN + "aktivert" + ChatColor.GRAY + "!");
        }

        return true;
    }
}
