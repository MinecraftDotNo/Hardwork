package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarningHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class WarnCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final WarningHandler warningHandler;

    public WarnCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.warningHandler = instance.getWarningHandler();
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 1) {
            StringBuilder build = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                build.append(args[i] + " ");
            }
            Player victim = this.plugin.playerMatch(args[0]);
            if (victim != null) {
                try {
                    boolean success = this.warningHandler.addWarning(victim, player, build.toString(), player.getLocation());
                    if (success) {
                        player.sendMessage(getOkChatColor() + "Advarsel satt på: " + getVarChatColor() + victim.getName());
                        List<String> warningList = this.warningHandler.getWarnings(victim);
                        if (warningList != null) {
                            int countWarnings = this.warningHandler.countWarnings(victim);
                            player.sendMessage(getOkChatColor() + "Brukeren har nå: " + getVarChatColor() + countWarnings + getOkChatColor() + " advarsler");
                            victim.sendMessage(getErrorChatColor() + "Du fikk en advarsel av: " + getVarChatColor() + player.getName() + getErrorChatColor() + ".");
                            victim.sendMessage(getDefaultChatColor() + "Grunn: " + getVarChatColor() + build.toString());
                            this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " delte ut en advarsel til " + victim.getName() + " (Antall advarsler: " + countWarnings + "). Grunnlag: " + build.toString());
                        }
                    }
                } catch (SQLException e) {
                    player.sendMessage(getErrorChatColor() + "Fikk ikke satt advarsel. Kontakt stab.");
                } catch (CommandException e) {
                    player.sendMessage(getErrorChatColor() + "Fikk ikke satt advarsel. Kontakt stab.");
                }
            } else {
                String victimName = this.userHandler.getUsernameFromDB(args[0]);
                try {
                    boolean success = this.warningHandler.addWarning(victimName, player, build.toString(), player.getLocation());
                    if (success) {
                        player.sendMessage(getOkChatColor() + "Advarsel satt på: " + getVarChatColor() + victimName);
                        List<String> warningList = this.warningHandler.getWarnings(victimName);
                        if (warningList != null) {
                            int countWarnings = this.warningHandler.countWarnings(victimName);
                            player.sendMessage(getOkChatColor() + "Brukeren har nå: " + getVarChatColor() + countWarnings + getOkChatColor() + " advarsler");
                            this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " delte ut en advarsel til " + victimName + " (Antall advarsler: " + countWarnings + "). Grunnlag: " + build.toString());
                        }
                    }
                } catch (SQLException e) {
                    player.sendMessage(getErrorChatColor() + "Fikk ikke satt advarsel. Kontakt stab.");
                } catch (CommandException e) {
                    player.sendMessage(getErrorChatColor() + "Fikk ikke satt advarsel. Kontakt stab.");
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
