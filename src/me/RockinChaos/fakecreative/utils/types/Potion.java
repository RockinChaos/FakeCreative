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

import java.util.Arrays;
import java.util.List;

import org.bukkit.potion.PotionType;

public enum Potion {
	REGEN(Arrays.asList(8193, 8225, 8257, 16385, 16417, 16449)),
	SPEED(Arrays.asList(8194, 8226, 8258, 16386, 16418, 16450)),
	FIRE_RESISTANCE(Arrays.asList(8227, 8259, 16419, 16451)),
	POISON(Arrays.asList(8196, 8228, 8260, 16388, 16420, 16452)),
	INSTANT_HEAL(Arrays.asList(8261, 8229, 16453, 16421)),
	NIGHT_VISION(Arrays.asList(8230, 8262, 16422, 16454)),
	WEAKNESS(Arrays.asList(8232, 8264, 16424, 16456)),
	STRENGTH(Arrays.asList(8201, 8233, 8265, 16393, 16425, 16457)),
	SLOWNESS(Arrays.asList(8234, 8266, 16426, 16458)),
	JUMP(Arrays.asList(8203, 8235, 8267, 16395, 16427, 16459)),
	INSTANT_DAMAGE(Arrays.asList(8268, 8236, 16460, 16428)),
	WATER_BREATHING(Arrays.asList(8237, 8269, 16429, 16461)),
	INVISIBILITY(Arrays.asList(8238, 8270, 16430, 16462));
    	
	private List < Integer > id;
	
	Potion(final List < Integer > id) { this.id = id; }

	/**
	* Attempts to get the PotionType bottles DataValue.
	* 
	* @param potionType - The potion to be fetched.
	* @return The id of the PotionType.
	*/
	public static List < Integer > getData(final PotionType potionType) {
	    for (Potion tag: Potion.values()) {
	    	final String potionName = potionType.name().toUpperCase();
	    	if (tag.name().equalsIgnoreCase(potionName)) {
	        	return tag.id;
	        }
	    }
	    return Arrays.asList();
	}
}