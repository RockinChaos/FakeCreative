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
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.types.Monster;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.modes.creative.Creative.Tabs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Targeting implements Listener {

    /**
     * Prevents an Entity from targeting the Player while they are in creative.
     *
     * @param event - EntityTargetEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onEntityTarget(final EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player) && !(event.getEntity() instanceof Player) && Creative.isCreativeMode((Player) event.getTarget(), true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents an Entity from targeting the Player while they are in creative.
     *
     * @param event - EntityTargetLivingEntityEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onEntityTargetLiving(final EntityTargetLivingEntityEvent event) {
        if ((event.getTarget() instanceof Player) && !(event.getEntity() instanceof Player) && Creative.isCreativeMode((Player) event.getTarget(), true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Gives the player the spawn egg of the entity they attempt to pick block.
     *
     * @param event - PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerTargetEntity(final PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (!(entity instanceof Player) && Creative.isCreativeMode(event.getPlayer(), true) && Tabs.PICK_ITEM.isTab(PlayerHandler.getMainHandItem(event.getPlayer())) && (ServerUtils.hasSpecificUpdate("1_9") && event.getHand().toString().equalsIgnoreCase("HAND") || !ServerUtils.hasSpecificUpdate("1_9"))) {
            event.setCancelled(true);
            try {
                event.getPlayer().getInventory().addItem(new ItemStack(ItemHandler.getMaterial(entity.getType().name().toUpperCase() + "_SPAWN_EGG", null)));
            } catch (Exception e) {
                event.getPlayer().getInventory().addItem(LegacyAPI.newItemStack(ItemHandler.getMaterial("MONSTER_EGG", null), 1, (short) Monster.getId(entity.getType())));
            }
            PlayerHandler.updateInventory(event.getPlayer(), 0);
        }
    }
}