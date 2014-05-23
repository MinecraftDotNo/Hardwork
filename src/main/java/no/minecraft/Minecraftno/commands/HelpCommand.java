package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.CommandListHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedList;

public class HelpCommand extends MinecraftnoCommand {

    private final CommandListHelper helper;
    private static int MAX_HELP_LENGTH = 10;

    public HelpCommand(Minecraftno instance, CommandListHelper helper) {
        super(instance);
        setAccessLevel(0);
        this.helper = helper;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length < 2) {
            LinkedList<String> commandList = new LinkedList<String>(this.helper.getList(userHandler.getAccess(player)));
            ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
            ConfigurationWorld wcfg = cfg.get(player.getWorld());

            for (Iterator<String> iter = commandList.iterator(); iter.hasNext(); ) {
                String[] thecommand = iter.next().split(" ");
                if (!wcfg.isCommandEnabled(thecommand[0].trim())) {
                    iter.remove();
                }
            }
            int page = 0;
            int pages = (commandList.size() / MAX_HELP_LENGTH) + 1;
            if (args.length == 1) {
                try {
                    page = Integer.parseInt(args[0]) - 1;
                    if (page < 0) {
                        page = 0;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(getErrorChatColor() + "Feil: Ugyldig side!");
                    return true;
                }
            }
            if (page >= pages || page < 0) {
                player.sendMessage(getErrorChatColor() + "Advarsel: siden eksisterer ikke.");
                page = 0;
            }
            int start = page * MAX_HELP_LENGTH;
            int end = start + MAX_HELP_LENGTH;
            player.sendMessage("--------- " + getDefaultChatColor() + "Hjelp - side: " +
                getVarChatColor() + (page + 1) + getDefaultChatColor() +
                " (" + getVarChatColor() + commandList.size() + getDefaultChatColor() + " kommandoer, " +
                getVarChatColor() + pages + getDefaultChatColor() + " sider)" + getVarChatColor() + " --------");

            for (int i = start; i < commandList.size() && i < end; i++) {
                player.sendMessage(getDefaultChatColor() + "/" + ChatColor.GOLD + commandList.get(i));
            }
            player.sendMessage("-----------------------------------------------------");
            if (end < commandList.size() - 1) {
                player.sendMessage(getDefaultChatColor() + "Skriv /" + getCommandChatColor() + "hjelp " + getVarChatColor() + (page + 2) + getDefaultChatColor() + " for flere kommandoer.");
            }
            return true;
        } else {
            return false;
        }
    }
}
