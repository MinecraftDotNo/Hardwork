package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ChatCommand extends MinecraftnoCommand {

    @SuppressWarnings("unused")
    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public ChatCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("info"))) {
            player.sendMessage(getCommandChatColor() + "Informasjon over hva chat funksjoner er aktivert");
            if ((this.userHandler.getAccess(player) >= 3)) {
                player.sendMessage(getOkChatColor() + "Stab/Vakt chaten er " + (this.userHandler.hasAdminChatActivated(player) ? "aktivert" : "deaktivert"));
            }
            player.sendMessage(getOkChatColor() + "Auto gruppechat er " + (this.userHandler.getGroupChatBind(player) ? "aktivert" : "deaktivert"));
            player.sendMessage(getOkChatColor() + "Hovedchat er " + (this.userHandler.getHovedChat(player) ? "aktivert" : "deaktivert"));
            player.sendMessage(getOkChatColor() + "Annonsering " + (this.userHandler.getAnnonseringer(player) ? "aktivert" : "deaktivert"));
            player.sendMessage(getOkChatColor() + "Irc " + (this.userHandler.getirc(player) ? "aktivert" : "deaktivert"));
            player.sendMessage(getOkChatColor() + "Kjøp/Salg er " + (this.userHandler.getTradeChat(player) ? "aktivert" : "deaktivert"));
            player.sendMessage(getOkChatColor() + "Du er " + (this.userHandler.getMute(player) ? "muted" : "ikke muted"));
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                player.sendMessage(getOkChatColor() + "Liste over chat som kan deaktivers.");
                player.sendMessage(getInfoChatColor() + "hoved.");
                player.sendMessage(getInfoChatColor() + "trade.");
                player.sendMessage(getInfoChatColor() + "ann.");
                player.sendMessage(getInfoChatColor() + "irc.");
                player.sendMessage(getInfoChatColor() + "gruppe.");
                player.sendMessage(getOkChatColor() + "Der du skriver f.eks: /chat trade off");
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                if ((this.userHandler.getAnnonseringer(player))) {
                    this.userHandler.setAnnonseringer(player, false);
                }
                if ((this.userHandler.getTradeChat(player))) {
                    this.userHandler.setTradeChat(player, false);
                }
                if ((this.userHandler.getHovedChat(player))) {
                    this.userHandler.setHovedChat(player, false);
                }
                if ((this.userHandler.getirc(player))) {
                    this.userHandler.setirc(player, false);
                }
                player.sendMessage(getOkChatColor() + "Chat deaktivert.");
                return true;
            } else if (args[0].equalsIgnoreCase("on")) {
                if (!(this.userHandler.getAnnonseringer(player))) {
                    this.userHandler.setAnnonseringer(player, true);
                }
                if (!(this.userHandler.getTradeChat(player))) {
                    this.userHandler.setTradeChat(player, true);
                }
                if (!(this.userHandler.getHovedChat(player))) {
                    this.userHandler.setHovedChat(player, true);
                }
                if (!(this.userHandler.getirc(player))) {
                    this.userHandler.setirc(player, true);
                }
                player.sendMessage(getOkChatColor() + "Chat aktivert..");
                return true;
            } else {
                player.sendMessage(getErrorChatColor() + "Ugyldig argument");
                return false;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("hoved")) {
                if (args[1].equalsIgnoreCase("off")) {
                    if ((this.userHandler.getHovedChat(player))) {
                        this.userHandler.setHovedChat(player, false);
                        player.sendMessage(getOkChatColor() + "Hovedchat deaktivert.");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede deaktivert hovedchat.");
                    }
                } else if (args[1].equalsIgnoreCase("on")) {
                    if (!(this.userHandler.getHovedChat(player))) {
                        this.userHandler.setHovedChat(player, true);
                        player.sendMessage(getOkChatColor() + "Hovedchat aktivert..");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede aktivert hovedchat.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Ugyldig argument");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("trade")) {
                if (args[1].equalsIgnoreCase("off")) {
                    if ((this.userHandler.getTradeChat(player))) {
                        this.userHandler.setTradeChat(player, false);
                        player.sendMessage(getOkChatColor() + "Tradechat deaktivert.");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede deaktivert tradechat.");
                    }
                } else if (args[1].equalsIgnoreCase("on")) {
                    if (!(this.userHandler.getTradeChat(player))) {
                        this.userHandler.setTradeChat(player, true);
                        player.sendMessage(getOkChatColor() + "Tradechat aktivert..");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede aktivert tradechat.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Ugyldig argument");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("ann")) {
                if (args[1].equalsIgnoreCase("off")) {
                    if ((this.userHandler.getAnnonseringer(player))) {
                        this.userHandler.setAnnonseringer(player, false);
                        player.sendMessage(getOkChatColor() + "Annonseringer deaktivert.");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede deaktivert annonseringer.");
                    }
                } else if (args[1].equalsIgnoreCase("on")) {
                    if (!(this.userHandler.getAnnonseringer(player))) {
                        this.userHandler.setAnnonseringer(player, true);
                        player.sendMessage(getOkChatColor() + "Annonseringer aktivert..");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede aktivert annonseringer.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Ugyldig argument");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("irc")) {
                if (args[1].equalsIgnoreCase("off")) {
                    if (this.userHandler.getirc(player)) {
                        this.userHandler.setirc(player, false);
                        player.sendMessage(getOkChatColor() + "irc deaktivert.");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede deaktivert irc.");
                    }
                } else if (args[1].equalsIgnoreCase("on")) {
                    if (!(this.userHandler.getirc(player))) {
                        this.userHandler.setirc(player, true);
                        player.sendMessage(getOkChatColor() + "irc aktivert..");
                    } else {
                        player.sendMessage(getOkChatColor() + "Du har allerede på irc.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Ugyldig argument");
                    return false;
                }
            }
        } else if (args[0].equalsIgnoreCase("gruppe")) {
            if (args[1].equalsIgnoreCase("off")) {
                if (this.userHandler.getGroupChatBind(player)) {
                    this.userHandler.setGroupChatBind(player, false);
                    player.sendMessage(getOkChatColor() + "Auto gruppechat deaktivert.");
                } else {
                    player.sendMessage(getOkChatColor() + "Du har allerede deaktivert auto gruppechat.");
                }
            } else if (args[1].equalsIgnoreCase("on")) {
                if (!(this.userHandler.getGroupChatBind(player))) {
                    this.userHandler.setGroupChatBind(player, true);
                    player.sendMessage(getOkChatColor() + "auto gruppechat aktivert..");
                } else {
                    player.sendMessage(getOkChatColor() + "Du har allerede aktivert auto gruppechat.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Ugyldig argument");
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}