package me.kare.bossfight;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ZombieBossHandler implements CommandExecutor{
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            summonZombieBoss(p.getLocation());
            return true;
        }
        return false;
    }

    public static void summonZombieBoss(Location l) {
        var zb = new ZombieBoss(l);
        WorldServer world = ((CraftWorld) l.getWorld()).getHandle(); // Creates and NMS world
        world.addEntity(zb); // Adds the entity to the world
    }

    public static void onMobDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Zombie cz && e.getEntity().getKiller() != null
                && cz.getEquipment().getHelmet().getType() == Material.END_PORTAL_FRAME) {
            e.getDrops().clear();
            e.getDrops().add(generateNormalDrop());

            var first = !FileHandler.data.getBoolean("ZombieKilled");
            if(first) {
                e.getDrops().add(generateUniqueDrop());
                FileHandler.data.set("ZombieKilled", true);
            }
        }
    }

    private static ItemStack generateNormalDrop() {
        var sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        var swordMeta = sword.getItemMeta();
        Component name = Component.text("Gravestep Falchion")
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
        swordMeta.displayName(name);
        sword.setItemMeta(swordMeta);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 6);

        if (Math.random() > 0.9) {
            sword.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 4);
        }

        return sword;
    }

    private static ItemStack generateUniqueDrop() {
        var unique = new ItemStack(Material.END_PORTAL_FRAME);
        var uniqueMeta = unique.getItemMeta();
        var name = Component.text("Gravestep Head")
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
        uniqueMeta.displayName(name);
        List<Component> list = new ArrayList<>();
        var color = 0xAAAAAA;
        var lore = Component.text("Head of Gravestep, servant of Thundersoul.")
                .color(TextColor.color(color))
                .decoration(TextDecoration.ITALIC, false);
        list.add(lore);
        lore = Component.text("Gravestep was the weakest of Thundersoul's minions,")
                .color(TextColor.color(color))
                .decoration(TextDecoration.ITALIC, false);
        list.add(lore);
        lore = Component.text("being a mediocre necromancer and a beginner electromancer.")
                .color(TextColor.color(color))
                .decoration(TextDecoration.ITALIC, false);
        list.add(lore);
        uniqueMeta.lore(list);
        unique.setItemMeta(uniqueMeta);

        return unique;
    }
}