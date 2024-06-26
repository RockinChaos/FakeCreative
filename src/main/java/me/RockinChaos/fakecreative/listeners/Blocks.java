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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Objects;

public class Blocks implements Listener {

    private final HashMap<String, Action> handMap = new HashMap<>();
    private final HashMap<String, Long> swingDelay = new HashMap<>();

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
            event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, event.getBlock().getType(), 6);
            if (!Creative.get(event.getPlayer()).getStats().blockDrops()) {
                event.getBlock().setType(Material.AIR);
            } else {
                event.getBlock().breakNaturally();
            }
        }
    }

    /**
     * Prevents glitching with the PlayerAnimationEvent when dropping items.
     *
     * @param event - PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (Creative.isCreativeMode(player, true) && this.handMap.get(PlayerHandler.getPlayerID(player)) != null) {
            this.handMap.remove(PlayerHandler.getPlayerID(player));
        }
    }

    /**
     * Prevents glitching with the PlayerAnimationEvent when interacting with blocks.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (Creative.isCreativeMode(player, true) && !PlayerHandler.isMenuClick(player, event.getAction())) {
            final Block block = PlayerHandler.getTargetBlock(player, 6);
            if (this.handMap.get(PlayerHandler.getPlayerID(player)) == null || !this.handMap.get(PlayerHandler.getPlayerID(player)).equals(event.getAction())) {
                if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) && (block != null && block.getType() != Material.AIR)) {
                    this.handMap.put(PlayerHandler.getPlayerID(player), Action.RIGHT_CLICK_AIR);
                } else {
                    this.handMap.put(PlayerHandler.getPlayerID(player), event.getAction());
                }
            } else if (this.handMap.get(PlayerHandler.getPlayerID(player)) != event.getAction()) {
                this.handMap.remove(PlayerHandler.getPlayerID(player));
                if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) && (block != null && block.getType() != Material.AIR)) {
                    this.handMap.put(PlayerHandler.getPlayerID(player), Action.RIGHT_CLICK_AIR);
                } else {
                    this.handMap.put(PlayerHandler.getPlayerID(player), event.getAction());
                }
            }
        }
    }

    /**
     * Allows the Player to instantly break any block.
     *
     * @param event - PlayerAnimationEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInstantBreak(final PlayerAnimationEvent event) {
        final Player player = event.getPlayer();
        if (Creative.isCreativeMode(player, true) && this.handMap.get(PlayerHandler.getPlayerID(player)) != null && StringUtils.containsIgnoreCase(this.handMap.get(PlayerHandler.getPlayerID(player)).name(), "LEFT") && (!Objects.requireNonNull(PlayerHandler.getMainHandItem(player)).getType().name().contains("SWORD") || !Creative.get(player).getStats().swordBlock())) {
            final Block block = PlayerHandler.getTargetBlock(player, 6);
            long dupeDuration = !this.swingDelay.isEmpty() && this.swingDelay.get(PlayerHandler.getPlayerID(player)) != null ? System.currentTimeMillis() - this.swingDelay.get(PlayerHandler.getPlayerID(player)) : -1;
            if ((dupeDuration == -1 || dupeDuration > Creative.get(player).getStats().breakSpeed() * 45) && Creative.isCreativeMode(player, true) && block != null) {
                this.swingDelay.put(PlayerHandler.getPlayerID(player), System.currentTimeMillis());
                Bukkit.getPluginManager().callEvent(new BlockBreakEvent(block, player));
            }
        }
    }
}