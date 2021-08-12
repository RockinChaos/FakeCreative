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

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum Monster {
	ELDER_GUARDIAN(4),
	WITHER_SKELETON(5),
	STRAY(6),
	HUSK(23),
	ZOMBIE_VILLAGER(27),
	SKELETON_HORSE(28),
	ZOMBIE_HORSE(29),
	DONKEY(31),
	MULE(32),
	EVOKER(34),
	VEX(35),
	VINDICATOR(36),
	CREEPER(50),
	SKELETON(51),
	SPIDER(52),
	ZOMBIE(54),
	SLIME(55),
	GHAST(56),
	ZOMBIE_PIGMAN(57),
	ENDERMAN(58),
	CAVE_SPIDER(59),
	SILVERFISH(60),
	BLAZE(61),
	MAGMA_CUBE(62),
	BAT(65),
	WITCH(66),
	ENDERMITE(67),
	GUARDIAN(68),
	SHULKER(69),
	PIG(90),
	SHEEP(91),
	COW(92),
	CHICKEN(93),
	SQUID(94),
	WOLF(95),
	MOOSHROOM(96),
	OCELOT(98),
	HORSE(100),
	RABBIT(101),
	POLAR_BEAR(102),
	LLAMA(103),
	PARROT(105),
	VILLAGER(120);
    	
	public int mobId;
	
	Monster(int i) { this.mobId = i; }

	/**
	* Attempts to get the Mob Spawn Egg DataValue.
	* 
	* @param mob - The mob to be fetched.
	* @return The id of the Entity.
	*/
	public static int getId(final Entity mob) {
	    for (Monster tag: Monster.values()) {
	    	final String mobName = mob.getType().name().toUpperCase();
	    	if (tag.name().replace("_", " ").equalsIgnoreCase(mobName)) {
	        	return tag.mobId;
	        }
	    }
	    return 0;
	}
	
	/**
	* Attempts to get the Mob Spawn Egg DataValue.
	* 
	* @param mob - The mob to be fetched.
	* @return The id of the EntityType.
	*/
	public static int getId(final EntityType mob) {
	    for (Monster tag: Monster.values()) {
	    	final String mobName = mob.name().toUpperCase();
	    	if (tag.name().replace("_", " ").equalsIgnoreCase(mobName)) {
	        	return tag.mobId;
	        }
	    }
	    return 0;
	}
}