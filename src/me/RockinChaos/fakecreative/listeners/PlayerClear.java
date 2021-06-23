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

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import me.RockinChaos.fakecreative.handlers.ItemHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.modes.Creative;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;

public class PlayerClear implements Listener {

   /**
	* Prevents the player from clearing their creative tabs.
    * 
	* @param event - PlayerCommandPreprocessEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		final String command = event.getMessage();
		if (PlayerHandler.isFakeCreativeMode(event.getPlayer()) && command != null && !command.isEmpty() && StringUtils.containsIgnoreCase(command, "clear")) {
			SchedulerUtils.runLater(2L, () -> { 
				if (ItemHandler.isContentsEmpty(event.getPlayer().getOpenInventory().getTopInventory().getContents())) {
					Creative.setTabs(event.getPlayer());
				}
			});
		}
	}
}