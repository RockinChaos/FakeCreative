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

import java.util.HashMap;

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
import org.bukkit.event.player.PlayerInteractEvent;

import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.modes.instance.PlayerPreferences;
import me.RockinChaos.fakecreative.utils.StringUtils;

public class Blocks implements Listener {
	
   /**
	* Prevents the player from damaging blocks.
	* 
	* @param event - BlockDamageEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onDamage(final BlockDamageEvent event) {
		if (PlayerHandler.isFakeCreativeMode((Player) event.getPlayer())) {
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
		if (PlayerHandler.isFakeCreativeMode((Player) event.getPlayer())) {
			event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, event.getBlock().getType(), 6);
			if (!PlayerPreferences.blockDrops((Player) event.getPlayer())) {
				event.getBlock().setType(Material.AIR);
			} else {
				event.getBlock().breakNaturally();
			}
		}
	}
	
   /**
	* Allows the Player to instantly break any block.
	* 
	* @param event - PlayerAnimationEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onInstantBreak(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (PlayerHandler.isFakeCreativeMode(player)) {
			handMap.put(PlayerHandler.getPlayerID(player), event.getAction());
		}
	}
	private HashMap < String, Action > handMap = new HashMap < String, Action > ();
	
   /**
	* Allows the Player to instantly break any block.
	* 
	* @param event - PlayerAnimationEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInstantBreak(PlayerAnimationEvent event) {
		final Player player = event.getPlayer();
		if (PlayerHandler.isFakeCreativeMode(player) && StringUtils.containsIgnoreCase(handMap.get(PlayerHandler.getPlayerID(player)).name(), "LEFT") && (PlayerHandler.getMainHandItem(player) == null || (!PlayerHandler.getMainHandItem(player).getType().name().contains("SWORD")) || !PlayerPreferences.swordBlock(player))) {
			final Block block = player.getTargetBlock(null, 6);
			long dupeDuration = (this.swingDelay != null && !this.swingDelay.isEmpty() && this.swingDelay.get(PlayerHandler.getPlayerID(player)) != null ? (((System.currentTimeMillis()) - this.swingDelay.get(PlayerHandler.getPlayerID(player)))) : -1);
			if ((dupeDuration == -1 || dupeDuration > PlayerPreferences.breakSpeed(player) * 45) && PlayerHandler.isFakeCreativeMode(player)) {
				this.swingDelay.put(PlayerHandler.getPlayerID(player), System.currentTimeMillis());
				Bukkit.getPluginManager().callEvent(new BlockBreakEvent(block, player));
			}
		}
	}
	private HashMap < String, Long > swingDelay = new HashMap < String, Long > ();
}