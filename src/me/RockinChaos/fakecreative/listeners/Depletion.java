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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

import me.RockinChaos.fakecreative.handlers.ConfigHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;

public class Depletion implements Listener {

   /**
	* Protects the Player from hunger depletion.
	* 
	* @param event - FoodLevelChangeEvent
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onHunger(FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player) && PlayerHandler.isFakeCreativeMode((Player)event.getEntity()) && !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Creative.hunger-depletion")) {
            event.setCancelled(true);
        }
    }
    
   /**
	* Prevents the Player from damaging any items.
	* 
	* @param event - PlayerItemDamageEvent
	*/
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDurability(final PlayerItemDamageEvent event) {
    	if (PlayerHandler.isFakeCreativeMode((Player)event.getPlayer()) && ConfigHandler.getConfig().getFile("config.yml").getBoolean("Creative.unbreakable-items")) {
    		event.setCancelled(true);
    	}
    }
}