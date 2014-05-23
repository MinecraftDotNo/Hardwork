package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.LogHandler;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class KickCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final LogHandler logHandler;

    public KickCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(2);
        this.logHandler = instance.getLogHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        String msg = "Ingen grunn oppgitt, vi trenger ikke en.";
        if (args.length > 1) {
            StringBuilder build = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                build.append(args[i] + " ");
            }
            msg = build.toString();
        }
        Player kick = this.plugin.playerMatch(args[0]);
        if (kick != null) {
            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                if ((player == reciever) && (!(this.userHandler.getAnnonseringer(player)))) {
                    player.sendMessage(kick.getDisplayName() + getWarningChatColor() + " ble kicket: " + getVarChatColor() + msg);
                    player.sendMessage("Kicket av : " + player.getDisplayName());
                }
                if (this.userHandler.getAnnonseringer(reciever)) {
                    reciever.sendMessage(kick.getDisplayName() + getWarningChatColor() + " ble kicket: " + getVarChatColor() + msg);
                    reciever.sendMessage("Kicket av : " + player.getDisplayName());
                }
            }

            this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(kick), 0, 0, msg, MinecraftnoLog.KICK);
            this.userHandler.updatePlayer(kick, 4);
            kick.kickPlayer(msg);
            return true;
        } else {
            player.sendMessage(getErrorChatColor() + " Fant ikke brukeren du ville kicke.");
            return true;
        }
    }
}
