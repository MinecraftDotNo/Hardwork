package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class InvisibleCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;

    public InvisibleCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                if (!this.userHandler.getInvisible(player)) {
                    for (Player target : this.plugin.getServer().getOnlinePlayers()) {
                        target.hidePlayer(player);
                    }
                    player.performCommand("dynmap hide");
                    player.sendMessage(getOkChatColor() + " Du er nå usynlig for andre brukere.");
                    this.userHandler.setInvisible(player, true);
                } else {
                    player.sendMessage(getOkChatColor() + " Du er allerede usynlig for andre brukere.");
                }
            } else if (args[0].equalsIgnoreCase("off")) {
                if (this.userHandler.getInvisible(player)) {
                    for (Player target : this.plugin.getServer().getOnlinePlayers()) {
                        target.showPlayer(player);
                    }
                    player.performCommand("dynmap show");
                    player.sendMessage(getOkChatColor() + " Du er nå synlig igjen.");
                    this.userHandler.setInvisible(player, false);
                } else {
                    player.sendMessage(getOkChatColor() + " Du er allerede synlig.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Ugyldig argument");
            }
            return true;
        } else {
            return false;
        }
    }
}