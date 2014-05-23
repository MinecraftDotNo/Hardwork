package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class UnbanCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public UnbanCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player victim = this.plugin.getServer().getPlayer(args[0]);
            if (victim == null) {
                String victimName = this.userHandler.getUsernameFromDB(args[0]);
                if (victimName != null) {
                    if (this.userHandler.isBanned(victimName)) {
                        this.userHandler.unBanUser(victimName, false);
                        this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), 0, 0, null, MinecraftnoLog.UNBAN);
                    } else if (this.userHandler.isWeekBanned(victimName)) {
                        this.userHandler.unBanUser(victimName, true);
                        this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), 0, 0, null, MinecraftnoLog.UNBAN);
                    } else {
                        player.sendMessage(getErrorChatColor() + "Brukeren er ikke bannet. (" + getVarChatColor() + victimName + getErrorChatColor() + ")");
                        return true;
                    }
                    for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                        if (this.userHandler.getAnnonseringer(reciever)) {
                            if ((player == reciever) && (!(this.userHandler.getAnnonseringer(player)))) {
                                reciever.sendMessage(getOkChatColor() + "Brukeren: " + getVarChatColor() + victimName + getOkChatColor() + " er ikke lengre bannet.");
                            }
                            reciever.sendMessage(getOkChatColor() + "Brukeren: " + getVarChatColor() + victimName + getOkChatColor() + " er ikke lengre bannet.");
                        }
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Brukeren eksisterer ikke. (" + getVarChatColor() + args[0] + ")");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Brukeren er ikke bannet. (" + getVarChatColor() + victim.getName() + getErrorChatColor() + ")");
                return true;
            }
            return true;
        } else {
            return false;
        }
    }
}
