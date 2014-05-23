package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class BcCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public BcCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            String prefix = getVarChatColor() + "(" + getErrorChatColor() + "Viktig" + getVarChatColor() + ") ";

            StringBuilder build = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                build.append(args[i] + " ");
            }
            String msg = build.toString();
            msg = msg.trim().replaceAll("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", getErrorChatColor() + "Error").replaceAll("\\s+", " ");
            this.plugin.getServer().broadcastMessage(prefix + getVarChatColor() + msg);
            this.plugin.getIrcBot().sendMessage("#hardwork", "(Viktig) " + msg);
            return true;
        } else {
            return false;
        }
    }
}