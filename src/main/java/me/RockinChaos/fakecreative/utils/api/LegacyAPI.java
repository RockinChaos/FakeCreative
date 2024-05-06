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
package me.RockinChaos.fakecreative.utils.api;

import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.listeners.Depletion;

/**
 * Welcome to the magical land of make-believe.
 * These are Deprecated Legacy Methods and/or non-functioning methods
 * that exist to support legacy versions of Minecraft.
 */
@SuppressWarnings("deprecation")
public class LegacyAPI {

    /**
     * Registers the Legacy Depletion Listener.
     * Only called when the Server version is below 1.14.
     */
    public static void registerDepletion() {
        if (ServerUtils.hasSpecificUpdate("1_14")) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Depletion(), FakeCreative.getCore().getPlugin());
        } else {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new me.RockinChaos.fakecreative.listeners.legacy.Legacy_Depletion(), FakeCreative.getCore().getPlugin());
        }
    }

    /**
     * Registers the Legacy Invulnerable Listener.
     * Only called when the Server version is below 1.9.
     */
    public static void registerInvulnerable() {
        if (!ServerUtils.hasSpecificUpdate("1_9")) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new me.RockinChaos.fakecreative.listeners.legacy.Legacy_Invulnerable(), FakeCreative.getCore().getPlugin());
        }
    }
}