package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class GCommand extends MinecraftnoCommand {

    @SuppressWarnings("unused")
    private final Minecraftno plugin;
    private final GroupHandler groupHandler;

    public GCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.groupHandler = instance.getGroupHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (this.groupHandler.isInGroup(player)) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (!(this.userHandler.getGroupChatBind(player))) {
                        if (!(this.userHandler.getAdminChatBind(player))) {
                            this.userHandler.setGroupChatBind(player, true);
                            player.sendMessage(getOkChatColor() + "Gruppechat aktivert");
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "Du kan ikke aktivere gruppechat når du har aktivert Stab/vakt chat.");
                            return true;
                        }
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede aktivert gruppechat.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("off")) {
                    if (this.userHandler.getGroupChatBind(player)) {
                        this.userHandler.setGroupChatBind(player, false);
                        player.sendMessage(getOkChatColor() + " Du har nå deaktivert gruppechat.");
                        return true;
                    }
                } else {
                    StringBuilder build = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        build.append(args[i] + " ");
                    }
                    this.groupHandler.sendGroupMessage(player, build.toString());
                    return true;
                }
            }
            return false;
        } else {
            player.sendMessage(getErrorChatColor() + "Du kan ikke bruke gruppechat uten å være medlem i en gruppe.");
            return true;
        }
    }
}