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
package me.RockinChaos.fakecreative.utils.types;

import org.bukkit.Material;

public enum Redstone {
    DISPENSER,
    NOTE_BLOCK,
    PISTON,
    TNT,
    LEVER,
    PLATE,
    RAIL,
    MINECART,
    REDSTONE_TORCH,
    TRAPDOOR,
    GATE,
    REDSTONE_LAMP,
    TRIPWIRE_HOOK,
    BUTTON,
    TRAPPED_CHEST,
    DAYLIGHT_DETECTOR,
    REDSTONE_BLOCK,
    REDSTONE,
    HOPPER,
    DROPPER,
    OBSERVER,
    DOOR,
    REPEATER,
    COMPARATOR,
    LECTERN,
    TARGET;
    	
	/**
	* Checks if the Material is a Redstone type.
	* 
	* @param material - The Material being checked.
	* @return If the Material is a Redstone type.
	*/
	public static boolean isRedstone(Material material) {
	    for (Redstone tag: Redstone.values()) {
	    	String[] mats = material.name().split("_");
	    	if (tag.name().equalsIgnoreCase((tag.name().contains("_") ? material.name() : (mats.length > 1 ? mats[(mats.length - 1)] : mats[0])))) {
	        	return true;
	        }
	    }
	    return false;
	}
}