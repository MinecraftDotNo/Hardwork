package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarningHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

public class WarningCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;
    private final WarningHandler warningHandler;

    public WarningCommand(Minecraftno instance) {
        super(instance);
        this.userHandler = instance.getUserHandler();
        this.warningHandler = instance.getWarningHandler();
        setAccessLevel(3);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player victim = this.plugin.playerMatch(args[0]);
            if (victim != null) {
                List<String> warningList = this.warningHandler.getWarnings(victim.getName());
                if (warningList == null) {
                    player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                    return true;
                } else {
                    if (!(warningList.size() > 4)) {
                        for (String s : warningList) {
                            player.sendMessage(s);
                        }
                    } else {
                        for (String s : warningList) {
                            player.sendMessage(s);
                            if (warningList.get(3).equals(s)) {
                                break;
                            }
                        }
                        player.sendMessage(getErrorChatColor() + "Bruk: /warning playername 2 for sjå flere advarsler.");
                    }
                }
            } else {
                String playerName = this.userHandler.getUsernameFromDB(args[0]);
                if (!playerName.isEmpty()) {
                    List<String> warningList = this.warningHandler.getWarnings(playerName);
                    if (warningList == null) {
                        player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                        return true;
                    } else {
                        if (warningList.size() <= 4) {
                            for (String s : warningList) {
                                player.sendMessage(s);
                            }
                        } else {
                            for (String s : warningList) {
                                player.sendMessage(s);
                                if (warningList.get(3).equals(s)) {
                                    break;
                                }
                            }
                            player.sendMessage(getErrorChatColor() + "Bruk: /warning playername 2 for sjå flere advarsler.");
                        }
                    }
                }
            }
        } else if (args.length == 2) {
            if (!canParse(args[1])) {
                this.invalidInt(player, args[1]);
                return false;
            }
            Player victim = this.plugin.playerMatch(args[0]);
            if (victim != null) {
                List<String> warningList = this.warningHandler.getWarnings(victim.getName());
                if (warningList == null) {
                    player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                    return true;
                } else {
                    if (args[1].equals(0)) {
                        return false;
                    } else if (args[1].equals(1)) {
                        for (String s : warningList) {
                            player.sendMessage(s);
                            if (warningList.get(3).equals(s)) {
                                break;
                            }
                        }
                        player.sendMessage(getErrorChatColor() + "Bruk: /warning playername " + 2 + " for sjå flere advarsler.");
                    } else {
                        for (int i = (3 + (4 * (Integer.parseInt(args[1]) - 2))); i < (3 + (4 * (Integer.parseInt(args[1]) - 1))); i++) {
                            player.sendMessage(warningList.get(i));
                        }
                        player.sendMessage(getErrorChatColor() + "Bruk: /warning playername " + Integer.parseInt(args[1] + 1) + " for sjå flere advarsler.");
                    }
                }
            } else {
                String playerName = this.userHandler.getUsernameFromDB(args[0]);
                if (!playerName.isEmpty()) {
                    List<String> warningList = this.warningHandler.getWarnings(playerName);
                    if (warningList == null) {
                        player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                        return true;
                    } else {
                        if (args[1].equals(0)) {
                            return false;
                        } else if (args[1].equals(1)) {
                            for (String s : warningList) {
                                player.sendMessage(s);
                                if (warningList.get(3).equals(s)) {
                                    break;
                                }
                            }
                        } else {
                            for (int i = (3 + (4 * (Integer.parseInt(args[1]) - 2))); i < (3 + (4 * (Integer.parseInt(args[1]) - 1))); i++) {
                                player.sendMessage(warningList.get(i));
                            }
                            player.sendMessage(getErrorChatColor() + "Bruk: /warning playername " + Integer.parseInt(args[1] + 1) + " for sjå flere advarsler.");
                        }
                    }
                }
            }
        } else if (args.length == 3) {
            if (canParse(args[2])) {
                if (args[0].equalsIgnoreCase("remove")) {
                    Player victim = this.plugin.playerMatch(args[1]);

                    if (victim != null) {
                        this.removeWarningFromPlayer(player, victim.getName(), Integer.parseInt(args[2]));
                    } else {
                        String playerName = this.userHandler.getUsernameFromDB(args[1]);
                        if (!playerName.isEmpty()) {
                            this.removeWarningFromPlayer(player, playerName, Integer.parseInt(args[2]));
                        }
                    }
                }
            } else {
                this.invalidInt(player, args[2]);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Handles the remove part of warningCommand
     *
     * @param player    Player issuing the command
     * @param userName  Username of the victim
     * @param warningId Id of warning
     */
    private void removeWarningFromPlayer(Player player, String userName, int warningId) {
        List<String> arr = this.warningHandler.getWarning(userName, warningId);
        if (arr != null) {
            if (this.warningHandler.delWarning(warningId, userName)) {
                player.sendMessage("Du fjernet advarselen med id " + warningId + " fra spiller " + userName);
                this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " fjernet en advarsel fra " + arr.get(1) + ". Info om advarsel - Id: " + arr.get(0) + ". Gitt av: " + arr.get(2) + ". Grunnlag: " + arr.get(3) + ". X: " + arr.get(4) + ". Y: " + arr.get(5) + ". Z: " + arr.get(6) + ". World: " + arr.get(7) + ". Tidspunkt advarselen ble satt: " + arr.get(8) + ".");
            } else {
                player.sendMessage(getErrorChatColor() + "Advarselen ble ikke fjernet.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Fant ikke en advarsel som samsvarte med id og spillernavn.");
        }
    }

    private final void invalidInt(Player p, String i) {
        p.sendMessage(getErrorChatColor() + "Ikke et tall: " + i);
    }

    public final static boolean canParse(String i) {
        try {
            Integer.valueOf(Integer.parseInt(i));
            return true;
        } catch (NumberFormatException ne) {

        }
        return false;
    }
}