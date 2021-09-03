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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import me.RockinChaos.fakecreative.api.events.PlayerEnterCreativeEvent;
import me.RockinChaos.fakecreative.api.events.PlayerExitCreativeEvent;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.modes.Creative;
import me.RockinChaos.fakecreative.utils.ServerUtils;

public class Gamemode implements Listener {

   /**
	* Removes the player from fake creative when they attempt to change their gamemode.
	* 
	* @param event - PlayerGameModeChangeEvent
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerSwitchGameMode(final PlayerGameModeChangeEvent event) {
    	Player player = event.getPlayer();
    	if (PlayerHandler.isCreativeMode(player, true)) {
    		PlayerHandler.setMode(player, null, event.getNewGameMode(), true, false);
    	}
    }
    
   /**
	* Triggered when the Player enters fake creative.
	* 
	* @param event - PlayerEnterCreativeEvent
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerEnterCreative(final PlayerEnterCreativeEvent event) {
    	if (!event.isCancelled()) { 
	    	Creative.setCreative(event.getSender(), event.getPlayer(), event.isRefresh(), event.isRestore(), event.isSilent());
    	} else if (!PlayerHandler.isCreativeMode(event.getPlayer(), true)) { ServerUtils.logDebug("Event was cancelled, " + event.getPlayer().getName() + " was not set to fake creative."); }
    }
    
   /**
	* Triggered when the Player exits fake creative.
	* 
	* @param event - PlayerExitCreativeEvent
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerExitCreative(final PlayerExitCreativeEvent event) {
    	if (!event.isCancelled()) { 
	    	Creative.setMode(event.getSender(), event.getPlayer(), event.getGameMode(), event.isSilent(), event.isDoSave());
    	} else if (PlayerHandler.isCreativeMode(event.getPlayer(), true)) { ServerUtils.logDebug("Event was cancelled, " + event.getPlayer().getName() + " still remains set as fake creative."); }
    }
}