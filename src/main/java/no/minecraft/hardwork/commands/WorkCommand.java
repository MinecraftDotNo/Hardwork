package no.minecraft.hardwork.commands;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.handlers.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Edvin
 */
public class WorkCommand implements CommandExecutor  {
    
    private final Hardwork hardwork;

    public WorkCommand(Hardwork hardwork) {
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
        String uuid = player.getUniqueId().toString();

        UserHandler uh = this.hardwork.getUserHandler();
        
        if (uh.isInWork(uuid) == false) {
            // Set user in WorkMode.
            if (uh.saveInventoryForWork(player) == false) {
                player.sendMessage(ChatColor.RED + "En feil skjedde under lagring av din inventory, kontakt en utvikler!");
            } else {
                player.sendMessage(ChatColor.GREEN + "Din inventory ble lagret og arbeids-verktøy er tildelt.");
            }
        } else {
            // Give user back inventory.
            if (uh.setSavedInventoryWork(player) == false) {
                player.sendMessage(ChatColor.RED + "En feil skjedde under henting av din inventory, kontakt en utvikler!");
            } else {
                player.sendMessage(ChatColor.GREEN + "Du har fått tilbake din inventory.");
            }
        }
        
        return true;
    }

}
