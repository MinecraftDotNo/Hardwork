package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GroupAdminCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final GroupHandler groupHandler;

    public GroupAdminCommand(Minecraftno instance) {
        super(instance);
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.groupHandler = instance.getGroupHandler();
        setAccessLevel(3);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage("---- " + getDefaultChatColor() + "Gruppesystem V2 - Admin Panel (Alt blir loggført)" + getVarChatColor() + " ----");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm søk " + getArgChatColor() + "[spiller]" + getVarChatColor() + " - Finner ut hvilken grupper spiller er i.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm søk g  " + getArgChatColor() + "[gruppenavn] eller [gruppeID]" + getVarChatColor() + " - Henter ut informasjon om gruppe.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm eier " + getArgChatColor() + "[gruppeID] [spiller]" + getVarChatColor() + " - Skifter eier på gruppe.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm join " + getArgChatColor() + "[gruppeID]" + getVarChatColor() + " - Lar deg bli med i gruppen.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm leave " + getVarChatColor() + " - Flytter deg tilbake til din opprinnelige gruppe.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm flytt " + getArgChatColor() + "[gruppeID] [spiller]" + getVarChatColor() + " - Flytter spiller til gruppe.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm slett " + getArgChatColor() + "[gruppeID]" + getVarChatColor() + " - Sletter hele gruppen.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm kick " + getArgChatColor() + "[gruppeID] [spiller]" + getVarChatColor() + " - Kicker spiller fra gruppen.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "gadm navn " + getArgChatColor() + "[gruppeID] [navn]" + getVarChatColor() + " - Bytter navn på gruppen.");
            return true;
        } else {
            if (args[0].equalsIgnoreCase("søk")) {
                if (args[1].equalsIgnoreCase("g")) {
                    if (args.length == 3) {
                        int groupID = 0;
                        if (isIntNumber(args[2])) {
                            groupID = Integer.parseInt(args[2]);
                            List<String> gInfo = this.groupHandler.getGroupInformation(groupID);
                            List<Integer> gMembers = this.groupHandler.getGroupMembers(groupID);
                            StringBuilder medlemmer = new StringBuilder();
                            for (Integer member : gMembers) {
                                medlemmer.append(this.userHandler.getNameFromId(member) + ", ");
                            }
                            String utskrift = medlemmer.substring(0, medlemmer.length() - 2) + ".";
                            player.sendMessage(getDefaultChatColor() + "-------- Gruppeinfo for: " + getVarChatColor() + gInfo.get(0) + getDefaultChatColor() + " --------");
                            player.sendMessage(getDefaultChatColor() + "ID: " + getVarChatColor() + groupID);
                            player.sendMessage(getDefaultChatColor() + "Eier: " + getVarChatColor() + gInfo.get(1));
                            player.sendMessage(getDefaultChatColor() + "Brukere: " + getVarChatColor() + utskrift);
                            player.sendMessage(getDefaultChatColor() + "Antall spillere: " + getVarChatColor() + gMembers.size() + getDefaultChatColor() + ".");
                        } else {
                            groupID = this.groupHandler.getGroupIDFromGroupName(args[2]);
                            List<String> gInfo = this.groupHandler.getGroupInformation(groupID);
                            List<Integer> gMembers = this.groupHandler.getGroupMembers(groupID);
                            StringBuilder medlemmer = new StringBuilder();
                            for (Integer member : gMembers) {
                                medlemmer.append(this.userHandler.getNameFromId(member) + ", ");
                            }
                            String utskrift = medlemmer.substring(0, medlemmer.length() - 2) + ".";
                            player.sendMessage(getDefaultChatColor() + "-------- Gruppeinfo for: " + getVarChatColor() + gInfo.get(0) + getDefaultChatColor() + " --------");
                            player.sendMessage(getDefaultChatColor() + "ID: " + getVarChatColor() + groupID);
                            player.sendMessage(getDefaultChatColor() + "Eier: " + getVarChatColor() + gInfo.get(1));
                            player.sendMessage(getDefaultChatColor() + "Brukere: " + getVarChatColor() + utskrift);
                            player.sendMessage(getDefaultChatColor() + "Antall spillere: " + getVarChatColor() + gMembers.size() + getDefaultChatColor() + ".");
                        }
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du må angi en gruppe du skal finne.");
                        return true;
                    }
                } else {
                    if (args.length == 2) {
                        String toSearch = args[1];
                        ArrayList<String> result = this.groupHandler.admGroupSearchPlayer(toSearch);
                        if (!result.isEmpty()) {
                            player.sendMessage(getDefaultChatColor() + "------------- Resultat ---------------");
                            player.sendMessage(getDefaultChatColor() + "Gruppe: " + getVarChatColor() + result.get(1));
                            player.sendMessage(getDefaultChatColor() + "GruppeID: " + getVarChatColor() + result.get(2));
                            player.sendMessage(getDefaultChatColor() + "------------- Slutt ------------------");
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "Søket retunerte enten tomt eller flere spillere med samme navn var funnet.");
                            return true;
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du må angi en spiller du skal finne.");
                        return true;
                    }
                }
            } else if (args[0].equalsIgnoreCase("eier")) {
                if (args.length == 3) {
                    try {
                        int groupID = Integer.parseInt(args[1]);
                        String newOwner = args[2];
                        switch (this.groupHandler.admUpdateGroupOwner(player, groupID, newOwner)) {
                            case 1:
                                player.sendMessage(getErrorChatColor() + "Den angitte brukeren eksisterer ikke.");
                                break;
                            case 2:
                                player.sendMessage(getErrorChatColor() + "Den angitte gruppen eksisterer ikke.");
                                break;
                            case 3:
                                player.sendMessage(getErrorChatColor() + "Spilleren befinner seg ikke i samme gruppe som den angitte gruppeID-en.");
                                break;
                            case 4:
                                player.sendMessage(getOkChatColor() + "Gruppen har nå skiftet eier.");
                                break;
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                        return true;
                    }
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor() + "Du må angi en gruppeID og den nye eieren sitt navn.");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                if (args.length == 2) {
                    try {
                        int groupID = Integer.parseInt(args[1]);
                        switch (this.groupHandler.admJoinGroup(groupID, player)) {
                            case 1:
                                player.sendMessage(getErrorChatColor() + "Den angitte gruppen eksiterer ikke.");
                                break;
                            case 2:
                                player.sendMessage(getOkChatColor() + "Du har nå force-joinet en annen gruppe.");
                                player.sendMessage(getErrorChatColor() + "HUSK: Dersom du logger ut må du forlate denne gruppe igjen og bruke /gadm join til din gamle gruppe.");
                                break;
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                        return true;
                    }
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor() + "Du må angi en gruppeID");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                switch (this.groupHandler.admGoBackToGroup(player)) {
                    case 1:
                        player.sendMessage(getErrorChatColor() + "Du har ikke noe gruppe å gå tilbake til.");
                        break;
                    case 2:
                        player.sendMessage(getErrorChatColor() + "Din gamle gruppe eksiterer ikke.");
                        break;
                    case 3:
                        player.sendMessage(getOkChatColor() + "Du har nå gått tilbake til din gamle gruppe.");
                        break;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("flytt")) {
                if (args.length == 3) {
                    try {
                        int groupID = Integer.parseInt(args[1]);
                        String thePlayer = args[2];
                        switch (this.groupHandler.admMovePlayer(groupID, player, thePlayer)) {
                            case 1:
                                player.sendMessage(getErrorChatColor() + "Spilleren eksiterer ikke.");
                                break;
                            case 2:
                                player.sendMessage(getErrorChatColor() + "Den nye gruppen eksiterer ikke.");
                                break;
                            case 3:
                                player.sendMessage(getErrorChatColor() + "Spilleren eier allerede en annen gruppe og må flyttes først.");
                                break;
                            case 4:
                                player.sendMessage(getOkChatColor() + "Spilleren ble flyttet.");
                                break;
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                        return true;
                    }
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor() + "Du må angi en gruppeID og spilleren som skal flyttes.");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("slett")) {
                if (args.length == 2) {
                    try {
                        int groupID = Integer.parseInt(args[1]);
                        switch (this.groupHandler.admDeleteGroup(player, groupID)) {
                            case 1:
                                player.sendMessage(getErrorChatColor() + "Gruppen eksisterer ikke.");
                                break;
                            case 2:
                                player.sendMessage(getOkChatColor() + "Gruppen har nå blitt slettet.");
                                break;
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                        return true;
                    }
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("kick")) {
                return this.kickPlayerCommand(player, args);
            } else if (args[0].equalsIgnoreCase("navn")) {
                if (args.length == 3) {
                    try {
                        int groupID = Integer.parseInt(args[1]);
                        String newName = args[2];
                        switch (this.groupHandler.admRenameGroup(player, groupID, newName)) {
                            case 1:
                                player.sendMessage(getErrorChatColor() + "Den angitte gruppen eksiterer ikke.");
                                break;
                            case 2:
                                player.sendMessage(getOkChatColor() + "Gruppen har nå fått nytt navn.");
                                break;
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the kick part of gadm
     *
     * @param player The player executing the command
     * @param args   Arguments issued with the command
     *
     * @return true
     */
    public boolean kickPlayerCommand(Player player, String[] args) {
        if (args.length == 3) {
            if (isIntNumber(args[1])) {
                int groupId = Integer.parseInt(args[1]);
                String victim = args[2];

                switch (this.groupHandler.admKickMember(groupId, player, args[2])) {
                    case 0:
                        player.sendMessage(getErrorChatColor() + "En feil har oppstått. Mest sannsynlig feil i spørring. Kontakt utvikler.");
                        break;
                    case 1:
                        player.sendMessage(getErrorChatColor() + "Spilleren eksisterer ikke.");
                        break;
                    case 2:
                        player.sendMessage(getErrorChatColor() + "Gruppen eksisterer ikke.");
                        break;
                    case 3:
                        player.sendMessage(getErrorChatColor() + "Spilleren er ikke i en gruppe.");
                        break;
                    case 4:
                        player.sendMessage(getErrorChatColor() + "Spillerens gruppeid stemmer ikke overens med gruppeiden som ble skrevet inn.");
                        break;
                    case 5:
                        player.sendMessage(getOkChatColor() + "Spilleren ble kicket.");
                        break;
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Gruppeid må være en tallverdi.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Du må angi gruppeid og spiller.");
        }

        return true;
    }

    /**
     * Checks if the given string is a valid integer
     *
     * @param number
     *
     * @return true if integer, false otherwise
     */
    public boolean isIntNumber(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
