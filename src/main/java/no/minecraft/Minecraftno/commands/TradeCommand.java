package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.ChatHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TradeCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;
    private final ChatHandler chatHandler;

    public TradeCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.userHandler = instance.getUserHandler();
        this.chatHandler = instance.getChatHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {

        if (args.length >= 1) {
            if (!this.userHandler.isRegPlayer(player)) {
                if (args[0].equalsIgnoreCase("off")) {
                    if ((this.userHandler.getTradeChat(player))) {
                        player.sendMessage(getErrorChatColor() + "Kjøp/Salg deaktivert");
                        this.userHandler.setTradeChat(player, false);
                    }
                } else if (args[0].equalsIgnoreCase("on")) {
                    if (!(this.userHandler.getTradeChat(player))) {
                        player.sendMessage(getErrorChatColor() + "Kjøp/Salg aktivert");
                        this.userHandler.setTradeChat(player, true);
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede aktivert Kjøp/Salg chat.");
                    }
                } else {
                    if (this.userHandler.getMute(player)) {
                        player.sendMessage(ChatColor.RED + "Du kan ikke snakke siden du er muted.");
                        return true;
                    } else {
                        StringBuilder build = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            build.append(args[i] + " ");
                        }
                        this.chatHandler.sendTradeChatMessage(player, build.toString());
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
