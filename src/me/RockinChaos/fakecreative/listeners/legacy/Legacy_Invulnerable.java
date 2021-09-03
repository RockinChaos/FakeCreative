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
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;

/**
* Handles the Damage for Creative Players.
* 
* @deprecated This is a LEGACY listener, only use on Minecraft versions below 1.9.
*/
public class Legacy_Invulnerable implements Listener {

   /**
	* Prevents the Player from taking damage.
	* 
	* @param event - EntityDamageByEntity
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.9.
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onEntityDamagePlayer(final EntityDamageByEntityEvent event) {
        if ((event.getEntity() instanceof Player) && PlayerHandler.isCreativeMode((Player)event.getEntity(), true) && PlayerHandler.getCreativeStats((Player)event.getEntity()).god()) {
            event.setCancelled(true);
        }
    }
    
   /**
	* Prevents the Player from taking damage.
	* 
	* @param event - EntityDamageByBlockEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.9.
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockDamagePlayer(final EntityDamageByBlockEvent event) {
        if ((event.getEntity() instanceof Player) && PlayerHandler.isCreativeMode((Player)event.getEntity(), true) && PlayerHandler.getCreativeStats((Player)event.getEntity()).god()) {
            event.setCancelled(true);
        }
    }
    
   /**
	* Prevents the Player from taking damage.
	* 
	* @param event - EntityDamageEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.9.
	*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerDamage(final EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player) && PlayerHandler.isCreativeMode((Player)event.getEntity(), true) && PlayerHandler.getCreativeStats((Player)event.getEntity()).god()) {
            event.setCancelled(true);
        }
    }
}