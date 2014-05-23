package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class BanCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public BanCommand(Minecraftno instance) {
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
                if (this.userHandler.banUser(victim, build.toString(), player)) {
                    this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), 0, 0, build.toString(), MinecraftnoLog.BAN);
                    for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                        if (this.userHandler.getAnnonseringer(reciever)) {
                            reciever.sendMessage(victim.getDisplayName() + getDefaultChatColor() + " ble bannet: " + getVarChatColor() + build.toString());
                            reciever.sendMessage((getDefaultChatColor() + "Bannet av : " + player.getDisplayName()));
                        }
                    }
                    victim.kickPlayer(build.toString());
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor() + "FEIL MED BAN! KONTAKT TJNOME!");
                }
            } else {
                String victimName = this.userHandler.getUsernameFromDB(args[0]);
                if (!victimName.isEmpty()) {
                    if (this.userHandler.isBanned(victimName)) {
                        player.sendMessage(getErrorChatColor() + "Brukeren er allerede permbannet. (" + getVarChatColor() + victimName + getErrorChatColor() + ")");
                        return true;
                    } else if (this.userHandler.isWeekBanned(victimName)) {
                        player.sendMessage(getErrorChatColor() + "Brukeren " + getVarChatColor() + victimName + getErrorChatColor() + " har allerede ukesban, vil nå få permban");
                        this.userHandler.unBanUser(victimName, true);
                        if (this.userHandler.banUser(victimName, build.toString(), player)) {
                            this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), 0, 0, build.toString(), MinecraftnoLog.BAN);
                            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                                if (this.userHandler.getAnnonseringer(reciever)) {
                                    reciever.sendMessage(this.userHandler.getPlayerDisplayName(victimName) +
                                        getDefaultChatColor() + " ble bannet: " + getVarChatColor() + build.toString());
                                    reciever.sendMessage(getDefaultChatColor() + "Bannet av : " + player.getDisplayName());
                                }
                            }
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "FEIL MED BAN! KONTAKT TJNOME!");
                        }
                    } else {
                        if (this.userHandler.banUser(victimName, build.toString(), player)) {
                            this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), 0, 0, build.toString(), MinecraftnoLog.BAN);
                            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                                if (this.userHandler.getAnnonseringer(reciever)) {
                                    reciever.sendMessage(this.userHandler.getPlayerDisplayName(victimName) +
                                        getDefaultChatColor() + " ble bannet: " + getVarChatColor() + build.toString());
                                    reciever.sendMessage(getDefaultChatColor() + "Bannet av : " + player.getDisplayName());
                                }
                            }
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "FEIL MED BAN! KONTAKT TJNOME!");
                        }
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Brukeren eksisterer ikke. (" + getVarChatColor() + args[0] + ")");
                    return true;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}