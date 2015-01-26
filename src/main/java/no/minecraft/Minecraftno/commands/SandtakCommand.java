package no.minecraft.Minecraftno.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.SandtakHandler;
import no.minecraft.Minecraftno.handlers.WEBridge;
import no.minecraft.Minecraftno.handlers.blocks.BlockInfoHandler;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SandtakCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;
    private final SandtakHandler sandtakHandler;
    private final WEBridge weBridge;
    private final BlockInfoHandler blockInfoHandler;

    public SandtakCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.sandtakHandler = instance.getSandtakHandler();
        this.userHandler = instance.getUserHandler();
        this.weBridge = instance.getWeBridge();
        this.blockInfoHandler = instance.getBlockInfoHandler();
    }

	/*
     * sandtak:
	 * - liste
	 * - fyll
	 * - lagre
	 * - status
	 * 
	 * admin:
	 * - ny (nytt sandtak)
	 * - fjern (fjern sandtak)
	 */

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.displayMenu(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("liste")) {
                this.listSandtak(player);
            } else if (args[0].equalsIgnoreCase("lagre")) {
                this.storeChest(player);
            } else if (args[0].equalsIgnoreCase("status")) {
                this.sandtakInventoryStatus(player);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("fyll")) {
                // fills the given sandtak all the way up
                this.fillSandtak(player, args[1]);
            } else if (args[0].equalsIgnoreCase("ny")) {
                this.addNewSandtak(player, args[1]);
            } else if (args[0].equalsIgnoreCase("fjern")) {
                this.removeSandtak(player, args[1]);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("fyll") && WarningCommand.canParse(args[2])) {
                this.fillSandtak(player, args[1], Integer.parseInt(args[2]), "sand");
            } else {
                player.sendMessage(getErrorChatColor() + "Feil bruk av kommandoen.");
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("fyll") && WarningCommand.canParse(args[2])) {
                if (args[3].equalsIgnoreCase("snø") || args[3].equalsIgnoreCase("sno")) {
                    this.fillSandtak(player, args[1], Integer.parseInt(args[2]), "snow");
                } else if (args[3].equalsIgnoreCase("sand")) {
                    this.fillSandtak(player, args[1], Integer.parseInt(args[2]), "sand");
                } else {
                    player.sendMessage(getErrorChatColor() + "Feil bruk av kommandoen.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Feil bruk av kommandoen.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "For mange argumenter.");
        }

        return true;
    }

    /**
     * Displays the menu for this command
     *
     * @param player Player issuing the command
     */
    private void displayMenu(Player player) {
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "sandtak liste" + getVarChatColor() + " - Viser en liste over alle sandtak.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "sandtak lagre" + getVarChatColor() + " - Lagrer en dobbelkiste for fremtidig bruk i et sandtak.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "sandtak fyll <navn> [antall] [snø]" + getVarChatColor() + " - Fyller et sandtak med sand (eller snø dersom du ber om det). Du kan også angi antall dobbelkister taket skal fylles med.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "sandtak status" + getVarChatColor() + " - Viser hvor mange dobbelkister du har lagret som kan brukes i forbindelse med sandtak.");
        if (this.userHandler.getAccess(player) >= 4) {
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "sandtak ny <navn>" + getVarChatColor() + " - Oppretter et nytt sandtak på det markerte området.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "sandtak fjern <navn>" + getVarChatColor() + " - Fjerner det angitte sandtaket.");
        }
    }

    /**
     * Displays a list of available sandtak for the player
     *
     * @param player Player issuing the command
     */
    private void listSandtak(Player player) {
        player.sendMessage(ChatColor.DARK_GREEN + "Tilgjengelige sandtak med kapasitet i parentes:");
        if (this.sandtakHandler.getSandtakList() != null) {
            StringBuilder sb = new StringBuilder();
            for (String name : this.sandtakHandler.getSandtakList()) {
                sb.append(ChatColor.WHITE + name + ChatColor.DARK_GREEN + " (" + ChatColor.WHITE + this.sandtakHandler.getSandtakMap().get(name).getSize() + ChatColor.DARK_GREEN + "), ");
            }
            player.sendMessage(sb.toString().substring(0, sb.length() - 2) + ".");
        } else {
            player.sendMessage(ChatColor.WHITE + "Ingen sandtak lagt til.");
        }
    }

    private void storeChest(Player player) {
        Selection sel = this.weBridge.getWePlugin().getSelection(player);
        if (sel != null) {
            if (sel.getArea() == 2) {
                if (this.verifyChestOwner(player, sel)) {
                    if (this.sandtakHandler.verifyValidSelection(sel)) {
                        int materialId = this.sandtakHandler.getMaterialFromChest(sel);

                        if (this.sandtakHandler.updateSandtakInventoryStatus(player.getName(), materialId)) {
                            this.sandtakHandler.removeDoubleChest(sel);
                            player.sendMessage(getOkChatColor() + "Din kistestatus, for bruk i sandtak, er nå oppdatert."); // include status in message?
                        } else {
                            player.sendMessage(getErrorChatColor() + "En feil oppstod under oppdatering av din kistestatus i databasen.");
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du må markere en dobbelkisten som må være helt full og bestå av enten stein eller cobblestein.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Du kan ikke bruke andres dobbelkister.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Du må ha en markering på to blokker, altså en dobbelkiste.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Fant ingen markering. Du må markere dobbelkisten som du ønsker å legge til.");
        }
    }

    /**
     * Retrieves information about a players inventory status
     *
     * @param player Player issuing the command
     */
    private void sandtakInventoryStatus(Player player) {
        int statusStone = this.sandtakHandler.getSandtakInventoryStatus(player.getName(), Material.STONE);
        int statusCobbleStone = this.sandtakHandler.getSandtakInventoryStatus(player.getName(), Material.COBBLESTONE);
        if (statusStone != -1 && statusCobbleStone != -1) {
            player.sendMessage(getDefaultChatColor() + "Du har " + getVarChatColor() + statusCobbleStone + getDefaultChatColor() + " dobbelkister klar til bruk for sandtak med sand.");
            player.sendMessage(getDefaultChatColor() + "Du har " + getVarChatColor() + statusStone + getDefaultChatColor() + " dobbelkister klar til bruk for sandtak med snø.");
        } else {
            player.sendMessage(getErrorChatColor() + "Klarte ikke å hente informasjon om hvor mange dobbelkister du har lagret.");
        }
    }

    private void fillSandtak(Player player, String sandtakName) {
        this.fillSandtak(player, sandtakName, 0, "sand");
    }

    //	private void fillSandtak(Player player, String sandtakName, int amount) {
    //		this.fillSandtak(player, sandtakName, amount, "sand");
    //	}

    /**
     * Fills the sandtak
     *
     * @param player      Player issuing the command
     * @param sandtakName Name of sandtak
     * @param amountOfDk  (Optional) Amount of double chests to use. If 0 = use default for sandtak.
     * @param type        Type of sandtak
     */
    private void fillSandtak(Player player, String sandtakName, int amountOfDk, String type) {
        String realSandtakName = this.sandtakHandler.getSandtakName(sandtakName);
        if (realSandtakName != null) {
            if (this.sandtakHandler.isSandtakEmpty(realSandtakName)) {
                if (amountOfDk >= 0) {
                    int amount = 0;
                    Material materialId = Material.COBBLESTONE; // cobblestone, used for sand
                    Material sandtakMaterialId = Material.SAND; // sand by default
                    if (amountOfDk == 0) {
                        amount = this.sandtakHandler.getSandtakMap().get(realSandtakName).getSize();
                    } else {
                        amount = amountOfDk;
                    }

                    if (type.equals("snow")) {
                        materialId = Material.STONE; // stone, used for snow
                        sandtakMaterialId = Material.SNOW_BLOCK; // snow
                    }
                    int playerInventoryAmount = this.sandtakHandler.getSandtakInventoryStatus(player.getName(), materialId);

                    if (playerInventoryAmount >= amount) {
                        int sandtakSize = this.sandtakHandler.getSandtakMap().get(realSandtakName).getSize();
                        if (amount <= sandtakSize) {
                            if (this.sandtakHandler.removeDksFromPlayerSandtakInventory(player.getName(), amount, materialId)) {
                                this.sandtakHandler.fillSandtak(realSandtakName, amount, sandtakMaterialId);
                                player.sendMessage(getOkChatColor() + "Sandtaket har blitt fylt opp.");
                                this.plugin.getLogHandler().log(this.plugin.getUserHandler().getUserId(player), 0, 0, 0, realSandtakName, MinecraftnoLog.SANDTAKFILL);
                                this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " fylte opp et sandtak. Navn: " + realSandtakName + ". Sandtaket ble fylt med " + amount + " dobbelkister og med materialet " + type + ".");
                            } else {
                                player.sendMessage(getErrorChatColor() + "En feil har oppstått under kommunikasjon med databasen. Kontakt en vakt/stab dersom dette fortsetter.");
                            }
                        } else {
                            player.sendMessage(getErrorChatColor() + "Sandtaket har ikke kapasitet til " + getVarChatColor() + amount + getErrorChatColor() + " dobbelkister. Du kan maks fylle " + getVarChatColor() + sandtakSize + getErrorChatColor() + " dobbelkister i dette sandtaket.");
                        }
                    } else if (playerInventoryAmount < amount && playerInventoryAmount > -1) {
                        player.sendMessage(getErrorChatColor() + "Du har ikke nok dobbelkister lagret for å kunne fylle opp sandtaket.");
                        player.sendMessage(getErrorChatColor() + "Du har " + getVarChatColor() + playerInventoryAmount + getErrorChatColor() + " dobbelkister lagret og trenger " + getVarChatColor() + amount + getErrorChatColor() + " for å kunne fylle sandtaket.");
                    } else {
                        player.sendMessage(getErrorChatColor() + "En feil har oppstått. Kontakt en vakt/stab.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Du må angi et positivt antall dobbelkister.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Sandtaket må være helt tomt for at du skal kunne fylle det.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Sandtaket eksisterer ikke.");
        }
    }

    /**
     * Handles the add part of the sandtak command
     *
     * @param player      Player issuing the command
     * @param sandtakName Name of the sandtak to be added
     */
    private void addNewSandtak(Player player, String sandtakName) {
        if (this.userHandler.getAccess(player) >= 4) {
            String realSandtakName = this.sandtakHandler.getSandtakName(sandtakName);
            if (realSandtakName == null) {
                Selection sel = this.weBridge.getWePlugin().getSelection(player);
                if (sel != null) {
                    if (this.verifySelectionArea(sel)) {
                        if (this.sandtakHandler.addSandtak(sandtakName, sel.getMaximumPoint(), sel.getMinimumPoint())) {
                            player.sendMessage(getOkChatColor() + "Sandtaket har blitt opprettet.");
                            this.plugin.getLogHandler().log(this.plugin.getUserHandler().getUserId(player), 0, 0, 0, sandtakName, MinecraftnoLog.SANDTAKNEW);
                            this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " la til et nytt sandtak. Navn: " + realSandtakName + ".");
                        } else {
                            player.sendMessage(getErrorChatColor() + "En feil oppstod noe som førte til at sandtaket ikke ble opprettet.");
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du må velge en markering som tilsvarer minimum én dobbelkiste.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Du må markere sandtaket som du ønsker å lagre først.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Det eksisterer allerede et sandtak med det navnet.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Du har ikke tilgang til å bruke denne kommandoen.");
        }
    }

    /**
     * Handles the remove part of the sandtak command
     *
     * @param player      Player issuing the command
     * @param sandtakName Name of sandtak that should be deleted
     */
    private void removeSandtak(Player player, String sandtakName) {
        if (this.userHandler.getAccess(player) >= 4) {
            String realSandtakName = this.sandtakHandler.getSandtakName(sandtakName);
            if (realSandtakName != null) {
                if (this.sandtakHandler.deleteSandtak(sandtakName)) {
                    player.sendMessage(getOkChatColor() + "Sandtaket har blitt fjernet.");
                    this.plugin.getLogHandler().log(this.plugin.getUserHandler().getUserId(player), 0, 0, 0, realSandtakName, MinecraftnoLog.SANDTAKDELETE);
                    this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " fjernet sandtaket ved navn " + realSandtakName + ".");
                } else {
                    player.sendMessage(getErrorChatColor() + "En feil oppstod under sletting av sandtaket.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Sandtaket eksisterer ikke i databasen.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Du har ikke tilgang til å bruke denne kommandoen.");
        }
    }

    // Helper methods

    /**
     * Verifies the selected area to check whether it has the capacity of exactly x amounts of double chests
     *
     * @param sel The players selection
     *
     * @return true if the selected area can contain x amounts of double chests and false otherwise
     */
    private boolean verifySelectionArea(Selection sel) {
        double result = (double) sel.getArea() / 3456;

        return result == Math.floor(result) && !Double.isInfinite(result) && result > 0;
    }

    /**
     * Checks if the player owns the chest
     *
     * @param player Player to check
     * @param sel    Selection being checked (two points)
     *
     * @return True if players is the owner, false otherwise.
     */
    private boolean verifyChestOwner(Player player, Selection sel) {
        String owner = this.blockInfoHandler.getOwner(sel.getMaximumPoint());
        String owner2 = this.blockInfoHandler.getOwner(sel.getMinimumPoint());

        if (owner.equalsIgnoreCase(owner2)) {
            if (owner.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }

        return false;
    }
}