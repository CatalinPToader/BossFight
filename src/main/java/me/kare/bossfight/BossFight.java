package me.kare.bossfight;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.UUID;

public class BossFight extends JavaPlugin implements CommandExecutor{
    private static BossFight instance;
    private RecipeHandler recipeInstance;
    private SummoningAltar altarInstance;
    private ZombieBossHandler zbHandlerInstance;
    private FileHandler fileInstance;
    private SkeletonBossHandler sbHandlerInstance;
    public static LightningWand wandInstance;

    private EventsHandler eventsHandlerInstance;

    public static BossFight getInstance() {
        return instance;
    }
    public static LightningWand getWandInstance() {return wandInstance;}

    @Override
    public void onEnable(){
        instance = this;

        this.getCommand("bossversion").setExecutor(this);

        wandInstance = new LightningWand();
        this.getCommand("wand").setExecutor(wandInstance);
        this.getCommand("wandgui").setExecutor(wandInstance);

        eventsHandlerInstance = new EventsHandler();
        getServer().getPluginManager().registerEvents(eventsHandlerInstance, this);

        zbHandlerInstance = new ZombieBossHandler();
        this.getCommand("zombieboss").setExecutor(zbHandlerInstance);

        sbHandlerInstance = new SkeletonBossHandler();
        this.getCommand("skeletonboss").setExecutor(sbHandlerInstance);

        altarInstance = new SummoningAltar();
        this.getCommand("gui").setExecutor(altarInstance);
        getServer().getPluginManager().registerEvents(altarInstance, this);

        recipeInstance = new RecipeHandler();
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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            sender.sendMessage("BossFight plugin version "+this.getDescription().getVersion());
            sender.sendMessage(Component.text("[Click for Changelog]").decorate(TextDecoration.BOLD).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false).clickEvent(ClickEvent.openUrl("https://headwayapp.co/kare-bossfight-release-notes")));
            return true;
        }
        return false;
    }


}
