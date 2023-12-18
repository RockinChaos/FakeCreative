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

import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class Pickups implements Listener {

    /**
     * Destroys any items the Player attempts to pickup in creative if their inventory is full.
     *
     * @param event - PlayerMoveEvent
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onItemPickup(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        SchedulerUtils.runAsync(() -> {
            if (Creative.isCreativeMode(player, true) && Creative.get(player).getStats().destroyPickups() && player.getInventory().firstEmpty() == -1) {
                SchedulerUtils.run(() -> {
                    for (Entity item : player.getNearbyEntities(0.5, 2, 0.5)) {
                        if (item instanceof Item) {
                            final Item itemDrop = (Item) item;
                            final ItemStack itemStack = itemDrop.getItemStack();
                            itemStack.setAmount(0);
                            itemStack.setType(Material.AIR);
                            itemDrop.remove();
                        }
                    }
                });
            }
        });
    }
}