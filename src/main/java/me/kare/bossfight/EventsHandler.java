package me.kare.bossfight;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class EventsHandler implements Listener {

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        ZombieBossHandler.onMobDeath(e);
        SkeletonBossHandler.onMobDeath(e);
        ChargedCreeperOverride.onMobDeath(e);
    }

    @EventHandler
    public void onEntityExplode(ExplosionPrimeEvent e) {
        ChargedCreeperOverride.onEntityExplode(e);
    }

    @EventHandler
    public static void onEntityChangeBlock(EntityChangeBlockEvent e) {
        IceBall.onEntityChangeBlock(e);
    }

    @EventHandler
    public void onEnterVehicle(VehicleEnterEvent e) {
        if (SkeletonBoss.isSkeletonBoss(e.getEntered()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        SummoningAltar.onBlockPlace(e);
        LightningWand.onBlockPlace(e);
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent e) {
        LightningWand.onEntityDamage(e);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e){
        RecipeHandler.onPlayerLogin(e);
    }

    @EventHandler
    public void onCraftEvent(CraftItemEvent e){
        RecipeHandler.onCraftEvent(e);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        LightningWand.onPlayerInteract(e);
    }
}
