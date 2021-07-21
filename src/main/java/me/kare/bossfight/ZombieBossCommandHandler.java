package me.kare.bossfight;

import net.minecraft.server.level.WorldServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;

public class ZombieBossCommandHandler implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player p) {
            ZombieBoss zb = new ZombieBoss(p.getLocation()/*Pig Spawn Location*/); // Calls CustomPig constructor
            WorldServer world = ((CraftWorld) p.getWorld()).getHandle(); // Creates and NMS world
            world.addEntity(zb); // Adds the entity to the world
        }
        return false;
    }
}
