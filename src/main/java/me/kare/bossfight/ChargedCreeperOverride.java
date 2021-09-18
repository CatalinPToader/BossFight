package me.kare.bossfight;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;

public class ChargedCreeperOverride{

    public static void onEntityExplode(ExplosionPrimeEvent e) {
        if (e.getEntity().getType() == EntityType.CREEPER) {
            Creeper c = (Creeper) e.getEntity();
            if (c.isPowered()) {
                int radius = c.getExplosionRadius();
                for (Entity entity : c.getNearbyEntities(radius, radius, radius)) {
                    if (entity.getType() != EntityType.PLAYER) {
                        entity.addScoreboardTag("drop head");
                    }
                }
            }
        }
    }

    public static void onMobDeath(EntityDeathEvent e) {
        if (!e.getEntity().getScoreboardTags().isEmpty() && e.getEntity().getScoreboardTags().contains("drop head")) {
            var type = e.getEntity().getType();
            var drops = e.getDrops();
            for(ItemStack s : drops) {
                var typeItem = s.getType();
                if(typeItem == Material.SKELETON_SKULL || typeItem == Material.ZOMBIE_HEAD || typeItem == Material.CREEPER_HEAD)
                    return;
            }
            if (type == EntityType.ZOMBIE) {
                drops.add(new ItemStack(Material.ZOMBIE_HEAD, 1));
            } else if (type == EntityType.SKELETON)
                drops.add(new ItemStack(Material.SKELETON_SKULL, 1));
            else if (type == EntityType.CREEPER)
                drops.add(new ItemStack(Material.CREEPER_HEAD, 1));
        }
    }
}
