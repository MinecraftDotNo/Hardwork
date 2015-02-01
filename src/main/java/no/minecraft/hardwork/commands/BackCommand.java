package no.minecraft.hardwork.commands;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import no.minecraft.hardwork.handlers.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleport Player to latest back location.
 * @author Edvin
 *
 */
public class BackCommand implements CommandExecutor {
    
    private final Hardwork hardwork;

    public BackCommand(Hardwork hardwork) {
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
        User user = uh.getUser(player.getUniqueId());
        
        Location loc = uh.getBackLocation(user);
        
        if (loc != null)
            player.teleport(loc);
        
        return true;
    }
}
