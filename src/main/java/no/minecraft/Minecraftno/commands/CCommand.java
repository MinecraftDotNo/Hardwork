package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.ChatHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;
    private final ChatHandler chatHandler;

    public CCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(2);
        this.userHandler = instance.getUserHandler();
        this.chatHandler = instance.getChatHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("on")) {
                if (!(this.userHandler.getAdminChatBind(player))) {
                    if (!(this.userHandler.getGroupChatBind(player))) {
                        this.userHandler.setAdminChatBind(player, true);
                        player.sendMessage(getOkChatColor() + "Stab/vakt chat aktivert.");
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du kan ikke aktivere Stab/vakt når group chat er aktivert.");
                    }
                } else {
                    player.sendMessage(getOkChatColor() + " Du har allerede aktivert Stab/vakt chat.");
                }
            } else if (args[0].equalsIgnoreCase("off")) {
                if (this.userHandler.getAdminChatBind(player)) {
                    this.userHandler.setAdminChatBind(player, false);
                    player.sendMessage(getOkChatColor() + " Stab/vakt deaktivert.");
                }
            } else {
                StringBuilder build = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    build.append(args[i] + " ");
                }
                this.chatHandler.sendVaktStabMessage(player, build.toString());
            }
            return true;
        } else {
            return false;
        }
    }
}