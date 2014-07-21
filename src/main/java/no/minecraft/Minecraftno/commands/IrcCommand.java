package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IrcCommand implements CommandExecutor {
    private Minecraftno plugin;

    public IrcCommand(Minecraftno plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Denne kommandoen kan kun brukes in-game!");
            return true;
        }

        if (args.length > 0)
            return false;

        sender.sendMessage(ChatColor.GREEN + "Skriv følgende i din IRC-klient:");
        sender.sendMessage(ChatColor.WHITE + "/msg H -access " + this.plugin.getIrcBot().getAccessCode(sender.getName()));

        return true;
    }
}
