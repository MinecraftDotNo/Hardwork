package no.minecraft.Minecraftno.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WikiCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public WikiCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && sender.isOp()) {
            this.plugin.getConfig().set("wiki", StringUtils.join(args, " "));
            this.plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Wiki-melding endret:");
        }

        sender.sendMessage(this.plugin.getConfig().getString("wiki", ChatColor.RED + "Informasjon om wiki mangler.").replace("&", String.valueOf(ChatColor.COLOR_CHAR)));
        return true;
    }
}
