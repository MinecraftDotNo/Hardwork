package no.minecraft.hardwork.commands;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import no.minecraft.hardwork.handlers.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {
    private final Hardwork hardwork;

    public DelHomeCommand(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        UserHandler uh = this.hardwork.getUserHandler();

        if (sender instanceof Player && uh.getUser(((Player) sender).getUniqueId()).getAccessLevel() < 3) {
            sender.sendMessage(ChatColor.RED + "Du har ikke tilgang til å fjerne andres home.");
            return true;
        }

        if (args.length != 1)
            return false;

        User target = uh.getUser(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Fant ikke spilleren.");
            return true;
        }

        uh.deleteHome(target);

        sender.sendMessage(ChatColor.GREEN + "Home slettet.");

        return false;
    }
}
