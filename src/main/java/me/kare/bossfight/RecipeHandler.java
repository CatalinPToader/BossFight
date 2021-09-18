package me.kare.bossfight;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Random;

public class RecipeHandler {

    public void summoningAltar() {
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

    public void wand() {
        var item = BossFight.getWandInstance().wand();

        // create a NamespacedKey for your recipe
        var key = new NamespacedKey(BossFight.getInstance(), "lightning_wand");

        // Create our custom recipe variable
        var recipe = new ShapedRecipe(key, item);

        recipe.shape("GDG", " C ", " C ");

        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('C', Material.COPPER_BLOCK);

        // Finally, add the recipe to the bukkit recipes
        Bukkit.addRecipe(recipe);
    }

    public void onEnable() {
        wand();
        summoningAltar();
    }

    public static void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        p.discoverRecipe(new NamespacedKey(BossFight.getInstance(), "lightning_wand"));
        p.discoverRecipe(new NamespacedKey(BossFight.getInstance(), "summoning_altar"));
    }

    public static void onCraftEvent(CraftItemEvent e) {
        var res = e.getRecipe().getResult();
        if (res.getType() == Material.LIGHTNING_ROD && res.getItemMeta().displayName() != null && res.getItemMeta().displayName().toString().contains("Wand")) {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(Component.text("Sorry, but stacking wands is disallowed to prevent exploits. Craft only one!").color(NamedTextColor.DARK_RED));
            }
            var is = e.getRecipe().getResult();
            Random r = new Random();

            NBTItem nbti = new NBTItem(is);
            nbti.setInteger("RandomValue", r.nextInt());

            is = nbti.getItem();
            e.setCurrentItem(is);
        }
    }
}
