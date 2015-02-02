package no.minecraft.hardwork.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.BankHandler;
import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import no.minecraft.hardwork.handlers.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final Hardwork hardwork;

    public SetHomeCommand(Hardwork hardwork) {
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

        if (args.length == 0 || args[0].equalsIgnoreCase("ja")) {
            if (user.getAccessLevel() < 3) {
                // Todo: Rewrite. This relies on old MinecraftNo code.
                BankHandler bank = ((Minecraftno) this.hardwork.getPlugin()).getBankHandler();

                if (bank.getAmount(player) < 50) {
                    player.sendMessage(ChatColor.RED + "Det koster 50 gull å sette nytt home. Du har for lite gull i banken :(");
                    return true;
                }

                bank.removeAmount(player, 50);
            }

            uh.setHome(user, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Home satt :)");
            return true;
        } else if (user.getAccessLevel() >= 3) {
            User target = uh.getUser(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Fant ikke spilleren.");
                return true;
            }

            uh.setHome(target, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Home for " + target.getName() + " satt :)");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Du har ikke tilgang til å sette andres home.");
            return true;
        }

        player.sendMessage(ChatColor.GRAY + "Det koster " + ChatColor.RED + "50 gull " + ChatColor.GRAY + " å sette home. Skriv \"/sethome ja\" for å godkjenne.");

        return true;
    }
}
