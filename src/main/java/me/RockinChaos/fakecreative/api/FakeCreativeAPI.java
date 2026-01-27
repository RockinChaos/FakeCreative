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
package me.RockinChaos.fakecreative.api;

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class FakeCreativeAPI {

    /**
     * Checks if the Player is in creative mode,
     * this will check if the Player is in either default Creative or FakeCreative.
     *
     * @param player - The Player to be checked.
     * @param isFake - If the player should be checked for fake creative mode.
     */
    public void isCreativeMode(final Player player, final boolean isFake) {
        Creative.isCreativeMode(player, isFake);
    }

    /**
     * Attempts to set the Player to FakeCreative.
     *
     * @param player - The Player being referenced.
     */
    public void setCreative(final Player player) {
        PlayerHandler.setCreative(player, null, false);
    }

    /**
     * Attempts to set the Player to Survival.
     *
     * @param player - The Player being referenced.
     */
    public void setSurvival(final Player player) {
        PlayerHandler.setMode(player, null, GameMode.SURVIVAL, false, false);
    }

    /**
     * Attempts to set the Player to Adventure.
     *
     * @param player - The Player being referenced.
     */
    public void setAdventure(final Player player) {
        PlayerHandler.setMode(player, null, GameMode.ADVENTURE, false, false);
    }

    /**
     * Attempts to set the Player to Spectator.
     *
     * @param player - The Player being referenced.
     */
    public void setSpectator(final Player player) {
        PlayerHandler.setMode(player, null, GameMode.SPECTATOR, false, false);
    }
}