package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class WordFilterCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public WordFilterCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
        this.plugin = instance;
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();

        if (args.length == 0) {
            this.displayFilterMenu(player);
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("ad")) {
                if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("add")) {
                        if (cfg.addAdvertiseWord(args[2])) {
                            player.sendMessage(getInfoChatColor() + "Du la til ordet: " + getVarChatColor() + args[2] + getInfoChatColor() + " i filteret.");
                        } else {
                            player.sendMessage(getErrorChatColor() + "Ordet finnes allerede i listen.");
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (cfg.delAdvertiseWord(args[2])) {
                            player.sendMessage(getInfoChatColor() + "Du fjernet ordet: " + getVarChatColor() + args[2] + getInfoChatColor() + " fra filter-listen.");
                        } else {
                            player.sendMessage(getErrorChatColor() + "Fant ikke ordet i filter-listen.");
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Ugyldig kommando.");
                    }
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("list")) {
                        StringBuilder build = new StringBuilder();
                        for (String ad : cfg.advertise) {
                            build.append(ad + ", ");
                        }
                        build.setLength(build.length() - 2);
                        build.append('.');

                        player.sendMessage(getDefaultChatColor() + "Filter ad liste: " + build.toString());
                    } else {
                        player.sendMessage(getErrorChatColor() + "Ugyldig kommando.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("login")) {
                if (!cfg.showloginonguest) {
                    cfg.showloginonguest = true;
                    player.sendMessage(getInfoChatColor() + "Login/logut fra gjester vises nå i chat");
                } else if (cfg.showloginonguest) {
                    cfg.showloginonguest = false;
                    player.sendMessage(getInfoChatColor() + "Login/logut fra gjest vises nå ikke i chat");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Ugyldig kommando.");
            }
        }

        return true;
    }

    /**
     * Displays the filter menu
     *
     * @param player Player issuing the command
     */
    public void displayFilterMenu(Player player) {
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "filter" + getVarChatColor() + " - Viser denne menyen.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "filter ad add" + getVarChatColor() + " - Legger til et nytt ord i chatfilteret.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "filter ad remove" + getVarChatColor() + " - Fjerner et ord fra chatfilteret.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "filter ad list" + getVarChatColor() + " - Lister ut alle ordene i chatfilteret.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "filter login" + getVarChatColor() + " - Endrer innstilling for om innlogging og utlogging av gjester skal vises.");
    }
}

