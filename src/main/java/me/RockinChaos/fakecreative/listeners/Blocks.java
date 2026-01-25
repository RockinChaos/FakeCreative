/*
 * FakeCreative
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.fakecreative.listeners;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.modes.instance.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Blocks implements Listener {

    public static final HashMap<String, Long> lastBreakTime = new HashMap<>();
    public static final HashMap<String, InteractionData> lastInteraction = new HashMap<>();

    /**
     * Prevents the player from damaging blocks.
     *
     * @param event - BlockDamageEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDamage(final BlockDamageEvent event) {
        if (Creative.isCreativeMode(event.getPlayer(), true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles block breaking when the Player instant breaks a block.
     *
     * @param event - BlockBreakEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onBreak(final BlockBreakEvent event) {
        if (Creative.isCreativeMode(event.getPlayer(), true)) {
            final Block block = event.getBlock();
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType(), 6);
            if (!Creative.get(event.getPlayer()).getStats().blockDrops()) {
                block.setType(Material.AIR);
            } else if (block.getType() != Material.AIR) {
                block.breakNaturally();
            }
        }
    }

    /**
     * Tracks player interactions to prevent false positives in animation event.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (Creative.isCreativeMode(player, true)) {
            lastInteraction.put(PlayerHandler.getPlayerID(player), new InteractionData(event.getAction(), event.getClickedBlock()));
        }
    }

    /**
     * Allows the player to instantly break blocks continuously in creative mode.
     *
     * @param event - PlayerAnimationEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInstantBreakBlock(final PlayerAnimationEvent event) {
        final Player player = event.getPlayer();
        if (!Creative.isCreativeMode(player, true)) return;
        final String playerId = PlayerHandler.getPlayerID(player);
        final InteractionData interaction = lastInteraction.get(playerId);
        if (interaction == null || interaction.action != Action.LEFT_CLICK_BLOCK) return;
        final long now = System.currentTimeMillis();
        if (interaction.lastSwingTime != null) {
            final long timeBetweenSwings = now - interaction.lastSwingTime;
            if (timeBetweenSwings > 80) {
                lastInteraction.remove(playerId);
                return;
            }
        }
        interaction.lastSwingTime = now;
        if (PlayerHandler.getMainHandItem(player).getType().name().contains("SWORD") && Creative.get(player).getStats().swordBlock()) return;
        final Block block = interaction.block;
        if (block != null && block.getType() != Material.AIR && canBreak(player)) {
            Bukkit.getPluginManager().callEvent(new BlockBreakEvent(block, player));
        }
    }


    /**
     * Allows the player to instantly break entities (armor stands, item frames, paintings, etc.) in creative mode.
     *
     * @param event - EntityDamageByEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInstantBreakEntity(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !Creative.isCreativeMode((Player) event.getDamager(), true)) return;
        final Player player = (Player) event.getDamager();
        final Entity entity = event.getEntity();
        final ItemStack item = ItemHandler.getEntityItem(entity);
        if (item.getType() != Material.AIR && !item.getType().name().contains("EGG") && canBreak(player)) {
            event.setCancelled(true);
            if (entity instanceof LivingEntity) {
                List<ItemStack> drops = new ArrayList<>();
                drops.add(item);
                EntityDeathEvent deathEvent = null;
                if (ServerUtils.hasSpecificUpdate("1_20")) {
                    deathEvent = new EntityDeathEvent((LivingEntity) entity, event.getDamageSource(), drops, 0);
                } else {
                    try {
                        final LivingEntity livingEntity = (LivingEntity) entity;
                        //noinspection JavaReflectionMemberAccess
                        deathEvent = EntityDeathEvent.class.getConstructor(LivingEntity.class, List.class, int.class).newInstance(livingEntity, drops, 0);
                    } catch (Exception e) {
                        ServerUtils.logWarn("Failed to create EntityDeathEvent");
                        ServerUtils.sendDebugTrace(e);
                    }
                }
                if (deathEvent != null) {
                    Bukkit.getPluginManager().callEvent(deathEvent);
                    if (Creative.get(player).getStats().blockDrops()) {
                        final Placement.OwnerData ownerData = Placement.getEntityOwner(entity);
                        PlayerStats playerStats = null;
                        if (ownerData != null && ownerData.state.equals(String.valueOf(entity.getEntityId()))) playerStats = Creative.getOfflineStats(ownerData.playerId);
                        if (playerStats == null || playerStats.dropPlacements()){
                            for (ItemStack drop : deathEvent.getDrops()) {
                                entity.getWorld().dropItemNaturally(entity.getLocation().add(0, 0.2, 0), drop);
                            }
                        }
                    }
                }
                entity.remove();
            } else {
                if (Creative.get(player).getStats().blockDrops()) {
                    final Placement.OwnerData ownerData = Placement.getEntityOwner(entity);
                    PlayerStats playerStats = null;
                    if (ownerData != null && ownerData.state.equals(String.valueOf(entity.getEntityId()))) playerStats = Creative.getOfflineStats(ownerData.playerId);
                    if (playerStats == null || playerStats.dropPlacements()) {
                        entity.getWorld().dropItemNaturally(entity.getLocation().add(0, 0.2, 0), item);
                    }
                }
                entity.remove();
            }
        }
    }

    /**
     * Instantly removes hanging entities (paintings, item frames) when broken by fake creative players.
     *
     * @param event - HangingBreakByEntityEvent
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInstantBreakHangingEntity(final HangingBreakByEntityEvent event) {
        final Entity remover = event.getRemover();
        final Entity entity = event.getEntity();
        if (remover instanceof Player) {
            final Player player = (Player) remover;
            setBreakTime(player);
            if (!Creative.get(player).getStats().blockDrops()) {
                event.setCancelled(true);
                entity.remove();
            }
        }
    }

    /**
     * Allows the player to instantly destroy vehicles (boats, minecarts, etc.) in creative mode.
     *
     * @param event - VehicleDamageEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInstantBreakVehicle(final VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player) || !Creative.isCreativeMode((Player) event.getAttacker(), true)) return;
        final Player player = (Player) event.getAttacker();
        final Vehicle vehicle = event.getVehicle();
        final ItemStack item = ItemHandler.getEntityItem(vehicle);
        if (item.getType() != Material.AIR && canBreak(player)) {
            event.setCancelled(true);
            final VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, player);
            Bukkit.getPluginManager().callEvent(destroyEvent);
            if (!destroyEvent.isCancelled()) {
                if (Creative.get(player).getStats().blockDrops()) {
                    final Placement.OwnerData ownerData = Placement.getEntityOwner(vehicle);
                    PlayerStats playerStats = null;
                    if (ownerData != null && ownerData.state.equals(String.valueOf(vehicle.getEntityId()))) playerStats = Creative.getOfflineStats(ownerData.playerId);
                    if (playerStats == null || playerStats.dropPlacements()) {
                        vehicle.getWorld().dropItemNaturally(vehicle.getLocation().add(0, 0.2, 0), item);
                    }
                }
                vehicle.remove();
            }
        }
    }

    /**
     * Sets the players last break time.
     *
     * @param player - The player to be rate limited
     */
    public static void setBreakTime(final Player player) {
        final String playerId = PlayerHandler.getPlayerID(player);
        final long now = System.currentTimeMillis();
        lastBreakTime.put(playerId, now);
    }

    /**
     * Checks if the player can break based on rate limiting.
     *
     * @param player - The player to check
     * @return True if they can break, false if rate limited
     */
    public static boolean canBreak(final Player player) {
        final String playerId = PlayerHandler.getPlayerID(player);
        final long now = System.currentTimeMillis();
        if (now - (lastBreakTime.getOrDefault(playerId, 0L)) < (Creative.get(player).getStats().breakSpeed() * 45)) return false;
        lastBreakTime.put(playerId, now);
        return true;
    }

    /**
     * Simple data class to hold interaction information.
     */
    public static class InteractionData {
        final Action action;
        final Block block;
        Long lastSwingTime;
        InteractionData(final Action action, final Block block) {
            this.action = action;
            this.block = block;
            this.lastSwingTime = null;
        }
    }
}