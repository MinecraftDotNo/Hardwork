package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.BankHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NametagCommand extends MinecraftnoCommand {

    private BankHandler bankHandler;

    public NametagCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.bankHandler = instance.getBankHandler();
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            printMain(player);
        } else {
            if (this.bankHandler.getAmount(player) >= 30) {
                if (player.getInventory().firstEmpty() != -1) {
                    String name = "Navn";
                    StringBuilder build = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        build.append(args[i] + " ");
                    }
                    name = build.toString().trim();

                    ItemStack item = new ItemStack(Material.NAME_TAG, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(name);
                    item.setItemMeta(meta);

                    player.getInventory().addItem(item);
                    this.bankHandler.removeAmount(player, 30);
                    this.bankHandler.insertAmount(UserHandler.SERVER_USERNAME, 30);
                    player.sendMessage(this.getOkChatColor() + "Du kjøpte en nametag for " + this.getVarChatColor() + "30g" + this.getOkChatColor() + ".");
                } else {
                    player.sendMessage(this.getErrorChatColor() + "Du har ikke plass til en nametag i din inventory.");
                }
            } else {
                player.sendMessage(this.getErrorChatColor() + "Du har ikke " + this.getVarChatColor() + "30g" + this.getErrorChatColor() + " på konto.");
            }
        }
        return true;
    }

    private void printMain(Player p) {
        p.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "nametag [navn]" + getVarChatColor() + " - Kjøper en nametag, husk navn.");
    }
}
