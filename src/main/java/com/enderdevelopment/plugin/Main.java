package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {
    private static final double RANGE = 20.0;
    private static final double TRUE_DAMAGE = 8.0;
    private static final long COOLDOWN_MILLIS = 5000L;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        cooldowns.clear();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isSneaking() || !wearingSilenceTrim(player)) {
            return;
        }

        event.setCancelled(true);
        long now = System.currentTimeMillis();
        long availableAt = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now < availableAt) {
            long seconds = (long) Math.ceil((availableAt - now) / 1000.0);
            player.sendActionBar(Component.text("Sonic beam recharging: " + seconds + "s")
                    .color(NamedTextColor.RED));
            return;
        }

        cooldowns.put(player.getUniqueId(), now + COOLDOWN_MILLIS);
        fireBeam(player);
    }

    private boolean wearingSilenceTrim(Player player) {
        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (armorPiece == null || armorPiece.getType().isAir()) {
                continue;
            }
            if (!(armorPiece.getItemMeta() instanceof ArmorMeta armorMeta)
                    || !armorMeta.hasArmorTrim()) {
                continue;
            }
            ArmorTrim trim = armorMeta.getArmorTrim();
            if (trim != null && isSilencePattern(trim.getPattern())) {
                return true;
            }
        }
        return false;
    }

    private boolean isSilencePattern(TrimPattern pattern) {
        return pattern != null && "silence".equals(pattern.getKey().getKey());
    }

    private void fireBeam(Player player) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();
        RayTraceResult entityHit = player.getWorld().rayTraceEntities(start, direction, RANGE, 0.35,
                entity -> entity instanceof LivingEntity && entity != player);
        RayTraceResult blockHit = player.getWorld().rayTraceBlocks(start, direction, RANGE);

        double entityDistance = entityHit == null ? Double.MAX_VALUE
                : start.distance(entityHit.getHitPosition().toLocation(player.getWorld()));
        double blockDistance = blockHit == null ? RANGE
                : start.distance(blockHit.getHitPosition().toLocation(player.getWorld()));
        double beamDistance = Math.min(entityDistance, blockDistance);
        Location endpoint = start.clone().add(direction.clone().multiply(beamDistance));

        player.getWorld().playSound(start, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.SONIC_BOOM, endpoint, 1, 0, 0, 0, 0);

        if (entityHit != null && entityDistance <= blockDistance
                && entityHit.getHitEntity() instanceof LivingEntity target) {
            target.setHealth(Math.max(0.0, target.getHealth() - TRUE_DAMAGE));
        }
    }
}
