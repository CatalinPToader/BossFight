package me.kare.bossfight;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class SummoningAltarRecipe {

    public void onEnable() {
        // Our custom variable which we will be changing around.
        var item = new ItemStack(Material.ENCHANTING_TABLE);

        // The meta of the diamond sword where we can change the name, and properties of the item.
        var meta = item.getItemMeta();

        // We will initialise the next variable after changing the properties of the sword

        // This sets the name of the item.
        Component name = Component.text("Summoning Altar").color(TextColor.color(0x5a86be));
        meta.displayName(name);

        // Set the meta of the sword to the edited meta.
        item.setItemMeta(meta);

        // create a NamespacedKey for your recipe
        var key = new NamespacedKey(BossFight.getInstance(), "summoning_altar");

        // Create our custom recipe variable
        var recipe = new ShapedRecipe(key, item);

        // Here we will set the places. E and S can represent anything, and the letters can be anything. Beware; this is case sensitive.
        recipe.shape(" D ", "SOS", "OOO");

        // Set what the letters represent.
        // E = Emerald, S = Stick
        recipe.setIngredient('D', Material.DRAGON_HEAD);
        recipe.setIngredient('S', Material.DIAMOND_SWORD);
        recipe.setIngredient('O', Material.CRYING_OBSIDIAN);

        // Finally, add the recipe to the bukkit recipes
        Bukkit.addRecipe(recipe);

    }
}
