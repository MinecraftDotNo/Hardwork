package no.minecraft.hardwork;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HardworkSavedInventory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7424206711292742869L;
    
    private int slot;
    private Map<Integer, Map<String, Object>> serializedInventory;
    
    public HardworkSavedInventory(Inventory inv) {
        serializedInventory = new HashMap<Integer, Map<String, Object>>(inv.getSize());
        
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack itemstack = inv.getItem(slot);
            if (itemstack != null) {
                serializedInventory.put(slot, itemstack.serialize());
            }
        }
    }
    
    /**
     * Set items in serializedInventory back to inv.
     * 
     * @param inv
     */
    public void unpack(Inventory inv) {
        for (int slot : serializedInventory.keySet()) {
            Map<String, Object> serializedItemStack = serializedInventory.get(slot);
            ItemStack itemStack = ItemStack.deserialize(serializedItemStack);
            inv.setItem(slot, itemStack);
        }
    }

}
