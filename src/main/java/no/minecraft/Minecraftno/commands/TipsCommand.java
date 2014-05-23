package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TipsCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public TipsCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
    }

    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            String prefix = ChatColor.WHITE + "(" + getOkChatColor() + "Tips" + getVarChatColor() + ") ";
            StringBuilder build = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                build.append(args[i] + " ");
            }
            String msg = build.toString();
            msg = msg.trim().replaceAll("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", getErrorChatColor() + "Error").replaceAll("\\s+", " ");
            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                if (this.userHandler.getAnnonseringer(reciever)) {
                    if (this.userHandler.getAnnonseringer(reciever)) {
                        reciever.sendMessage((prefix + getVarChatColor() + msg));
                    }
                }
            }
            this.plugin.getIrcBot().sendMessage("#hardwork", "(Tips) " + msg);
        } else {
            return false;
        }
        return true;
    }
}
