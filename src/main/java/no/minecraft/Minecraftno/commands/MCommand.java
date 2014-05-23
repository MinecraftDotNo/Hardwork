package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.ChatHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class MCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final ChatHandler chatHandler;

    public MCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
        this.logHandler = instance.getLogHandler();
        this.userHandler = instance.getUserHandler();
        this.chatHandler = instance.getChatHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 2) {
            if (!this.userHandler.isRegPlayer(player)) {
                Player sendTo = this.plugin.playerMatch(args[0]);
                if (sendTo != null) {
                    if (sendTo.equals(player)) {
                        player.sendMessage(getErrorChatColor() + " Kan ikke sende til deg selv.");
                        return true;
                    } else {
                        StringBuilder build = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            build.append(args[i] + " ");
                        }
                        this.chatHandler.sendPrivatMessage(player, sendTo, build.toString());
                        return true;
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + " Brukeren er ikke pålogget.");
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}