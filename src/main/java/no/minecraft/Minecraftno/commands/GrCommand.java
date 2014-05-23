package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.GroupHandler.OptionColumns;
import no.minecraft.Minecraftno.handlers.data.GroupData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

public class GrCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final GroupHandler groupHandler;

    public GrCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.groupHandler = instance.getGroupHandler();
        this.plugin = instance;
    }

    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        List<String> whiteListCommands = new ArrayList<String>(Arrays.asList("ny", "liste", "invitasjoner", "godta", "avslå")); /* Kommandoer som kan
                                                                                                                            */
        if (args.length == 0 || (args[0].equalsIgnoreCase("hjelp") && args.length >= 1 && args.length <= 2)) {
            List<String> commands = this.groupHandler.getHelpList(player);
            try {
                int antallPerSide = 10;
                int page = 1;
                if ((args.length == 2)) {
                    page = Integer.parseInt(args[1]);
                }
                player.sendMessage("--------- " + getDefaultChatColor() + "Gruppesystem V2" + getVarChatColor() + " --------");
                final int startpos = (page - 1) * antallPerSide;
                if (page > 0 && startpos <= commands.size() - 1) {
                    final int stoppos = startpos + antallPerSide >= commands.size() ? commands.size() - 1 : startpos + antallPerSide - 1;
                    final int numberOfPages = (int) Math.ceil(commands.size() / (double) antallPerSide);
                    if (numberOfPages > 1) {
                        player.sendMessage(getDefaultChatColor() + "Side " + getVarChatColor() + page + getDefaultChatColor() + " av " + getVarChatColor() + numberOfPages + getDefaultChatColor() + " - totalt " + getVarChatColor() + commands.size() + getDefaultChatColor() + " kommandoer");
                    }
                    for (int i = startpos; i <= stoppos; i++) {
                        player.sendMessage(commands.get(i));
                    }
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor() + "Siden '" + page + "' finnes ikke");
                    return true;
                }
            } catch (NumberFormatException ex) {
                player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                return true;
            }
        } else {
            if (!groupHandler.isInGroup(player) && !whiteListCommands.contains(args[0].toLowerCase())) {
                player.sendMessage(getErrorChatColor() + "Du må være i en gruppe for å utføre denne kommandoen.");
                return true;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("ny")) {
                if (args[1].length() > 20) {
                    player.sendMessage(getErrorChatColor() + "Forlangt navn");
                    return true;
                }

                if (this.groupHandler.isInGroup(player)) {
                    player.sendMessage(getErrorChatColor() + "Du er allerede i en gruppe.");
                    return true;
                }
                String groupName = args[1];
                try {
                    if (this.groupHandler.createGroup(groupName, player)) {
                        player.sendMessage(getOkChatColor() + "Du opprettet gruppen: " + getVarChatColor() + groupName);
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if ((args.length >= 1 && args.length <= 2 && args[0].equalsIgnoreCase("liste"))) {
                try {
                    int antallPerSide = 10;
                    int page = 1;
                    if ((args.length >= 2)) {
                        page = Integer.parseInt(args[1]);
                    }

                    List<ArrayList<String>> publicGroups = this.groupHandler.getListGroups();
                    if (publicGroups.size() == 0) {
                        player.sendMessage(getDefaultChatColor() + "Ingen offentlige grupper.");
                        return true;
                    }
                    player.sendMessage("--------- " + getDefaultChatColor() + "Offentlige grupper" + getVarChatColor() + " --------");
                    final int startpos = (page - 1) * antallPerSide;
                    if (page > 0 && startpos <= publicGroups.size() - 1) {
                        final int stoppos = startpos + antallPerSide >= publicGroups.size() ? publicGroups.size() - 1 : startpos + antallPerSide - 1;
                        final int numberOfPages = (int) Math.ceil(publicGroups.size() / (double) antallPerSide);
                        if (numberOfPages != 1) {
                            player.sendMessage(getDefaultChatColor() + "Side " + getVarChatColor() + page + getDefaultChatColor() + " av " + getVarChatColor() + numberOfPages + getDefaultChatColor() + " - totalt " + getVarChatColor() + publicGroups.size() + getDefaultChatColor() + " offentlig" + (publicGroups.size() > 1 ? "e gruppe!" : " gruppe!"));
                        }
                        for (int i = startpos; i <= stoppos; i++) {
                            player.sendMessage(getDefaultChatColor() + "Gruppe: " + getVarChatColor() + publicGroups.get(i).get(0) + getDefaultChatColor() + " | eier: " + getVarChatColor() + publicGroups.get(i).get(1));
                        }
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "Siden '" + page + "' finnes ikke");
                        return true;
                    }
                } catch (NumberFormatException ex) {
                    player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                    return true;
                }
            } else if (args.length >= 1 && args.length <= 2 && args[0].equalsIgnoreCase("invitasjoner")) {
                try {
                    int antallPerSide = 10;
                    int page = 1;
                    if ((args.length >= 2)) {
                        page = Integer.parseInt(args[1]);
                    }

                    List<ArrayList<String>> invitasjoner = this.groupHandler.getInvites(player);
                    if (invitasjoner.size() == 0) {
                        player.sendMessage(getDefaultChatColor() + "Du har ingen invitasjoner.");
                        return true;
                    }
                    final int startpos = (page - 1) * antallPerSide;
                    if (page > 0 && startpos <= invitasjoner.size() - 1) {
                        final int stoppos = startpos + antallPerSide >= invitasjoner.size() ? invitasjoner.size() - 1 : startpos + antallPerSide - 1;
                        final int numberOfPages = (int) Math.ceil(invitasjoner.size() / (double) antallPerSide);
                        if (numberOfPages != 1) {
                            player.sendMessage(getDefaultChatColor() + "Side " + getVarChatColor() + page + getDefaultChatColor() + " av " + getVarChatColor() + numberOfPages + getDefaultChatColor() + " - totalt " + getVarChatColor() + invitasjoner.size() + getDefaultChatColor() + " invitasjon" + (invitasjoner.size() > 1 ? "er!" : "!"));
                        }
                        for (int i = startpos; i <= stoppos; i++) {
                            player.sendMessage(getDefaultChatColor() + "Fra: " + getVarChatColor() + invitasjoner.get(i).get(0) + getDefaultChatColor() + " | gruppe: " + getVarChatColor() + invitasjoner.get(i).get(1) + getDefaultChatColor() + " | id: " + getVarChatColor() + invitasjoner.get(i).get(2));
                        }
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "Siden '" + page + "' finnes ikke");
                        return true;
                    }
                } catch (NumberFormatException ex) {
                    player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("godta")) {
                if (this.groupHandler.isInGroup(player)) {
                    player.sendMessage(getErrorChatColor() + "Du er allerede i en gruppe.");
                    return true;
                }
                if (args.length == 2) {
                    try {
                        int groupID = Integer.parseInt(args[1]);
                        switch (this.groupHandler.acceptInvite(player, groupID)) {
                            case 1:
                                player.sendMessage(getErrorChatColor() + "Den gruppen eksiterer ikke.");
                                break;
                            case 2:
                                player.sendMessage(getErrorChatColor() + "Du har ikke invitasjon til den gruppen.");
                                break;
                            case 3:
                                player.sendMessage(getOkChatColor() + "Du ble med i gruppen: " + this.groupHandler.getGroupNameFromID(groupID));
                                break;
                        }
                        return true;
                    } catch (NumberFormatException ex) {
                        player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                        return true;
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Korrekt måte å bruke den på: " + getDefaultChatColor() + "/" + getCommandChatColor() + "gr godta " + getArgChatColor() + "[id]");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("avslå")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("alle")) {
                        if (this.groupHandler.denyAllInvites(player)) {
                            player.sendMessage(getOkChatColor() + "Alle dine invitasjoner ble slettet!");
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "En feil oppstod, kontakt stab.");
                            return true;
                        }
                    } else {
                        try {
                            int groupID = Integer.parseInt(args[1]);
                            if (this.groupHandler.denyInvite(player, groupID)) {
                                player.sendMessage(getOkChatColor() + "Invitasjonen ble slettet.");
                                return true;
                            } else {
                                player.sendMessage(getErrorChatColor() + "En feil oppstod, kontakt stab.");
                                return true;
                            }
                        } catch (NumberFormatException ex) {
                            player.sendMessage(getErrorChatColor() + "Ugyldig tall!");
                            return true;
                        }
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Korrekt måte å bruke den på: " + getDefaultChatColor() + "/" + getCommandChatColor() + "gr avslå " + getArgChatColor() + "[id] eller [alle]");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("info")) {
                int groupID = this.groupHandler.getGroupID(player);
                GroupData grData = this.groupHandler.getGroupData(groupID);
                List<Integer> groupMembers = this.groupHandler.getGroupMembers(groupID);
                StringBuilder medlemmer = new StringBuilder();
                for (Integer member : groupMembers) {
                    medlemmer.append(this.userHandler.getNameFromId(member) + ", ");
                }
                String utskrift = medlemmer.substring(0, medlemmer.length() - 2) + ".";
                player.sendMessage(getDefaultChatColor() + "-------- Gruppeinfo for: " + getVarChatColor() + grData.getGroupName() + getDefaultChatColor() + " --------");
                player.sendMessage(getDefaultChatColor() + "ID: " + getVarChatColor() + groupID);
                player.sendMessage(getDefaultChatColor() + "Eier: " + getVarChatColor() + grData.getGroupOwner());
                player.sendMessage(getDefaultChatColor() + "Brukere: " + getVarChatColor() + utskrift);
                player.sendMessage(getDefaultChatColor() + "Antall spillere: " + getVarChatColor() + groupMembers.size() + getDefaultChatColor() + ".");
                return true;
            } else if (args[0].equalsIgnoreCase("info2")) {
                GroupData grData = this.groupHandler.getGroupData(this.groupHandler.getGroupID(player));
                player.sendMessage(getDefaultChatColor() + "======= " + getVarChatColor() + "Instillinger" + getDefaultChatColor() + " =======");
                player.sendMessage(getDefaultChatColor() + "Gruppen er: " + getVarChatColor() + (grData.isGroupPublic() ? "offentlig" : "ikke offentlig"));
                player.sendMessage(getDefaultChatColor() + "Kun eier kan invitere: " + getVarChatColor() + (grData.getPublicInviteOn() ? "nei" : "ja"));
                player.sendMessage(getDefaultChatColor() + "Gruppebank er " + getVarChatColor() + (grData.isBankOn() ? "påslått" : "avslått"));
                player.sendMessage(getDefaultChatColor() + "======= " + getVarChatColor() + "Info" + getDefaultChatColor() + " =======");
                player.sendMessage(getDefaultChatColor() + "Gruppen har: " + getVarChatColor() + grData.getBank() + getDefaultChatColor() + " gull på konto");
                player.sendMessage(getDefaultChatColor() + "Eier av gruppen er: " + getVarChatColor() + grData.getGroupOwner());
                return true;
            } else if (args[0].equalsIgnoreCase("inv")) {
                if (args.length == 1) {
                    player.sendMessage(getErrorChatColor() + "Du må angi en spiller du skal invitere.");
                    return true;
                }
                if (!this.groupHandler.getGroupData(this.groupHandler.getGroupID(player)).getPublicInviteOn() && !this.groupHandler.getGroupData(this.groupHandler.getGroupID(player)).getGroupOwner().equalsIgnoreCase(player.getName())) {
                    player.sendMessage(getErrorChatColor() + "Kun eier kan invitere spillere.");
                    return true;
                } else {
                    switch (this.groupHandler.sendInvite(player, args[1])) {
                        case 1:
                            player.sendMessage(getOkChatColor() + "Denne spilleren har allerede invitasjon til denne gruppen.");
                            break;
                        case 2:
                            player.sendMessage(getErrorChatColor() + "Denne spilleren er ikke en del av denne serveren.");
                            break;
                        case 3:
                            player.sendMessage(getOkChatColor() + "Spilleren fikk invitasjon og er pålogget.");
                            break;
                        case 4:
                            player.sendMessage(getOkChatColor() + "Spilleren vil motta invitasjonen når han eller hun logger på.");
                            break;
                    }
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("uninv")) {
                if (args.length != 2) {
                    return false;
                }

                int groupId = this.groupHandler.getGroupID(player);

                if (!this.groupHandler.getGroupData(groupId).getPublicInviteOn() && !this.groupHandler.getGroupData(groupId).getGroupOwner().equalsIgnoreCase(player.getName())) {
                    player.sendMessage(getErrorChatColor() + "Du kan ikke fjerne invitasjoner i denne gruppen.");
                    return true;
                }

                if (!this.groupHandler.checkInviteExist(groupId, args[1])) {
                    player.sendMessage(getErrorChatColor() + "Spilleren \"" + args[1] + "\" er ikke invitert til " + this.groupHandler.getGroupNameFromID(groupId) + ".");
                    return true;
                }

                if (!this.groupHandler.denyInvite(args[1], groupId)) {
                    player.sendMessage(getErrorChatColor() + "Noe gikk galt :(");
                    return true;
                }

                player.sendMessage(getOkChatColor() + "Invitasjonen ble slettet.");
                return true;
            } else if (args[0].equalsIgnoreCase("invlist")) {
                if (args.length != 1) {
                    return false;
                }

                int groupId = this.groupHandler.getGroupID(player);

                Map<String, String> invites = this.groupHandler.getInvites(groupId);

                player.sendMessage(getInfoChatColor() + " === Invitasjoner for " + this.groupHandler.getGroupNameFromID(groupId) + " (" + invites.size() + " stk.) ===");

                for (String invited : invites.keySet()) {
                    player.sendMessage(getDefaultChatColor() + " - " + invited + ", invitert av " + invites.get(invited));
                }

                return true;
            } else if (args[0].equalsIgnoreCase("forlat")) {
                int groupID = this.groupHandler.getGroupID(player);
                if (!this.groupHandler.getGroupData(groupID).getGroupOwner().equalsIgnoreCase(player.getName())) {
                    if (this.groupHandler.leaveGroup(player)) {
                        player.sendMessage(getOkChatColor() + "Du forlot gruppen.");
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "En feil oppstod, kontakt stab.");
                        return true;
                    }
                } else {
                    if (this.groupHandler.getUserCount(groupID) == 1) {
                        this.groupHandler.deleteGroup(player);
                        player.sendMessage(getDefaultChatColor() + "Du var siste spiller i gruppen og den ble derfor slettet.");
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du er eier av gruppen og kan ikke forlate den");
                        player.sendMessage(getErrorChatColor() + "uten å sette en ny eier av gruppen. " + getDefaultChatColor() + "/" + getCommandChatColor() + "gr eier " + getArgChatColor() + "[spiller]");
                        return true;
                    }
                }
            } else if (args[0].equalsIgnoreCase("who")) {
                int size = 0;
                StringBuilder onlineMedlemmer = new StringBuilder();
                for (Player vplayer : this.plugin.getServer().getOnlinePlayers()) {
                    if (this.groupHandler.getGroupID(vplayer) == this.groupHandler.getGroupID(player)) {
                        onlineMedlemmer.append(vplayer.getName() + ", ");
                        size++;
                    }
                }
                String utskrift = onlineMedlemmer.substring(0, onlineMedlemmer.length() - 2) + ".";
                player.sendMessage(getDefaultChatColor() + "Påloggede brukere: " + getVarChatColor() + utskrift);
                player.sendMessage(getDefaultChatColor() + "Antall spillere: " + getVarChatColor() + size + getDefaultChatColor() + ".");
                return true;
            } else if (args[0].equalsIgnoreCase("loc")) {
                if (args.length == 2) {
                    Player victim = this.plugin.playerMatch(args[1]);
                    if (victim != null) {
                        if (this.groupHandler.getGroupID(player) == this.groupHandler.getGroupID(victim)) {
                            int x = (int) victim.getLocation().getX();
                            int y = (int) victim.getLocation().getY();
                            int z = (int) victim.getLocation().getZ();

                            player.sendMessage(victim.getDisplayName() + getDefaultChatColor() + " befinner seg på: X: " + getVarChatColor() + x + getDefaultChatColor() + ", Y: " + getVarChatColor() + y + getDefaultChatColor() + ", Z: " + getVarChatColor() + z + getDefaultChatColor() + ".");
                        } else {
                            player.sendMessage(getErrorChatColor() + "Spilleren må være i samme gruppe som deg.");
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Spilleren er ikke online på serveren nå.");
                    }
                } else if (args.length == 1) {
                    player.sendMessage(getErrorChatColor() + "Du må angi en spiller du skal finne lokasjonen for.");
                } else {
                    player.sendMessage(getErrorChatColor() + "For mange eller for få argumenter.");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("kick")) {
                if (args.length == 1) {
                    player.sendMessage(getErrorChatColor() + "Du må angi en spiller du skal kicke.");
                    return true;
                }
                if (!this.groupHandler.getGroupData(this.groupHandler.getGroupID(player)).getGroupOwner().equalsIgnoreCase(player.getName())) {
                    player.sendMessage(getErrorChatColor() + "Du må være eier av gruppen for å kicke.");
                    return true;
                }
                switch (this.groupHandler.kickMember(player, args[1])) {
                    case 1:
                        player.sendMessage(getErrorChatColor() + "Spilleren ble ikke funnet eller det er flere spillere med like navn.");
                        player.sendMessage(getErrorChatColor() + "Prøv å spesifiser navnet mer.");
                        break;
                    case 2:
                        player.sendMessage(getErrorChatColor() + "Spilleren er ikke i din gruppe.");
                        break;
                    case 3:
                        player.sendMessage(getOkChatColor() + "Spilleren ble fjernet fra gruppen.");
                        Player kickedPlayer = this.plugin.playerMatch(args[1]);
                        if (kickedPlayer != null) {
                            kickedPlayer.sendMessage(getErrorChatColor() + "Du ble fjernet fra gruppen du var i.");
                        }
                        break;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("eier")) {
                if (args.length == 1) {
                    player.sendMessage(getErrorChatColor() + "Du må angi en spiller som skal være nye eier.");
                    return true;
                }
                if (!this.groupHandler.getGroupData(this.groupHandler.getGroupID(player)).getGroupOwner().equalsIgnoreCase(player.getName())) {
                    player.sendMessage(getErrorChatColor() + "Du må være eier av gruppen for å kunne skifte eier.");
                    return true;
                }
                switch (this.groupHandler.updateOwner(player, args[1])) {
                    case 1:
                        player.sendMessage(getErrorChatColor() + "Spilleren ble ikke funnet eller det er flere spillere med like navn.");
                        player.sendMessage(getErrorChatColor() + "Prøv å spesifiser navnet mer.");
                        break;
                    case 2:
                        player.sendMessage(getErrorChatColor() + "Spilleren er ikke i din gruppe.");
                        break;
                    case 3:
                        player.sendMessage(getOkChatColor() + "Ny eier i gruppen skiftet.");
                        Player newOwner = this.plugin.playerMatch(args[1]);
                        if (newOwner != null) {
                            newOwner.sendMessage(getOkChatColor() + "Du er nå eier av gruppen.");
                        }
                        break;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("mod")) {
                if (args.length > 1) {
                    if (!this.groupHandler.getGroupData(this.groupHandler.getGroupID(player)).getGroupOwner().equalsIgnoreCase(player.getName())) {
                        player.sendMessage(getErrorChatColor() + "Du må være eier av gruppen å kunne bruke denne kommandoen.");
                        return true;
                    }
                    Set<String> allowedEdits = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                    allowedEdits.addAll(Arrays.asList("bankOn", "publicOn", "inviteOn", "farge"));
                    StringBuilder tehValues = new StringBuilder();
                    for (String option : allowedEdits) {
                        tehValues.append(getArgChatColor() + option + getVarChatColor() + ", ");
                    }
                    if (args[1].equalsIgnoreCase("list")) {
                        player.sendMessage(getDefaultChatColor() + "Du kan endre på: " + tehValues.toString());
                        return true;
                    } else if (args[1].equalsIgnoreCase("info")) {
                        if (args.length == 3) {
                            if (args[2].equalsIgnoreCase("bankOn")) {
                                player.sendMessage(getDefaultChatColor() + "Hvis denne er på er gruppebank påslått.");
                                return true;
                            } else if (args[2].equalsIgnoreCase("publicOn")) {
                                player.sendMessage(getDefaultChatColor() + "Hvis denne er på vil gruppen havne i kommandoen: /gr liste.");
                                return true;
                            } else if (args[2].equalsIgnoreCase("inviteOn")) {
                                player.sendMessage(getDefaultChatColor() + "Hvis denne er på vil alle i gruppen kunne invitere.");
                                return true;
                            } else if (args[2].equalsIgnoreCase("farge")) {
                                player.sendMessage(getDefaultChatColor() + "Endrer farge i gruppechaten til det du angir.");
                                StringBuilder colors = new StringBuilder();
                                for (ChatColor color : ChatColor.values()) {
                                    colors.append(getArgChatColor() + color.name().toLowerCase() + getVarChatColor() + ", ");
                                }
                                player.sendMessage(getDefaultChatColor() + "Gyldige Farger er: " + colors.toString());
                                return true;
                            } else {
                                player.sendMessage(getErrorChatColor() + "Innstillingen finnes ikke.");
                                return true;
                            }
                        } else {
                            player.sendMessage(getErrorChatColor() + "Du må angi hva du skal ha informasjon om, sjekk /gr mod list");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("inst")) {
                        if (args.length == 4) {
                            boolean enabled = false;
                            if (allowedEdits.contains(args[2].toLowerCase())) {
                                if (!args[2].equalsIgnoreCase("farge")) {
                                    if (args[3].equalsIgnoreCase("på")) {
                                        enabled = true;
                                    } else if (args[3].equalsIgnoreCase("av")) {
                                        enabled = false;
                                    } else {
                                        player.sendMessage(getErrorChatColor() + "Gyldige verdier for om den skal være på eller av er: på / av");
                                        return true;
                                    }
                                }

                                int groupID = this.groupHandler.getGroupID(player);
                                if (args[2].equalsIgnoreCase("bankOn")) {
                                    if (this.groupHandler.editGroupOption(groupID, OptionColumns.bankOn, enabled)) {
                                        player.sendMessage(getOkChatColor() + "Innstillingen ble endret og lagret.");
                                        return true;
                                    } else {
                                        player.sendMessage(getErrorChatColor() + "En feil oppstod, kontakt stab.");
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("publicOn")) {
                                    if (this.groupHandler.editGroupOption(groupID, OptionColumns.publicOn, enabled)) {
                                        player.sendMessage(getOkChatColor() + "Innstillingen ble endret og lagret.");
                                        return true;
                                    } else {
                                        player.sendMessage(getErrorChatColor() + "En feil oppstod, kontakt stab.");
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("inviteOn")) {
                                    if (this.groupHandler.editGroupOption(groupID, OptionColumns.inviteOn, enabled)) {
                                        player.sendMessage(getOkChatColor() + "Innstillingen ble endret og lagret.");
                                        return true;
                                    } else {
                                        player.sendMessage(getErrorChatColor() + "En feil oppstod, kontakt stab.");
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("farge")) {
                                    try {
                                        ChatColor color = ChatColor.valueOf(args[3].toUpperCase());
                                        if (this.groupHandler.editGroupOptionColor(groupID, color)) {
                                            player.sendMessage(getOkChatColor() + "Innstillingen ble endret og lagret.");
                                            return true;
                                        } else {
                                            player.sendMessage(getErrorChatColor() + "En feil oppstod, kontakt stab.");
                                            return true;
                                        }
                                    } catch (Exception ex) {
                                        player.sendMessage(getErrorChatColor() + "Ugyldig farge oppgitt.");
                                        return true;
                                    }
                                }
                            } else {
                                player.sendMessage(getErrorChatColor() + "Innstillingen finnes ikke.");
                                return true;
                            }
                        } else {
                            player.sendMessage(getErrorChatColor() + "En eller flere verdier mangler.");
                            return true;
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Feil bruk av " + getArgChatColor() + "/gr mod inst" + getErrorChatColor() + ", " + getArgChatColor() + "sjekk /gr hjelp");
                        return true;
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Skriv /gr list.");
                    return true;
                }
                return false;
            }
        }
        player.sendMessage(getErrorChatColor() + "Ugyldig kommando, sjekk /gr");
        return true;
    }
}
