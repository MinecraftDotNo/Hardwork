package no.minecraft.Minecraftno.handlers.player;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HultbergItemStack implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1350134671643111396L;
    private int slot;
    private int itemId;
    private byte data;
    private short durability;
    private int amount;
    private Map<String, Object> meta;

    public HultbergItemStack(ItemStack stack, int slot) {
        this.slot = slot;
        this.itemId = stack.getTypeId();
        this.data = stack.getData().getData();
        this.durability = stack.getDurability();
        this.amount = stack.getAmount();
        if (stack.getItemMeta() != null) {
            meta = this.createMap((stack.getItemMeta()).serialize());
        }
    }

    public ItemStack getStack() {
        ItemStack r = new ItemStack(this.itemId);
        r.setDurability(this.durability);
        r.setData(new MaterialData(this.data));
        r.setAmount(this.amount);
        if (this.meta != null && !this.meta.isEmpty()) {
            r.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(this.meta, ConfigurationSerialization.getClassByAlias("ItemMeta")));
        }
        return r;
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