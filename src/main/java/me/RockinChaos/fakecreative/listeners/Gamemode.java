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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.protocol.events.PlayerEnterCreativeEvent;
import me.RockinChaos.core.utils.protocol.events.PlayerExitCreativeEvent;
import me.RockinChaos.fakecreative.modes.Mode;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class Gamemode implements Listener {

    /**
     * Removes the player from fake creative when they attempt to change their gamemode.
     *
     * @param event - PlayerGameModeChangeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerSwitchGameMode(final PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (Creative.isCreativeMode(player, true)) {
            PlayerHandler.setMode(player, null, event.getNewGameMode(), true, false);
        }
    }

    /**
     * Triggered when the Player enters fake creative.
     *
     * @param event - PlayerEnterCreativeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerEnterCreative(final PlayerEnterCreativeEvent event) {
        if (!event.isCancelled()) {
            Creative.setCreative(event.getSender(), event.getPlayer(), event.isRefresh(), event.isRestore(), event.isSilent());
        } else if (!Creative.isCreativeMode(event.getPlayer(), true)) {
            ServerUtils.logDebug("Event was cancelled, " + event.getPlayer().getName() + " was not set to fake creative.");
        }
    }

    /**
     * Triggered when the Player exits fake creative.
     *
     * @param event - PlayerExitCreativeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerExitCreative(final PlayerExitCreativeEvent event) {
        if (!event.isCancelled()) {
            Mode.setMode(event.getSender(), event.getPlayer(), event.getGameMode(), event.isSilent(), event.isDoSave());
        } else if (Creative.isCreativeMode(event.getPlayer(), true)) {
            ServerUtils.logDebug("Event was cancelled, " + event.getPlayer().getName() + " still remains set as fake creative.");
        }
    }

    /**
     * Called when a player switches worlds.
     * Restores flight capabilities to the fake creative player if they were lost during the world change.
     *
     * @param event - PlayerChangedWorldEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerWorldSwitch(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        SchedulerUtils.run(() -> {
            if (Creative.isCreativeMode(player, true) && !player.getAllowFlight()) {
                Mode.setFlight(player, true);
            }
        });
    }
}