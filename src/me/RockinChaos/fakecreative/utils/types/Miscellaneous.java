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

public enum Miscellaneous {
    BEACON,
    EGG,
    CONDUIT,
    SCUTE,
    COAL,
    CHARCOAL,
    DIAMOND,
    INGOT,
    SCRAP,
    SADDLE,
    STICK,
    BOWL,
    STRING,
    FEATHER,
    GUNPOWDER,
    SEEDS,
    WHEAT,
    FLINT,
    BUCKET,
    SNOWBALL,
    LEATHER,
    BRICK,
    BALL,
    PAPER,
    BOOK,
    DUST,
    SAC,
    BEANS,
    LAZULI,
    DYE,
    MEAL,
    BONE,
    SUGAR,
    PEARL,
    BLAZE_ROD,
    NUGGET,
    WART,
    EYE,
    EXPERIENCE_BOTTLE,
    CHARGE,
    EMERALD,
    MAP,
    STAR,
    ROCKET,
    QUARTZ,
    SHARD,
    CRYSTALS,
    HIDE,
    ARMOR,
    FRUIT,
    SHELL,
    MUSIC,
    SEA,
    PATTERN,
    HONEYCOMB;
    	
	/**
	* Checks if the Material is a Miscellaneous type.
	* 
	* @param material - The Material being checked.
	* @return If the Material is a Miscellaneous type.
	*/
	public static boolean isMiscellaneous(final Material material) {
	    for (Miscellaneous tag: Miscellaneous.values()) {
	    	final String[] mats = material.name().split("_");
	    	if (mats[0].equalsIgnoreCase(MUSIC.name()) || (!material.name().equalsIgnoreCase("ENCHANTED_BOOK") && (tag.name().equalsIgnoreCase((tag.name().contains("_") ? material.name() : (mats.length > 1 ? mats[(mats.length - 1)] : mats[0])))))) {
	        	return true;
	        }
	    }
	    return false;
	}
}