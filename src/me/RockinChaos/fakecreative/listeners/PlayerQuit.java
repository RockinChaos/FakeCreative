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

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.RockinChaos.fakecreative.handlers.PlayerHandler;

public class PlayerQuit implements Listener {

   /**
	* Attempts to remove the player from creative upon exiting the server.
    * 
	* @param event - PlayerQuitEvent
	*/
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerQuit(final PlayerQuitEvent event) {
	    if (PlayerHandler.isFakeCreativeMode(event.getPlayer())) {
	    	PlayerHandler.setMode(event.getPlayer(), null, event.getPlayer().getGameMode(), true);
	    }
    }
}