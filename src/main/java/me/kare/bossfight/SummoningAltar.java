package me.kare.bossfight;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.util.Tuple;
import org.bukkit.*;
import org.bukkit.block.EnchantingTable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SummoningAltar implements CommandExecutor, Listener {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            ChestGui menu = summonGUI(p, p.getLocation());
            menu.show(p);
            return true;
        }
        return false;
    }

    public ChestGui summonGUI(Player p, Location l) {
        Component name = Component.text("Summoning Altar").color(TextColor.color(0x5a86be));
        var menu = new ChestGui(1, "A");
        menu.setTitle(ComponentHolder.of(name));

        menu.setOnTopClick(event -> event.setCancelled(true));
        menu.setOnBottomClick(event -> {
        });
        menu.setOnGlobalClick(event -> {
        });
        menu.setOnOutsideClick(event -> {
        });
        menu.setOnClose(event -> {
        });

        var background = new OutlinePane(0, 0, 9, 1, Pane.Priority.LOWEST);
        var bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        var bgMeta = bg.getItemMeta();
        bgMeta.displayName(Component.text(""));
        bg.setItemMeta(bgMeta);
        background.addItem(new GuiItem(bg));
        background.setRepeat(true);

        menu.addPane(background);

        var navigationPane = new StaticPane(3, 0, 3, 1);

        var zombieBoss = new ItemStack(Material.ZOMBIE_HEAD);
        var zombieMeta = zombieBoss.getItemMeta();
        zombieMeta.displayName(
                Component.text("Summon Gravestep")
                        .color(TextColor.color(0x30631c))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
        );
        List<Component> list = new ArrayList<>();

        var t = checkMatsZombie(list, p, l);
        boolean canSummon = t.a();
        Villager sacrifice = t.b();

        zombieMeta.lore(list);
        zombieBoss.setItemMeta(zombieMeta);

        navigationPane.addItem(new GuiItem(zombieBoss, event -> {
            clickZombie(canSummon, sacrifice, p, l);
        }), 0, 0);

        var value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ==";

        var skeletonBoss = BossFight.getSkullFromBase64(value);
        var skeletonMeta = skeletonBoss.getItemMeta();
        skeletonMeta.displayName(
                Component.text("Coming Soon")
                        .color(TextColor.color(0xAA0000))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true));
        skeletonBoss.setItemMeta(skeletonMeta);

        navigationPane.addItem(new GuiItem(skeletonBoss, event -> {}), 1, 0);
        navigationPane.addItem(new GuiItem(skeletonBoss, event -> {}), 2, 0);

        menu.addPane(navigationPane);

        return menu;
    }

    private void playSummonEffect(World w, Location l) {
        Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> w.strikeLightningEffect(l), 10L);
        Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> w.strikeLightningEffect(l), 15L);
        Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> w.strikeLightningEffect(l), 20L);
        Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> w.strikeLightningEffect(l), 25L);
        Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> w.strikeLightningEffect(l), 30L);
    }

    private void clickZombie(boolean canSummon, Villager sacrifice, Player p, Location l) {
        if (!isLocationGood(l)) {
            p.sendMessage("The area around the summoning altar isn't clear. Ritual needs 11x5x11 clear.");
            return;
        }

        if (canSummon) {
            sacrifice.getWorld().strikeLightningEffect(sacrifice.getLocation());
            sacrifice.damage(100);
            var inv = p.getInventory();
            BossFight.removeItems(inv, Material.ROTTEN_FLESH, 192);
            BossFight.removeItems(inv, Material.ZOMBIE_HEAD, 1);
            p.giveExp(-825);
            p.closeInventory();
            playSummonEffect(l.getWorld(), l);
            Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> ZombieBossHandler.summonZombieBoss(new Location(l.getWorld(), l.getX(), l.getY() + 1, l.getZ())), 30L);
        } else if (p.getGameMode() == GameMode.CREATIVE) {
            p.closeInventory();
            playSummonEffect(l.getWorld(), l);
            Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> ZombieBossHandler.summonZombieBoss(new Location(l.getWorld(), l.getX(), l.getY() + 1, l.getZ())), 30L);
        }
    }

    private boolean isLocationGood(Location l) {
        var w = l.getWorld();
        for (var i = -5; i <= 5; i++)
            for (var j = -5; j <= 5; j++)
                for (var k = 0; k <= 5; k++) {
                    if (!(i == 0 && j == 0)) {
                        var newloc = new Location(w, l.getX() + i, l.getY() + k, l.getZ() + j);
                        if(w.getBlockAt(newloc).getState().getType() != Material.AIR)
                            return false;
                    }
                }
        return true;
    }

    private Tuple<Boolean, Villager> checkMatsZombie(List<Component> list, Player p, Location l) {
        var canSummon = true;
        Villager sacrifice = null;

        var available = TextColor.color(0x00871C);
        var missing = TextColor.color(0xF4463F);
        var inv = p.getInventory();

        var flesh = new ItemStack(Material.ROTTEN_FLESH, 64);
        var fleshText = Component.text("3x 64 Rotten Flesh");
        if (inv.contains(flesh, 3))
            list.add(fleshText.color(available));
        else {
            list.add(fleshText.color(missing));
            canSummon = false;
        }

        var head = new ItemStack(Material.ZOMBIE_HEAD);
        var headText = Component.text("1x Zombie Head");
        if (inv.contains(head))
            list.add(headText.color(available));
        else {
            list.add(headText.color(missing));
            canSummon = false;
        }

        var hasVillager = false;
        var villagerText = Component.text("1x Villager Nearby");
        var entities = p.getWorld().getNearbyEntities(l, 2.5d, 2.5d, 2.5d);
        for (Entity e : entities)
            if (e instanceof Villager) {
                sacrifice = (Villager) e;
                hasVillager = true;
                list.add(villagerText.color(available));
                break;
            }
        if (!hasVillager) {
            list.add(villagerText.color(missing));
            canSummon = false;
        }

        var levelText = Component.text("30 Levels");
        if (p.getLevel() >= 30)
            list.add(levelText.color(available));
        else {
            list.add(levelText.color(missing));
            canSummon = false;
        }

        return new Tuple<>(canSummon, sacrifice);
    }

    public static void onBlockPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var b = e.getBlock().getState();
        if (b.getType() == Material.END_PORTAL_FRAME) {
            ItemStack item;
            var inv = p.getInventory();
            var main = inv.getItemInMainHand();
            if (main.getType() == Material.END_PORTAL_FRAME)
                item = main;
            else
                item = inv.getItemInOffHand();

            if (item.displayName().toString().contains("Gravestep")) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    private void onBlockInteract(PlayerInteractEvent e) {
        if (e.hasBlock() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            var b = e.getClickedBlock().getState();
            if (b instanceof EnchantingTable) {
                var name = ((EnchantingTable) b).getCustomName();
                if (name != null && name.equals("§x§5§A§8§6§B§ESummoning Altar")) {
                    e.setCancelled(true);
                    ChestGui menu = summonGUI(e.getPlayer(), b.getLocation());
                    menu.show(e.getPlayer());
                }
            }
        }
    }
}
