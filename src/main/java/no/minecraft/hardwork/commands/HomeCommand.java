package no.minecraft.hardwork.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import no.minecraft.hardwork.handlers.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final Hardwork hardwork;

    public HomeCommand(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used in-game.");
            return true;
        }

        if (args.length > 1)
            return false;

        Player player = (Player) sender;

        UserHandler uh = this.hardwork.getUserHandler();
        no.minecraft.Minecraftno.handlers.player.UserHandler mcnoUh = Minecraftno.getInstance().getUserHandler();

        User user = uh.getUser(player.getUniqueId());

        Location home = null;

        if (args.length == 0) {
            home = uh.getHome(user);
        } else if (user.getAccessLevel() >= 3) {
            User target = uh.getUser(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Fant ikke spilleren.");
                return true;
            }

            home = uh.getHome(target);
        } else {
            player.sendMessage(ChatColor.RED + "Du har ikke tilgang til å gå til andres home.");
            return true;
        }

        if (home == null) {
            player.sendMessage(ChatColor.RED + "Fant ikke noe home :(");
            return true;
        }

        mcnoUh.setTeleportBackLocation(player, player.getLocation());
        player.teleport(home);

        player.sendMessage(ChatColor.GREEN + "*poof*");

        return true;
    }
}
