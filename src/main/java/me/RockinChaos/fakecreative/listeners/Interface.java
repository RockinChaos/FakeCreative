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
import me.RockinChaos.fakecreative.utils.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Interface implements Listener {

    /**
     * Handles the inventory close action for the virtualInventory.
     *
     * @param event - InventoryCloseEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        if (Menu.modifyMenu(player)) {
            SchedulerUtils.runAsyncLater(40L, () -> {
                if (!Menu.isOpen(player)) {
                    Menu.setModifyMenu(false, (Player) event.getPlayer());
                }
            });
        }
    }
}