package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ICommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public ICommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 1 && args.length < 4) {
            ItemStack itemStack = null;
            try {
                itemStack = this.plugin.matchItem(args[0]);
            } catch (CommandException e) {
                player.sendMessage(getErrorChatColor() + "Ugyldig item (" + getOkChatColor() + args[0] + getErrorChatColor() + ").");
                return true;
            }
            if (itemStack == null) {
                player.sendMessage(getErrorChatColor() + "Ugyldig item (" + getOkChatColor() + args[0] + getErrorChatColor() + ").");
                return true;
            }

            if (itemStack.getType() == Material.AIR) {
                return true;
            }

            int amount = 1;
            if (args.length >= 2) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(getErrorChatColor() + "Ugyldig antall (" + getOkChatColor() + args[1] + getErrorChatColor() + ").");
                    return true;
                }

                if (amount < 1) {
                    player.sendMessage(getErrorChatColor() + "Ugyldig antall (" + getOkChatColor() + args[1] + getErrorChatColor() + ").");
                    return true;
                } else if (amount > 576) {
                    amount = 576;
                } else if (amount == 0) {
                    amount = 1;
                }
                itemStack.setAmount(amount);
            }
            if (args.length >= 3) {
                if (amount < 1) {
                    player.sendMessage(getErrorChatColor() + "Kan ikke gi uendelig-items til andre spillere.");
                    return true;
                }
                Player targetPlayer = plugin.getServer().getPlayer(args[2]);
                if (targetPlayer != null) {
                    if (this.userHandler.getAccess(player) >= 3) {
                        targetPlayer.getInventory().addItem(itemStack);
                        player.sendMessage(getDefaultChatColor() + "Gav " + getArgChatColor() + targetPlayer.getName() + " " + getVarChatColor() + amount + getDefaultChatColor() + " av item med id/navn: " + getVarChatColor() + itemStack.getTypeId() + getDefaultChatColor() + "/" + getVarChatColor() + Material.getMaterial(itemStack.getTypeId()).name());
                        targetPlayer.sendMessage(getDefaultChatColor() + "Du fikk " + getVarChatColor() + amount + getDefaultChatColor() + " av item med id/navn: " + getVarChatColor() + itemStack.getTypeId() + getDefaultChatColor() + "/" + getVarChatColor() + Material.getMaterial(itemStack.getTypeId()).name() + getDefaultChatColor() + " fra spilleren " + getArgChatColor() + player.getName() + getDefaultChatColor() + ".");

                        logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(targetPlayer), amount, itemStack.getTypeId(), null, MinecraftnoLog.GIVE);
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Fant ikke spilleren: " + getOkChatColor() + args[2]);
                }
            } else {
                String amountString = null;
                if (amount > 0) {
                    amountString = Integer.toString(amount);
                } else {
                    amountString = "uendelig";
                }
                player.getInventory().addItem(itemStack);
                player.sendMessage(getOkChatColor() + "Gav deg selv " + getErrorChatColor() + amountString + getOkChatColor() + " av item med id/navn: " + getErrorChatColor() + itemStack.getTypeId() + getOkChatColor() + "/" + getErrorChatColor() + Material.getMaterial(itemStack.getTypeId()).name());

                logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(player), amount, itemStack.getTypeId(), null, MinecraftnoLog.GIVE);
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Du må skrive tall");
        }
        return true;
    }
}
