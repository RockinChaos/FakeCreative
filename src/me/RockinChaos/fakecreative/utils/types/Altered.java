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

import me.RockinChaos.fakecreative.handlers.ItemHandler;

public enum Altered {
    BAMBOO_SAPLING("BAMBOO"),
    KELP_PLANT("KELP"),
    TALL_SEAGRASS("SEAGRASS"),
    FIRE_CORAL_WALL_FAN("FIRE_CORAL_FAN"),
    BUBBLE_CORAL_WALL_FAN("BUBBLE_CORAL_FAN"),
    BRAIN_CORAL_WALL_FAN("BRAIN_CORAL_FAN"),
    TUBE_CORAL_WALL_FAN("TUBE_CORAL_FAN"),
    HORN_CORAL_WALL_FAN("HORN_CORAL_FAN"),
    DEAD_TUBE_CORAL_WALL_FAN("DEAD_TUBE_CORAL_FAN"),
    DEAD_BRAIN_CORAL_WALL_FAN("DEAD_BRAIN_CORAL_FAN"),
    DEAD_BUBBLE_CORAL_WALL_FAN("DEAD_BUBBLE_CORAL_FAN"),
    DEAD_FIRE_CORAL_WALL_FAN("DEAD_FIRE_CORAL_FAN"),
    DEAD_HORN_CORAL_WALL_FAN("DEAD_HORN_CORAL_FAN"),
    OAK_WALL_SIGN("OAK_SIGN"),
    SPRUCE_WALL_SIGN("SPRUCE_SIGN"),
    BIRCH_WALL_SIGN("BIRCH_SIGN"),
    JUNGLE_WALL_SIGN("JUNGLE_SIGN"),
    ACACIA_WALL_SIGN("ACACIA_SIGN"),
    DARK_OAK_WALL_SIGN("DARK_OAK_SIGN"),
    CRIMSON_WALL_SIGN("CRIMSON_SIGN"),
    WARPED_WALL_SIGN("WARPED_SIGN"),
    POTATOES("POTATO"),
    CARROTS("CARROT"),
    BEETROOTS("BEETROOT"),
    SWEET_BERRY_BUSH("SWEET_BERRIES");
    
	private final String fixedType;
	private Altered(final String fixedType) {
		this.fixedType = fixedType;
	}

	/**
	* Checks if the Material is a Altered type.
	* 
	* @param material - The Material being checked.
	* @return The correct Material if Altered.
	*/
	public static Material getAlter(final Material material) {
	    for (Altered tag: Altered.values()) {
	    	if (tag.name().equalsIgnoreCase(material.name())) {
	        	return ItemHandler.getMaterial(tag.fixedType, null);
	        }
	    }
	    return material;
	}
}