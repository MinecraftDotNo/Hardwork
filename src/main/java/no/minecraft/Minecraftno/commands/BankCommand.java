package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.BankHandler;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BankCommand extends MinecraftnoCommand {

    private final BankHandler bankHandler;

    public BankCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        bankHandler = instance.getBankHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.printMenu(player);
        } else {
            if (args[0].equalsIgnoreCase("info")) {
                if (args.length == 2 && this.plugin.getUserHandler().getAccess(player) > 2) {
                    this.displayUserAccountInfo(player, args[1]);
                } else {
                    this.displayUserAccountInfo(player);
                }
            } else if (args[0].equalsIgnoreCase("inn")) {
                this.bankInnCommand(player);
            } else if (args[0].equalsIgnoreCase("ut")) {
                if (args.length == 2) {
                    if (this.isStringInteger(args[1])) {
                        int amount = Integer.parseInt(args[1]);
                        if (amount > 0 && amount <= BankHandler.MAX_SPACE) {
                            if (this.bankHandler.getAmount(player) >= amount) {
                                this.bankUtCommand(player, amount);
                            } else {
                                player.sendMessage(getErrorChatColor() + "Du kan ikke ta ut så mye gull ettersom du kun har " + getVarChatColor() + this.bankHandler.getAmount(player) + getErrorChatColor() + " på kontoen din.");
                            }
                        } else if (amount > BankHandler.MAX_SPACE) {
                            player.sendMessage(getDefaultChatColor() + "Prøver å ta ut " + getVarChatColor() + BankHandler.MAX_SPACE + getDefaultChatColor() + " gull ettersom du oppga et for høyt antall gull.");
                            this.bankUtCommand(player, BankHandler.MAX_SPACE);
                        } else {
                            player.sendMessage(getErrorChatColor() + "Dersom du skal angi antall gull du skal ta ut, må du skrive inn et tall større enn 0.");
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du må taste inn et gyldig tall dersom du ønsker å angi hvor mye gull du skal ta ut av banken.");
                    }
                } else if (args.length == 1) {
                    this.bankUtCommand(player);
                } else {
                    player.sendMessage(getErrorChatColor() + "Du har skrevet inn for mange argumenter til kommandoen.");
                    player.sendMessage("Riktig syntax for kommandoen er: " + getOkChatColor() + "/bank ut <antall> - Antall er valgfritt.");
                }
            } else if (args[0].equalsIgnoreCase("betal")) {
                if (args.length >= 3) {
                    Player target = this.plugin.playerMatch(args[1]);
                    if (target != null) {
                        if (player != target) {
                            if (this.isStringInteger(args[2])) {
                                this.bankBetalCommand(player, target, args);
                            } else {
                                player.sendMessage(getErrorChatColor() + "Du må skrive inn et gyldig antall gull for å kunne overføre til en annen bruker.");
                            }
                        } else {
                            player.sendMessage(getErrorChatColor() + "Du kan ikke betale deg selv.");
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Spilleren er ikke pålogget eller eksisterer ikke.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Du må skrive inn både mottaker og antall gull som skal overføres.");
                }
            }
        }
        return true;
    }

    /**
     * Displays the menu for the given player
     *
     * @param player Player issuing command
     */
    private void printMenu(Player player) {
        player.sendMessage("----------------- " + getDefaultChatColor() + "Hardwork Bank ASA" + ChatColor.WHITE + " -----------------");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "bank info" + getVarChatColor() + " - Viser hvor mye gull du har på kontoen");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "bank inn" + getVarChatColor() + " Setter inn alt gull fra inventory.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "bank ut " + getArgChatColor() + "[mengde]" + getVarChatColor() + " Tar ut alt gull fra konto, eller" + getArgChatColor() + " [mengde] " + getVarChatColor() + " gull.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "bank betal " + getArgChatColor() + "[til] [mengde] ([grunn])" + getVarChatColor() + " betaler" + getArgChatColor() + " [mengde] " + getVarChatColor() + " gull til " + getArgChatColor() + " [til] " + getVarChatColor() + " , eventuellt med grunnen: " + getArgChatColor() + "[grunn] " + getVarChatColor() + ".");
    }

    /**
     * Checks if the given string is a number
     *
     * @param number String to check
     *
     * @return True if string is a number, false otherwise
     */
    public boolean isStringInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Displays bank information for the given user
     *
     * @param player Player issuing the command
     */
    private void displayUserAccountInfo(Player player) {
        int currentAmount = bankHandler.getAmount(player);
        player.sendMessage(getDefaultChatColor() + "Du har: " + getVarChatColor() + currentAmount + getDefaultChatColor() + " gull i banken.");
    }

    /**
     * Displays bank information for the given user
     *
     * @param player   Player issuing the command
     * @param userName User to display bank information of
     */
    private void displayUserAccountInfo(Player player, String userName) {
        Player victim = this.plugin.playerMatch(userName);
        int amount = -1;
        if (victim != null) {
            amount = this.bankHandler.getAmount(victim);
        } else {
            if (!this.plugin.getUserHandler().getUsernameFromDB(userName).isEmpty()) {
                amount = this.bankHandler.getAmount(userName);
            } else {
                player.sendMessage(getErrorChatColor() + "Fant ikke spilleren " + getVarChatColor() + userName + getErrorChatColor() + " i databasen.");
            }
        }

        if (amount != -1) {
            player.sendMessage(getDefaultChatColor() + "Spilleren har: " + getVarChatColor() + amount + getDefaultChatColor() + " gull i banken.");
        }
    }

    /**
     * The sub command insert. Handles the part where a user inserts gold in the bank.
     *
     * @param player Player issuing the command.
     */
    private void bankInnCommand(Player player) {
        Inventory inv = player.getInventory();
        ItemStack[] items = inv.getContents();
        int playerInventoryGold = 0;

        for (ItemStack stack : items) {
            if (stack != null) {
                if (stack.getType().equals(Material.GOLD_INGOT)) {
                    playerInventoryGold += stack.getAmount();
                } else if (stack.getType().equals(Material.GOLD_BLOCK)) {
                    playerInventoryGold += (stack.getAmount() * 9);
                }
            }
        }

        if (playerInventoryGold == 0) {
            player.sendMessage(getErrorChatColor() + "Du har ikke gull i inventoryen.");
        } else {
            if (bankHandler.insertAmount(player, playerInventoryGold)) {
                inv.remove(Material.GOLD_INGOT);
                inv.remove(Material.GOLD_BLOCK);
                player.sendMessage(getDefaultChatColor() + "Satt inn: " + getVarChatColor() + playerInventoryGold + getDefaultChatColor() + " gull i banken.");
                // Logging av bankinn
                logHandler.log(this.userHandler.getUserId(player), 0, playerInventoryGold, 0, null, MinecraftnoLog.BANKINN);
                int currentAmount = bankHandler.getAmount(player);
                player.sendMessage(getDefaultChatColor() + "Du har nå: " + getVarChatColor() + currentAmount + getDefaultChatColor() + " gull i banken.");
            } else {
                player.sendMessage(getErrorChatColor() + "En feil har oppstått. Kontakt en utvikler!");
            }
        }
    }

    /**
     * Handles the bank ut part of the command.
     *
     * @param player Player issuing the command.
     * @param amount Amount of gold.
     */
    private void bankUtCommand(Player player, int amount) {
        int maxEither = 0; // Empty inventory slots = 64 of either type.
        int maxIngots = 0; // Inventory slots with ingots = 64 - number of ingots in slot.
        int maxBlocks = 0; // Inventory slots with blocks = 64 - number of blocks in slot.

        ItemStack[] itemStacks = player.getInventory().getContents();
        for (ItemStack stack : itemStacks) {
            if (stack == null || stack.getType() == Material.AIR) {
                maxEither += player.getInventory().getMaxStackSize();
            } else if (stack.getType() == Material.GOLD_INGOT) {
                maxIngots += stack.getMaxStackSize() - stack.getAmount();
            } else if (stack.getType() == Material.GOLD_BLOCK) {
                maxBlocks += stack.getMaxStackSize() - stack.getAmount();
            }
        }

        // Absolute maximum number of gold we can fit in the inventory.
        int absMax = maxIngots + (maxBlocks * 9) + (maxEither * 9);

        // Trying to withdraw more than we can possibly fit?
        if (amount > absMax)
            amount = absMax;

        // The number of blocks and ingots we'd like to fit in the inventory.
        int optimalBlocks = (int) ((double) amount / (double) 9);
        int optimalIngots = amount % 9;

        // The number of blocks nad ingots we will place in the inventory.
        int blocks = 0;
        int ingots = 0;

        // Can we fit all the blocks?
        if (optimalBlocks <= maxEither + maxBlocks) {
            // Yes :)
            blocks = optimalBlocks;

            // How many "either slots" did we spend?
            int spentEither = Math.abs(maxBlocks - blocks);

            // Can we fit the ingots in there too?
            if (optimalIngots <= (maxEither - spentEither) + maxIngots) {
                // Yes! :) All is well, and I have a cigar!
                ingots = optimalIngots;
            } else {
                // No :( Fit what we can.
                ingots = maxIngots + (maxEither - spentEither);
                amount -= optimalIngots - ingots;
            }
        } else {
            // No. This is all the blocks we can fit.
            blocks = maxEither + maxBlocks;

            // How much is missing?
            int remainingBlocks = optimalBlocks - blocks;

            // Can we fit the remainder as ingots?
            if (optimalIngots + (remainingBlocks * 9) <= maxIngots) {
                // Heck yes! :)
                ingots = optimalIngots + (remainingBlocks * 9);
            } else {
                // No :( Fit what we can.
                ingots = maxIngots;
                amount -= (optimalIngots + (remainingBlocks * 9)) - ingots;
            }
        }

        // Verify our calculation.
        if (amount < 0 || (blocks * 9) + ingots != amount) {
            player.sendMessage(getErrorChatColor() + "Jim kan ikke mattematikk :( Prøvde å ta ut " + blocks + " blokker og " + ingots + " ingots, med det blir ikke " + amount + " til sammen.");
            return;
        }

        // Remove the gold from the account.
        if (!this.bankHandler.removeAmount(player, amount)) {
            // Huh? Something went wrong!
            player.sendMessage(getErrorChatColor() + "Noe gikk galt under uttak av gull! Ta kontakt med staben!");
            this.plugin.getIrcBot().sendMessage("#hardwork.logg", "[Error][bankUtCommand] - En feil oppstod når plugin prøvde å fjerne " + amount + " fra " + player.getName() + " sin konto.");
            return;
        }

        // Log the transaction.
        logHandler.log(this.userHandler.getUserId(player), 0, amount, 0, null, MinecraftnoLog.BANKUT);

        // Somewhere to store blocks and ingots we fail to place in the inventory.
        Map<Integer, ItemStack> failBlocks = new HashMap<>();
        Map<Integer, ItemStack> failIngots = new HashMap<>();

        // Place the blocks in the inventory.
        if (blocks > 0)
            player.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, blocks));

        // Place the ingots in the inventory.
        if (ingots > 0)
            player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, ingots));

        // The number of gold ingots to refund.
        int refund = 0;

        // Did we fail to place all the blocks in the inventory?
        if (!failBlocks.isEmpty()) {
            for (ItemStack stack : failBlocks.values()) {
                refund += stack.getAmount() * 9;
            }
        }

        // Did we fail to place all the ingots in the inventory?
        if (!failIngots.isEmpty()) {
            for (ItemStack stack : failIngots.values()) {
                refund += stack.getAmount();
            }
        }

        // Do we have to refund gold?
        if (refund > 0) {
            // Yep :(
            this.bankHandler.insertAmount(player, refund);
            logHandler.log(this.userHandler.getUserId(player), 0, refund, 0, null, MinecraftnoLog.BANKINN);

            this.plugin.getIrcBot().sendMessage("#hardwork.logg", "[Error][bankUtCommand] - " + player.getName() + " tok ut " + amount + " gull, men fikk ikke plass til alt i inventory. Antall gull til overs: " + refund);
        }

        // This is the story all about how...
        player.sendMessage(getDefaultChatColor() + "Tok ut: " + getVarChatColor() + (amount - refund) + getDefaultChatColor() + " gull. Du har nå: " + getVarChatColor() + this.bankHandler.getAmount(player) + getDefaultChatColor() + " gull i banken.");
    }

    /**
     * Retrieves amount of gold connected to the user and issues the bankUtCommand if the amount is not 0
     *
     * @param player Player issuing the command
     */
    private void bankUtCommand(Player player) {
        int amount = this.bankHandler.getAmount(player);
        if (amount != 0) {
            this.bankUtCommand(player, this.bankHandler.getAmount(player));
        } else {
            player.sendMessage(getErrorChatColor() + "Du har 0 gull på kontoen din.");
        }
    }

    /**
     * Handles the bank betal part of the command
     *
     * @param player Player issuing the command
     * @param target Receiver of the command
     * @param args   Arguments passed along
     */
    private void bankBetalCommand(Player player, Player target, String[] args) {
        int amount = Integer.parseInt(args[2]);
        if (amount >= 1) {
            String reason = "";
            if (args.length >= 4) {
                StringBuilder build = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    build.append(args[i] + " ");
                }
                build.substring(0, build.length() - 1);
                build.append('.');
                reason = build.toString();
            }

            if (this.bankHandler.transfer(player, target, amount, userHandler)) {
                player.sendMessage(getDefaultChatColor() + "Sendte " + getVarChatColor() + amount + getDefaultChatColor() + " gull til " + getVarChatColor() + target.getName() + getDefaultChatColor() + ". Du har nå " + getVarChatColor() + this.bankHandler.getAmount(player) + getDefaultChatColor() + " gull på kontoen.");

                this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(target), amount, 0, reason, MinecraftnoLog.BANKTRANSFER);

                if (!reason.isEmpty()) {
                    target.sendMessage(getDefaultChatColor() + "Spilleren " + getVarChatColor() + player.getName() + getDefaultChatColor() + " overførte " + getVarChatColor() + amount + getDefaultChatColor() + " gull til deg. Grunnen var: " + getVarChatColor() + reason + getDefaultChatColor() + ". Du har nå " + getVarChatColor() + this.bankHandler.getAmount(target) + getDefaultChatColor() + " gull på kontoen.");
                } else {
                    target.sendMessage(getDefaultChatColor() + "Spilleren " + getVarChatColor() + player.getName() + getDefaultChatColor() + " overførte " + getVarChatColor() + amount + getDefaultChatColor() + " gull til deg. Du har nå " + getVarChatColor() + this.bankHandler.getAmount(target) + getDefaultChatColor() + " gull på kontoen.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Du har ikke nok gull på kontoen til å overføre " + getVarChatColor() + amount + getErrorChatColor() + " gull.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Dersom du ønsker å betale en spiller så må du overføre minimum 1 gull. Ikke vær gjerrig nå!");
        }
    }
}
