package no.minecraft.Minecraftno.handlers.player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * @deprecated Using Yaml configs for saving inventories, see no.minecraft.Minecraftno.commands.WorkCommand.java
 * @author Edvin
 *
 */
public class HultbergItemStack implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1350134671643111396L;
    
    /* Slot is used in legacy and new way */
    private int slot;    
    
    /* New serialize map */
    private Map<String, Object> serialized;
    
    /* Legacy */
    private int itemId;
    private byte data;
    private short durability;
    private int amount;
    private Map<String, Object> meta;

    public HultbergItemStack(ItemStack stack, int slot) {
        this.slot = slot;        
        this.serialized = null;
        
        /* Not serialising with legacy methods */
    }

    public ItemStack getStack() {
        if (this.serialized != null) {
            if (!this.serialized.isEmpty())
                return ItemStack.deserialize(serialized);
            
            return null;
        } else {
            // Support legacy
            ItemStack r = new ItemStack(this.itemId);
            r.setDurability(this.durability);
            r.setData(new MaterialData(this.data));
            r.setAmount(this.amount);
            if (this.meta != null && !this.meta.isEmpty()) {
                r.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(this.meta, ConfigurationSerialization.getClassByAlias("ItemMeta")));
            }
            return r;
        }
    }

    public int getSlot() {
        return slot;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createMap(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        if (!map.isEmpty()) {
            for (String s : map.keySet()) {
                Object value = map.get(s);
                if (value instanceof Map) {
                    value = this.createMap((Map<String, Object>) value);
                }

                newMap.put(s, value);
            }
        }
        return newMap;
    }
}