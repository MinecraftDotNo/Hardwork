package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jibble.pircbot.User;

public class ircCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public ircCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
        this.plugin = instance;
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("kick")) {
                if (this.plugin.getIrcBot() == null) {
                    User bruker = this.plugin.playerMatchIRC(args[1]);
                    if (bruker != null) {
                        this.plugin.getIrcBot().kick("#hardwork", bruker.getNick(), args[2]);
                        return true;
                    }
                }
            } else if (args[0].equalsIgnoreCase("ban")) {
                if (this.plugin.getIrcBot() == null) {
                    User bruker = this.plugin.playerMatchIRC(args[1]);
                    if (bruker != null) {
                        this.plugin.getIrcBot().ban("#hardwork", bruker.getNick());
                        return true;
                    }
                }
            } else if (args[0].equalsIgnoreCase("unban")) {
                if (this.plugin.getIrcBot() == null) {
                    this.plugin.getIrcBot().unBan("#hardwork", args[1]);
                    return true;
                }
            }
        }
        return false;
    }
}