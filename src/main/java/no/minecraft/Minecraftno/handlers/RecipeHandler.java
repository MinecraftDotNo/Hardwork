package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeHandler {
    private Minecraftno plugin;

    public RecipeHandler(Minecraftno plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	public void registerRecipes() {
    	// Mossy brick
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SMOOTH_BRICK, 1, (short) 1)).shape(" v ", "vsv", " v ").setIngredient('v', Material.VINE).setIngredient('s', Material.SMOOTH_BRICK));

        // Cracked brick (x6)
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SMOOTH_BRICK, 6, (short) 2)).shape("ss ", "s s", " ss").setIngredient('s', Material.SMOOTH_BRICK));

        // Special brick (x8)
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SMOOTH_BRICK, 8, (short) 3)).shape("sss", "s s", "sss").setIngredient('s', Material.SMOOTH_BRICK));

        // Glowstone dust (x4)
        this.plugin.getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.GLOWSTONE_DUST, 4)).addIngredient(Material.GLOWSTONE));

        // Double step
        this.plugin.getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.DOUBLE_STEP)).addIngredient(2, Material.STEP));

        // Bone (x4)
        this.plugin.getServer().addRecipe(new FurnaceRecipe(new ItemStack(Material.BONE, 4), Material.MILK_BUCKET));

        // Endstone
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.ENDER_STONE)).shape("nsn", "scs", "nsn").setIngredient('n', Material.NETHER_BRICK).setIngredient('s', Material.SNOW_BLOCK).setIngredient('c', Material.COBBLESTONE));

        // Slimeball
        this.plugin.getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.SLIME_BALL)).addIngredient(Material.SNOW_BALL).addIngredient(Material.INK_SACK, 2));

        // Gunpowder aka sulphur
        this.plugin.getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.SULPHUR)).addIngredient(Material.BLAZE_ROD).addIngredient(Material.SAND));

        // Creeper skull
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SKULL_ITEM, 1, (short) 4)).shape("g g", " w ", "g g").setIngredient('g', Material.SULPHUR).setIngredient('w', Material.SKULL_ITEM, 1));
        
        // Skeleton skull
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SKULL_ITEM)).shape("b b", " w ", "b b").setIngredient('b', Material.BONE).setIngredient('w', Material.SKULL_ITEM, 1));
        
        // Zombie skull
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SKULL_ITEM, 1, (short) 2)).shape("r r", " w ", "r r").setIngredient('r', Material.ROTTEN_FLESH).setIngredient('w', Material.SKULL_ITEM, 1));
        
        // Podzol
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.DIRT, 1, (short) 2)).shape("gdg", "dgd", "gdg").setIngredient('g', Material.GRASS).setIngredient('d', Material.DIRT));
        
        // Red Sand
        this.plugin.getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.SAND, 1, (short) 1)).addIngredient(Material.INK_SACK, 1).addIngredient(Material.SAND));
        
        // Ender pearl
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.ENDER_PEARL)).shape(" b ", "btb", " b ").setIngredient('b', Material.BLAZE_ROD).setIngredient('t', Material.GHAST_TEAR));

        // Spider eye
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SPIDER_EYE)).shape("  ", "wrw", "  ").setIngredient('w', Material.NETHER_WARTS).setIngredient('r', Material.ROTTEN_FLESH));

        // Glowstone
        this.plugin.getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.GLOWSTONE)).shape("ggg", "gjg", "ggg").setIngredient('g', Material.GLASS).setIngredient('j', Material.JACK_O_LANTERN));
    }
}
