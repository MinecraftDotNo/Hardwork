package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SmoothBrick;
import org.bukkit.material.Step;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BankHandler {
    private static class PriceData {
        public PriceData(int id, short damage, short amountPerGold) {
            this.id = id;
            this.damage = damage;
            this.amountPerGold = amountPerGold;
        }

        public final int id;
        public final short damage;
        public final short amountPerGold;
    }

    public static class Cost {
        public Cost(short goldAmount, short leftOver) {
            this.goldAmount = goldAmount;
            this.leftOver = leftOver;
        }

        public final short goldAmount;
        public final short leftOver;
    }

    private final Minecraftno plugin;
    private final MySQLHandler sqlHandler;
    private final UserHandler userHandler;
    private Connection conn;
    private List<String> priceList = new ArrayList<String>();
    private List<PriceData> buyableItems = new ArrayList<PriceData>();
    private PreparedStatement priceListPS;

    public static short MAX_SPACE = 35 * 64;

    public BankHandler(Minecraftno instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.userHandler = instance.getUserHandler();
    }

    public void initialise() {
        this.conn = this.plugin.getConnection();

        try {
            this.priceListPS = this.conn.prepareStatement("SELECT * FROM prices");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (Under laging av prepared statement)", e);
        }
        getPriceListFromDB();
    }

    /**
     * Henter ut summen en spiller har på bankkontoen sin.
     *
     * @param player
     *
     * @return amount
     */
    public int getAmount(Player player) {
        int setamount = 0;
        String amount = this.sqlHandler.getColumn("SELECT `amount` FROM bank WHERE `userid`=" + this.userHandler.getUserId(player) + "");
        if (amount == null) {
            if (sqlHandler.update("REPLACE INTO `bank` (`userid`, `amount`) VALUES (" + this.userHandler.getUserId(player) + ", " + setamount + ")")) {
                return setamount;
            } else {
                return 0;
            }
        }
        return Integer.parseInt(amount);
    }

    public int getAmount(String player) {
        int setamount = 0;
        String amount = this.sqlHandler.getColumn("SELECT `amount` FROM bank WHERE `userid`=" + this.userHandler.getUserId(player) + "");
        if (amount == null) {
            if (sqlHandler.update("REPLACE INTO `bank` (`userid`, `amount`) VALUES (" + this.userHandler.getUserId(player) + ", " + setamount + ")")) {
                return setamount;
            } else {
                return 0;
            }
        }
        return Integer.parseInt(amount);
    }

    /**
     * Setter summen en spiller har på bankkonto.
     *
     * @param player
     * @param 0      sum (int)
     *
     * @return true om den fikk oppdatert
     */

    public boolean setAmount(Player player, int amount) {
        return this.sqlHandler.update("UPDATE bank SET `amount`='" + amount + "' WHERE `userid`=" + this.userHandler.getUserId(player));
    }

    /**
     * Setter inn valgt sum på kontoen til en spiller
     *
     * @param player
     * @param 0      sum (int)
     *
     * @return true om summen ble satt inn.
     */
    public boolean insertAmount(Player player, int amount) {
        int newamount = getAmount(player) + amount;
        return this.sqlHandler.update("UPDATE bank SET `amount`='" + newamount + "' WHERE `userid`=" + this.userHandler.getUserId(player));
    }

    /**
     * Setter inn valgt sum på kontoen til en spiller
     *
     * @param player
     * @param 0      sum (int)
     *
     * @return true om summen ble satt inn.
     */
    public boolean insertAmount(String player, int amount) {
        int newamount = getAmount(player) + amount;
        return this.sqlHandler.update("UPDATE bank SET `amount`='" + newamount + "' WHERE `userid`=" + this.userHandler.getUserId(player));
    }

    /**
     * Fjerner valgt sum fra spillerens bankkonto
     *
     * @param player
     * @param 0      sum (int)
     *
     * @return false om summen er høyere enn det spilleren har på bankkonto
     */
    public boolean removeAmount(Player player, int amount) {
        if (getAmount(player) >= amount) {
            int newamount = getAmount(player) - amount;
            return this.sqlHandler.update("UPDATE bank SET `amount`='" + newamount + "' WHERE `userid`=" + this.userHandler.getUserId(player));
        } else {
            return false;
        }
    }

    /**
     * Overfører sum fra en spiller til en annen.
     *
     * @param from   (Player)
     * @param to     (Player)
     * @param amount (int)
     *
     * @return false avsender ikke har nok på bok.
     */
    public boolean transfer(Player from, Player to, int amount, UserHandler uh) {
        return removeAmount(from, amount) && insertAmount(to, amount);
    }

    /**
     * Metode for å beregne ledig plass i inventory slik at man unngår at
     * spilleren mister gull ved uttak
     *
     * @param player (Player) Spilleren som tar ut gull
     *
     * @return (int) Maks uttak fra banken.
     */
    public static int calculateMaxBankUt(Player player) {
        int emptySlot = 64;
        int freeSpace = 0;

        for (int i = 0; i <= 35; i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot.getType() == Material.AIR) {
                freeSpace += emptySlot;
            } else if (slot.getType() == Material.GOLD_INGOT) {
                freeSpace += emptySlot - slot.getAmount();
            }
        }

        return freeSpace;
    }

    public List<String> getPriceList() {
        return priceList;
    }

    public Cost getCost(ItemStack material) {
        for (PriceData item : this.buyableItems) {
            if (item.id == material.getTypeId() && item.damage == material.getDurability()) {
                short materialCost = (short) (material.getAmount() / item.amountPerGold);
                short leftOver = (short) (material.getAmount() % item.amountPerGold);
                return new Cost(materialCost, leftOver);
            }
        }
        return null;
    }

    private void getPriceListFromDB() {
        try {
            ResultSet rs = this.priceListPS.executeQuery();
            this.priceList.add(ChatColor.DARK_GREEN + "Priser:");
            while (rs.next()) {
                int id = rs.getInt("material");
                short damage = rs.getShort("damage");
                short amountPerGold = rs.getShort("amountpergold");
                this.buyableItems.add(new PriceData(id, damage, amountPerGold));
                // short damage = rs.getShort("damage");

                String materialName = getItemName(id, damage);
                if (materialName == null) {
                    materialName = "error";
                }
                this.priceList.add(ChatColor.DARK_GREEN + "- " + ChatColor.WHITE + materialName.toLowerCase().replace(" ", "") + ChatColor.DARK_GREEN + ". Antall per gull: " + ChatColor.WHITE + amountPerGold);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Fikk ikke hentet prisliste: ", e);
        }
    }

    public static String getItemName(int id, short damage) {
        if (damage == 0) {
            return Material.getMaterial(id).toString().toLowerCase();
        } else {
            if (Material.WOOD.getId() == id) {
                if (TreeSpecies.getByData((byte) damage) != null) {
                    return Material.getMaterial(id).toString().toLowerCase() + ":" + TreeSpecies.getByData((byte) damage).toString();
                }
            } else if (Material.WOOL.getId() == id) {
                if (DyeColor.getByWoolData((byte) damage) != null) {
                    return Material.getMaterial(id).toString().toLowerCase() + ":" + DyeColor.getByWoolData((byte) damage).toString();
                }
            } else if (Material.INK_SACK.getId() == id) {
                if (DyeColor.getByDyeData((byte) damage) != null) {
                    return Material.getMaterial(id).toString().toLowerCase() + ":" + DyeColor.getByDyeData((byte) damage).toString();
                }
            } else if (Material.STEP.getId() == id) {
                Step s = new Step();
                for (Material search : s.getTextures()) {
                    if (s.getTextures().indexOf(search) == damage) {
                        return Material.getMaterial(id).toString().toLowerCase() + ":" + search.toString();
                    }
                }
                return null;
            } else if (Material.DOUBLE_STEP.getId() == id) {
                Step s = new Step();
                for (Material search : s.getTextures()) {
                    if (s.getTextures().indexOf(search) == damage) {
                        return Material.getMaterial(id).toString().toLowerCase() + ":" + search.toString();
                    }
                }
                return null;
            } else if (Material.SMOOTH_BRICK.getId() == id) {
                SmoothBrick s = new SmoothBrick();
                for (Material search : s.getTextures()) {
                    if (s.getTextures().indexOf(search) == damage) {
                        return Material.getMaterial(id).toString().toLowerCase() + ":" + search.toString();
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static short getFreeSpace(Player p, ItemStack material) {
        short space = 0;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || it.getType() == Material.AIR) {
                space += 64;
            } else if (it.getTypeId() == material.getTypeId() && it.getDurability() == material.getDurability()) {
                space += 64 - it.getAmount();
            }
        }
        return space;
    }
}