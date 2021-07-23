package me.kare.bossfight;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.UUID;

public class BossFight extends JavaPlugin{
    private static BossFight instance;
    private SummoningAltarRecipe recipeInstance;
    private SummoningAltar altarInstance;
    private ZombieBossHandler zbHandlerInstance;
    private FileHandler fileInstance;

    public static BossFight getInstance() {
        return instance;
    }

    @Override
    public void onEnable(){
        instance = this;

        zbHandlerInstance = new ZombieBossHandler();
        this.getCommand("zombieboss").setExecutor(zbHandlerInstance);
        getServer().getPluginManager().registerEvents(zbHandlerInstance, this);

        altarInstance = new SummoningAltar();
        this.getCommand("gui").setExecutor(altarInstance);
        getServer().getPluginManager().registerEvents(altarInstance, this);

        recipeInstance = new SummoningAltarRecipe();
        recipeInstance.onEnable();

        fileInstance = new FileHandler();
        fileInstance.start();

        getLogger().info("BossFight is loaded.");
    }

    @Override
    public void onDisable(){

        fileInstance = null;
        zbHandlerInstance = null;
        altarInstance = null;
        recipeInstance = null;
        getLogger().info("BossFight disabled.");
    }


    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (var slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

    public static ItemStack getSkullFromBase64(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        if (base64 == null || base64.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

}
