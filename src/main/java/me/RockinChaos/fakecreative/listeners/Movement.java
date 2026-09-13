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

import me.RockinChaos.core.utils.ReflectionUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Movement implements Listener {
    private final Class<?> humanEntity = ReflectionUtils.getMinecraftClass("EntityHuman");

    /**
     * Handles burning when the Player is caught on fire.
     *
     * @param event PlayerMoveEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onMovement(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player.getFireTicks() > 0 && Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().allowBurn()) {
            player.setFireTicks(0);
        }
    }

    /**
     * Handles instant teleporting when the Player is touching a portal block.
     *
     * @param event PlayerMoveEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPortalMovement(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (Creative.isCreativeMode(player, true) && Creative.get(player).getStats().instantPortal() && event.getTo() != null && !this.isPortal(event.getFrom().getBlock()) && this.isPortal(event.getTo().getBlock())) {
            setInvulnerable(player, true);
            SchedulerUtils.runPlayerLater(player, 4L, () -> setInvulnerable(player, false));
        }
    }

    /**
     * Sets the PlayerAbilities invulnerable flag for the specified player.
     * This is used to emulate Creative modes instant portal travel.
     *
     * @param player The player to set the invulnerable flag for.
     * @param invulnerable Whether the player should be invulnerable.
     */
    private void setInvulnerable(final Player player, final boolean invulnerable) {
        try {
            final Object nmsPlayer = ReflectionUtils.getEntity(player);
            final Object abilities = ReflectionUtils.getField(humanEntity, ReflectionUtils.MinecraftField.PlayerAbilities.getField()).get(nmsPlayer);
            ReflectionUtils.getField(abilities.getClass(), ReflectionUtils.MinecraftField.Invulnerable.getField()).set(abilities, invulnerable);
        } catch (Exception e) {
            ServerUtils.logSevere("{MOVEMENT} Failed to set invulnerable for " + player.getName() + ".");
            ServerUtils.sendSevereTrace(e);
        }
    }

    /**
     * Checks if the specified block is a Portal.
     *
     * @param block The block to check.
     * @return Whether the block is a Portal.
     */
    public boolean isPortal(final Block block) {
        final String type = block.getType().name();
        return type.equals("NETHER_PORTAL") || type.equals("PORTAL");
    }
}