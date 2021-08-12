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

public enum Combat {
    BOW,
    ARROW,
    SWORD,
    ELYTRA,
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    ENCHANTED_BOOK,
    SHIELD,
    TOTEM_OF_UNDYING,
    TRIDENT,
    CROSSBOW;
    	
	/**
	* Checks if the Material is a Combat type.
	* 
	* @param material - The Material being checked.
	* @return If the Material is a Combat type.
	*/
	public static boolean isCombat(final Material material) {
	    if (material.isBlock()) { return false; }
	    for (Combat tag: Combat.values()) {
	    	final String[] mats = material.name().split("_");
	    	if (tag.name().equalsIgnoreCase((tag.name().contains("_") ? material.name() : (mats.length > 1 ? mats[(mats.length - 1)] : mats[0])))) {
	        	return true;
	        }
	    }
	    return false;
	}
}