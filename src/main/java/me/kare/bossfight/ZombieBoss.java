package me.kare.bossfight;

import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityZombie;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class ZombieBoss extends EntityZombie{
    Random r;
    static final int MAX_CD = 300;
    static final int MIN_CD = 100;
    static final int MINIONS = 6;
    int thunderCD = MAX_CD/3;
    int minionCD = MAX_CD/2;
    String name = ChatColor.DARK_RED + "Gravestep";
    boolean isDead = false;

    World world;
    Zombie thisBukkit;
    BossBar bar;

    public ZombieBoss(Location loc) {
        super(EntityTypes.be, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        thisBukkit = (Zombie) this.getBukkitEntity(); // get bukkit entity for future reference
        world = thisBukkit.getWorld(); // get world for future reference
        thisBukkit.setShouldBurnInDay(false);
        thisBukkit.setArmsRaised(true);

        thisBukkit.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300d);
        thisBukkit.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(24d);
        thisBukkit.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1d);
        thisBukkit.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.275d);
        thisBukkit.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(6d);

        thisBukkit.getEquipment().setHelmet(new ItemStack(Material.END_PORTAL_FRAME));
        thisBukkit.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        thisBukkit.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        thisBukkit.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        thisBukkit.getEquipment().setHelmetDropChance(0);
        thisBukkit.getEquipment().setChestplateDropChance(0);
        thisBukkit.getEquipment().setLeggingsDropChance(0);
        thisBukkit.getEquipment().setBootsDropChance(0);

        this.setHealth(300.0f);
        this.setCustomName(new ChatComponentText(name));
        this.setCustomNameVisible(true);

        bar = Bukkit.createBossBar(name, BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
        bar.setProgress(1.0d);

        List<Entity> near = thisBukkit.getNearbyEntities(10d, 10d, 10d); // get all nearby entities
        for (Entity e : near) {
            if (e instanceof Player) {
                this.setGoalTarget(((CraftPlayer) e).getHandle(), EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true); // target closest player
                bar.addPlayer((Player) e);
            }
        }
        r = new Random();
    }


    @Override
    public void tick() {
        super.tick();
        var health = this.getHealth();
        if (health > 0)
            bar.setProgress(this.getHealth() / this.getMaxHealth());
        else if (!isDead) {
            bar.setProgress(0);
            isDead = true;
            Bukkit.getScheduler().runTaskLater(BossFight.getInstance(), () -> bar.removeAll(), 10L);
        }
        if (!isDead) {
            thunderCD--;
            minionCD--;
            if (thunderCD <= 30 && thunderCD % 5 == 0) {
                world.playEffect(thisBukkit.getLocation(), Effect.POTION_BREAK, 2);
            }
            if (thunderCD <= 0 && this.getGoalTarget() != null) {
                Player target = (Player) this.getGoalTarget().getBukkitEntity();
                var damage = 4;
                var damageReduction = 0;
                world.strikeLightningEffect(target.getLocation());
                double oldHealth = target.getHealth();
                target.damage(1d);
                target.setHealth(oldHealth);
                double newHealth = Math.max(0, oldHealth - damage * (1 - damageReduction));
                target.setHealth(newHealth);
                var vect = target.getLocation().getDirection().multiply(-1);
                var vectX = vect.getX();
                if(Math.abs(vectX) < 1) {
                    vect.setX(vectX + 1 * Math.signum(vectX));
                }
                var vectZ = vect.getZ();
                if(Math.abs(vectZ) < 1) {
                    vect.setX(vectZ + 1 * Math.signum(vectZ));
                }
                target.setVelocity(vect);
                thunderCD = r.nextInt(MAX_CD-MIN_CD)+MIN_CD;
                thisBukkit.setAdult();
                thisBukkit.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.275d);
            }

            if (minionCD <= 30) {
                this.setGoalTarget(null);
                thisBukkit.setJumping(true);
            }
            if (minionCD <= 0) {
                List<Entity> near = thisBukkit.getNearbyEntities(10d, 10d, 10d); // get all nearby entities
                for (Entity e : near) {
                    if (e instanceof Player) {
                        for (var i = 0; i <= MINIONS; i++) {
                            Zombie z = (Zombie) world.spawnEntity(thisBukkit.getLocation(), EntityType.ZOMBIE);
                            z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.375d);
                            thisBukkit.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8d);
                        }
                        minionCD = r.nextInt(MAX_CD-MIN_CD)+MIN_CD;
                        thisBukkit.setBaby();
                        thisBukkit.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.225d);
                        break;
                    }
                }
            }
        }
    }
}
