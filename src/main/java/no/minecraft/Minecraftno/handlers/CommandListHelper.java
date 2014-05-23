package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.commands.MinecraftnoCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandListHelper {

    List<PluginCommand> commands;
    ArrayList<ArrayList<String>> commandLists;

    public CommandListHelper() {
        commandLists = new ArrayList<ArrayList<String>>(5);
        commands = new ArrayList<PluginCommand>();
        for (int i = 0; i < 5; ++i) {
            this.commandLists.add(new ArrayList<String>());
        }
    }

    /**
     * Lager strengelistene som skal brukes i /help-kommandoen
     *
     * @param plugin
     */
    public void populateLists(Minecraftno plugin) {
        // Fant ikke noen god måte å få tak i objektet inni plugin der listen
        // med
        // kommandoer var, så må lage ei liste med kommandoer på en litt
        // knotete måte.
        List<Command> parseCommands = PluginCommandYamlParser.parse(plugin);
        Logger.getLogger("Minecraft").log(Level.INFO, "[Minecraftno] Hentet " + parseCommands.size() + " kommandoer fra plugin.yml.");
        for (Command c : parseCommands) {
            PluginCommand comm = plugin.getCommand(c.getName());
            if (comm != null) {
                this.commands.add(comm);
            }
        }

        for (PluginCommand c : this.commands) {
            // Lage lister med kommandoer.
            if (c != null) {
                CommandExecutor ce = c.getExecutor();
                if (ce instanceof MinecraftnoCommand)
                // Class<Extends HardworkCommand.class>
                {
                    MinecraftnoCommand hc = (MinecraftnoCommand) ce;
                    if (ce != null) {
                        String usage = " ";
                        if (c.getUsage().split(" ").length > 1) {
                            String[] getUsage = c.getUsage().split(" ");
                            StringBuilder build = new StringBuilder();
                            for (int i = 1; i < getUsage.length; i++) {
                                build.append(getUsage[i] + " ");
                            }
                            usage = build.toString();
                        }
                        this.commandLists.get(hc.getAccessLevel()).add(c.getName() + " " + ChatColor.GRAY + usage + ChatColor.WHITE + "- " + c.getDescription());
                    }
                } else {
                    Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kommandoen: " + c.getName() + " er ikke en \"HardworkCommand\"");
                }
            }
        }
        // Legge til kommandoer fra lavere permission til høyere.
        for (int i = 1; i < this.commandLists.size(); ++i) {
            this.commandLists.get(i).addAll(this.commandLists.get(i - 1));
            Collections.sort(this.commandLists.get(i));
        }
    }

    /**
     * Henter strengeliste med kommandoer fra access-level accessLevel
     *
     * @param accessLevel
     *
     * @return
     */
    public ArrayList<String> getList(int accessLevel) {
        if (accessLevel < this.commandLists.size()) {
            return this.commandLists.get(accessLevel);
        }
        // Returner en tomm liste hvis noe er feil
        Logger.getLogger("Minecraft").log(Level.WARNING, "[Minecraftno] prøvde å hente" + " kommandoliste fra en access-level" + "som ikke finnes");
        return new ArrayList<String>();
    }
}
