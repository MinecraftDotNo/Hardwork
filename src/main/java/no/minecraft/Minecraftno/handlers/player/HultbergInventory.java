package no.minecraft.Minecraftno.handlers.player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.HashSet;

public class HultbergInventory implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6569047372957587015L;
    private HashSet<HultbergItemStack> contents;

    public HultbergInventory(Inventory inv) {
        contents = new HashSet<HultbergItemStack>(inv.getSize());
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack != null) {
                contents.add(new HultbergItemStack(itemstack, i));
            }
        }
    }

    public void setContents(Inventory inv) {
        for (HultbergItemStack i : contents) {
            inv.setItem(i.getSlot(), i.getStack());
        }
    }
}
