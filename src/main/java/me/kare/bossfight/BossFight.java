package me.kare.bossfight;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BossFight extends JavaPlugin{
    private static BossFight instance;
    private SummoningAltarRecipe recipeInstance;
    private SummoningAltar altarInstance;

    public static BossFight getInstance() {
        return instance;
    }

    @Override
    public void onEnable(){
        instance = this;
        this.getCommand("zombieboss").setExecutor(new ZombieBossCommandHandler());
        altarInstance = new SummoningAltar();
        this.getCommand("gui").setExecutor(altarInstance);
        getServer().getPluginManager().registerEvents(altarInstance, this);
        recipeInstance = new SummoningAltarRecipe();
        recipeInstance.onEnable();
        getLogger().info("BossFight is loaded.");
    }

    @Override
    public void onDisable(){
        recipeInstance = null;
        getLogger().info("BossFight disabled.");
    }


    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
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

}
