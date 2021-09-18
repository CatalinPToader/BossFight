package me.kare.bossfight;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.DropperGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class LightningWand implements CommandExecutor {
    private static boolean usedNetherStar = false;

    private static void clickEvent(InventoryClickEvent e) {
        if (e.getCursor().getAmount() == 0 || e.getCursor().getType() != Material.NETHER_STAR
                || e.getInventory().getItem(e.getSlot()).getType() != Material.LIME_STAINED_GLASS_PANE)
            e.setCancelled(true);
        else {
            e.getCursor().subtract(1);
            var is = new ItemStack(Material.NETHER_STAR);
            var meta = is.getItemMeta();
            meta.displayName(Component.text("Nether Star Fuel").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            is.setItemMeta(meta);
            e.getClickedInventory().setItem(e.getSlot(), is);
            e.setCancelled(true);
            usedNetherStar = true;
        }
    }

    private static void closeEvent(InventoryCloseEvent e) {
        if (usedNetherStar) {
            usedNetherStar = false;
            var main = e.getPlayer().getInventory().getItemInMainHand();
            if (main.getType() == Material.LIGHTNING_ROD) {
                var name = main.getItemMeta().displayName();
                if (name != null && name.toString().contains("Lightning Wand")) {
                    var meta = main.getItemMeta();
                    createMeta(meta);
                    main.setItemMeta(meta);
                }
            } else {
                var off = e.getPlayer().getInventory().getItemInOffHand();
                if (off.getType() == Material.LIGHTNING_ROD) {
                    var name = off.getItemMeta().displayName();
                    if (name != null && name.toString().contains("Lightning Wand")) {
                        var meta = off.getItemMeta();
                        createMeta(meta);
                        off.setItemMeta(meta);
                    }
                }
            }
        }
    }

    private static void createMeta(ItemMeta meta) {
        var metaname = meta.displayName();
        metaname = metaname.replaceText(TextReplacementConfig.builder().match(" \\(Empty\\)").replacement("").build());
        meta.displayName(metaname);
        var list = meta.lore();
        var comp = list.get(0);
        list.remove(0);
        list.add(0, comp.replaceText(TextReplacementConfig.builder().match(Pattern.compile("[0-5]\\/5")).replacement("5/5").build()));
        meta.lore(list);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 5, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public static DropperGui rechargeGUI(Player p, Component name) {
        var menu = new DropperGui("A");
        menu.setTitle(ComponentHolder.of(name));

        menu.setOnTopClick(LightningWand::clickEvent);
        menu.setOnBottomClick(event -> {
        });
        menu.setOnGlobalClick(event -> {
            var i = event.getClickedInventory().getItem(event.getSlot());
            if (i != null && i.getType() == Material.LIGHTNING_ROD)
                event.setCancelled(true);
        });
        menu.setOnOutsideClick(event -> {
        });
        menu.setOnClose(LightningWand::closeEvent);

        var background = new OutlinePane(0, 0, 3, 3, Pane.Priority.LOWEST);
        var bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        var bgMeta = bg.getItemMeta();
        bgMeta.displayName(Component.text(""));
        bg.setItemMeta(bgMeta);
        background.addItem(new GuiItem(bg));
        background.setRepeat(true);

        menu.getContentsComponent().addPane(background);

        var starSlot = new StaticPane(1, 1, 1, 1);
        var slot = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        var slotMeta = slot.getItemMeta();
        slotMeta.displayName(Component.text("Recharge Slot").color(NamedTextColor.GREEN));
        List<Component> list = new ArrayList<>();
        list.add(Component.text("Drop a nether star in to recharge wand.").color(NamedTextColor.GREEN));
        slotMeta.lore(list);
        slot.setItemMeta(slotMeta);
        starSlot.addItem(new GuiItem(slot), 0, 0);
        menu.getContentsComponent().addPane(starSlot);

        return menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (command.getName().equals("wand"))
                p.getInventory().addItem(wand());
            else {
                DropperGui menu = rechargeGUI(p, Component.text("Debug GUI"));
                menu.show(p);
            }
            return true;
        }
        return false;
    }

    public ItemStack wand() {
        var is = new ItemStack(Material.LIGHTNING_ROD);
        var meta = is.getItemMeta();
        var name = Component.text("Lightning Wand (Empty)")
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true)
                .color(TextColor.color(0xe3e880));
        meta.displayName(name);
        List<Component> list = new ArrayList<>();
        var color = 0xAAAAAA;
        var lore = Component.text("Charge 0/5")
                .color(TextColor.color(color))
                .decoration(TextDecoration.ITALIC, false);
        list.add(lore);
        lore = Component.text("Recharge using Nether Stars")
                .color(TextColor.color(color))
                .decoration(TextDecoration.ITALIC, false);
        list.add(lore);
        lore = Component.text("Used to summon lightning on hit mob.")
                .color(TextColor.color(color))
                .decoration(TextDecoration.ITALIC, false);
        list.add(lore);
        meta.lore(list);
        is.setItemMeta(meta);

        return is;
    }

    public static void onBlockPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var b = e.getBlock().getState();
        if (b.getType() == Material.LIGHTNING_ROD) {
            ItemStack item;
            var inv = p.getInventory();
            var main = inv.getItemInMainHand();
            if (main.getType() == Material.LIGHTNING_ROD)
                item = main;
            else
                item = inv.getItemInOffHand();

            if (item.displayName().toString().contains("Wand")) {
                e.setCancelled(true);
                DropperGui menu = rechargeGUI(p, item.getItemMeta().displayName());
                menu.show(p);
            }
        }
    }

    public static void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() != null)
                if (e.getItem().getType() == Material.LIGHTNING_ROD && e.getItem().getItemMeta().displayName() != null && e.getItem().getItemMeta().displayName().toString().contains("Wand")) {
                    DropperGui menu = rechargeGUI(e.getPlayer(), e.getItem().getItemMeta().displayName());
                    menu.show(e.getPlayer());
                    e.setCancelled(true);
                }
        }
    }

    public static void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            var main = p.getInventory().getItemInMainHand();
            if (main.getType() == Material.LIGHTNING_ROD) {
                var meta = main.getItemMeta();
                if (meta.displayName() != null && meta.displayName().toString().contains("Lightning Wand"))
                    if (meta.hasEnchant(Enchantment.ARROW_INFINITE)) {
                        updateMeta(meta);
                        main.setItemMeta(meta);
                        e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation());
                    }
            } else {
                var off = p.getInventory().getItemInOffHand();
                if (off.getType() == Material.LIGHTNING_ROD) {
                    var meta = off.getItemMeta();
                    if (meta.displayName() != null && meta.displayName().toString().contains("Lightning Wand"))
                        if (meta.hasEnchant(Enchantment.ARROW_INFINITE)) {
                            updateMeta(meta);
                            off.setItemMeta(meta);
                            e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation());
                        }
                }
            }
        }
    }

    private static void updateMeta(ItemMeta meta) {
        var level = meta.getEnchantLevel(Enchantment.ARROW_INFINITE);
        meta.removeEnchant(Enchantment.ARROW_INFINITE);
        if (level - 1 != 0)
            meta.addEnchant(Enchantment.ARROW_INFINITE, level - 1, true);
        else {
            var name = meta.displayName();
            name = name.append(Component.text(" (Empty)"));
            meta.displayName(name);
        }
        var list = meta.lore();
        var comp = list.get(0);
        list.remove(0);
        String charge = String.valueOf(level - 1) + "/5";
        list.add(0, comp.replaceText(TextReplacementConfig.builder().match(Pattern.compile("[0-5]\\/5")).replacement(charge).build()));
        meta.lore(list);
    }
}
