package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class WeekBanCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public WeekBanCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
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
                if (this.userHandler.WeekbanUser(victim, build.toString(), player)) {
                    this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), 0, 0, build.toString(), MinecraftnoLog.WEEKBAN);
                    for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                        if (this.userHandler.getAnnonseringer(reciever)) {
                            reciever.sendMessage(victim.getDisplayName() + getDefaultChatColor() + " ble ukesbannet: " + getVarChatColor() + build.toString());
                            reciever.sendMessage(getDefaultChatColor() + "Ukesbannet av : " + player.getDisplayName());
                        }
                    }
                    victim.kickPlayer("Ukesbannet: " + build.toString());
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor() + "FEIL MED UKESBAN! KONTAKT TJNOME!");
                }
            } else {
                String victimName = this.userHandler.getUsernameFromDB(args[0]);
                if (!victimName.isEmpty()) {
                    if (this.userHandler.isBanned(victimName)) {
                        player.sendMessage(getErrorChatColor() + "Brukeren er allerede permbannet. (" + getVarChatColor() + victimName + getErrorChatColor() + ")");
                    } else if (this.userHandler.isWeekBanned(victimName)) {
                        player.sendMessage(getErrorChatColor() + "Brukeren er allerede ukesbannet. (" + getVarChatColor() + victimName + getErrorChatColor() + ")");
                    } else {
                        if (this.userHandler.WeekbanUser(victimName, build.toString(), player)) {
                            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                                if (this.userHandler.getAnnonseringer(reciever)) {
                                    reciever.sendMessage(this.plugin.getUserHandler().getPlayerDisplayName(victimName) + getDefaultChatColor() + " ble ukesbannet: " + getVarChatColor() + build.toString());
                                    reciever.sendMessage(getDefaultChatColor() + "Ukesbannet av : " + player.getDisplayName());
                                }
                            }
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "FEIL MED UKESBAN! KONTAKT TJNOME!");
                        }
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Ingen matcher brukernavnet");
                }
            }
            return true;
        } else {
            return false;
        }
    }
}