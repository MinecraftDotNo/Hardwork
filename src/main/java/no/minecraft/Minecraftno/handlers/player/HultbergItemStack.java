package no.minecraft.Minecraftno.handlers.player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class HultbergItemStack implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1350134671643111396L;
    private int slot;    
    private Map<String, Object> serialized;

    public HultbergItemStack(ItemStack stack, int slot) {
        this.slot = slot;        
        this.serialized = stack.serialize();
    }

    public ItemStack getStack() {
        if (this.serialized != null && !this.serialized.isEmpty()) {
            return ItemStack.deserialize(serialized);
        }
        
        return null;
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