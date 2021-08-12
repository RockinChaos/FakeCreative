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

public enum Decoration {
	SAPLING,
	LEAVES,
	COBWEB,
	GRASS,
	FERN,
	BUSH,
	SEAGRASS,
	PICKLE,
	FLOWER,
	DANDELION,
	POPPY,
	ORCHID,
	ALLIUM,
	BLUET,
	TULIP,
	DAISY,
	ROSE,
	CORNFLOWER,
	VALLEY,
	MUSHROOM,
	FUNGUS,
	ROOTS,
	SPROUTS,
	VINES,
	CANE,
	KELP,
	BAMBOO,
	TORCH,
	END_ROD,
	PLANT,
	CHEST,
	FARMLAND,
	FURNACE,
	TABLE,
	LADDER,
	SNOW,
	CACTUS,
	JUKEBOX,
	FENCE,
	INFESTED_STONE,
	INFESTED_COBBLESTONE,
	INFESTED_STONE_BRICKS,
	INFESTED_MOSSY_STONE_BRICKS,
	INFESTED_CRACKED_STONE_BRICKS,
	INFESTED_CHISELED_STONE_BRICKS,
	BROWN_MUSHROOM_BLOCK,
	RED_MUSHROOM_BLOCK,
	MUSHROOM_STEM,
	BARS,
	CHAIN,
	PANE,
	VINE,
	LILY_PAD,
	END_PORTAL_FRAME,
	WALL,
	ANVIL,
	CARPET,
	SLIME_BLOCK,
	PATH,
	SUNFLOWER,
	LILAC,
	PEONY,
	BOX,
	TERRACOTTA,
	CORAL,
	FAN,
	SCAFFOLDING,
	PAINTING,
	SIGN,
	BED,
	FRAME,
	POT,
	SKULL,
	HEAD,
	STAND,
	BANNER,
	CRYSTAL,
	LOOM,
	COMPOSTER,
	BARREL,
	SMOKER,
	GRINDSTONE,
	STONECUTTER,
	BELL,
	LANTERN,
	CAMPFIRE,
	SHROOMLIGHT,
	NEST,
	BEEHIVE,
	HONEY_BLOCK,
	HONEYCOMB_BLOCK,
	LODESTONE,
	ANCHOR;
    	
	/**
	* Checks if the Material is a Decoration type.
	* 
	* @param material - The Material being checked.
	* @return If the Material is a Decoration type.
	*/
	public static boolean isDecoration(final Material material) {
	    for (Decoration tag: Decoration.values()) {
	    	final String[] mats = material.name().split("_");
	    	if (tag.name().equalsIgnoreCase((tag.name().contains("_") ? material.name() : (mats.length > 1 ? mats[(mats.length - 1)] : mats[0])))) {
	        	return true;
	        }
	    }
	    return false;
	}
}