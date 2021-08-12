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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;

import me.RockinChaos.fakecreative.utils.interfaces.Interface;

public class Interfaces implements Listener {

  	private Interface expiredInventory;

   /**
    * Handles the click action for the virtualInventory.
    * 
    * @param event - InventoryClickEvent
    */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onClick(final InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Interface) {
			((Interface) holder).onClick(event);
    		this.expiredInventory = ((Interface) holder);
		}
	}

   /**
	* Handles the chat action for the virtualInventory.
	* 
	* @param event - AsyncPlayerChatEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onChat(final AsyncPlayerChatEvent event) {
		if (this.expiredInventory != null && this.expiredInventory.chatPending()) {
			this.expiredInventory.onChat(event);
		}
	}
}