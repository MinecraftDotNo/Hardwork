package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.ChatHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ReplayCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final ChatHandler chatHandler;

    public ReplayCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
        this.userHandler = instance.getUserHandler();
        this.chatHandler = instance.getChatHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (!this.userHandler.isRegPlayer(player)) {
                if (args[0].equalsIgnoreCase("l")) {
                    if (this.userHandler.getLastMsgSend(player) != null) {
                        Player SendTo = this.plugin.playerMatch(this.userHandler.getLastMsgSend(player));
                        if (SendTo != null) {
                            if (SendTo.isOnline()) {
                                StringBuilder build = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    build.append(args[i] + " ");
                                }
                                String msg = build.toString().trim();
                                this.chatHandler.sendPrivatMessage(player, SendTo, msg);
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("lock")) {
                    if (this.userHandler.getLastMsgGet(player) != null) {
                        String SendToGetName = this.userHandler.getLastMsgGet(player);
                        this.userHandler.setLastMsgLock(player, SendToGetName);
                        player.sendMessage(getErrorChatColor() + "Du har nå låst chat til " + SendToGetName);
                    }
                } else if (args[0].equalsIgnoreCase("unlock")) {
                    if (this.userHandler.getLastMsgGet(player) != null) {
                        this.userHandler.getPlayerData(player).setlastmsgLock(null);
                    }
                } else {
                    if (this.userHandler.getLastMsgGet(player) != null) {
                        Player SendToGet = this.plugin.playerMatch(this.userHandler.getLastMsgGet(player));
                        if (SendToGet != null) {
                            StringBuilder build = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                build.append(args[i] + " ");
                            }
                            String msg = build.toString().trim();
                            this.chatHandler.sendPrivatMessage(player, SendToGet, msg);
                        } else {
                            player.sendMessage(getErrorChatColor() + " Brukeren er ikke pålogget.");
                            return true;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
} 