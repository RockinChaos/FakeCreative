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
package me.RockinChaos.fakecreative.listeners.legacy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;

/**
* Handles the Stat Depletion for Creative Players.
* 
* @deprecated This is a LEGACY listener, only use on Minecraft versions below 1.8.
*/
public class Legacy_Depletion implements Listener {

   /**
	* Protects the Player from hunger depletion.
	* 
	* @param event - FoodLevelChangeEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onHunger(final FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player) && PlayerHandler.isCreativeMode((Player)event.getEntity(), true) && !PlayerHandler.getCreativeStats((Player)event.getEntity()).allowHunger()) {
            event.setCancelled(true);
        }
    }
    
   /**
	* Prevents the Player from damaging any items.
	* 
	* @param event - PlayerItemDamageEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDurability(final PlayerAnimationEvent event) {
    	if (PlayerHandler.isCreativeMode((Player)event.getPlayer(), true) && PlayerHandler.getCreativeStats((Player)event.getPlayer()).unbreakableItems()) {
    		SchedulerUtils.runAsync(() -> PlayerHandler.getHandItem(event.getPlayer()).setDurability((short)0));
    		PlayerHandler.updateInventory(event.getPlayer(), 0);
    	}
    }
}