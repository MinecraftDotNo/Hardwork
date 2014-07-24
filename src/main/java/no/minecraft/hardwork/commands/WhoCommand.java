package no.minecraft.hardwork.commands;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class WhoCommand implements CommandExecutor {
    private final Hardwork hardwork;

    public WhoCommand(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Player[] onlinePlayers = this.hardwork.getPlugin().getServer().getOnlinePlayers();

            Map<Integer, List<String>> players = new HashMap<>();
            for (Player player : onlinePlayers) {
                User user = this.hardwork.getUserHandler().getUser(player.getUniqueId());
                int accessLevel = user == null ? 0 : user.getAccessLevel();

                if (!players.containsKey(accessLevel))
                    players.put(accessLevel, new ArrayList<String>());

                players.get(accessLevel).add(player.getDisplayName());
            }

            List<String> output = new ArrayList<>();
            for (int accessLevel : new TreeSet<>(players.keySet())) {
                List<String> sort = players.get(accessLevel);
                Collections.sort(sort);

                output.add(StringUtils.join(sort, ChatColor.RESET + ", "));
            }

            sender.sendMessage(ChatColor.GREEN + "Påloggede spillere (" + onlinePlayers.length + "): " + StringUtils.join(output, ChatColor.RESET + ", "));

            return true;
        }

        if (args.length == 1) {
            if (sender.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatColor.GREEN + "Ta en titt i speilet!");
                return true;
            }

            Player player = this.hardwork.getPlugin().getServer().getPlayerExact(args[0]);

            if (player != null) {
                sender.sendMessage(ChatColor.GREEN + player.getName() + " er pålogget nå!");
                return true;
            }

            User user = this.hardwork.getUserHandler().getUser(args[0]);

            if (user == null) {
                sender.sendMessage(ChatColor.RED + "Fant ingen spiller med navnet \"" + args[0] + "\".");
                return true;
            }

            Date date = this.hardwork.getUserHandler().getUserLastLogin(user);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            sender.sendMessage(ChatColor.GREEN + user.getName() + " var sist pålogget " + dateFormat.format(date) + ".");

            return true;
        }

        return false;
    }
}
