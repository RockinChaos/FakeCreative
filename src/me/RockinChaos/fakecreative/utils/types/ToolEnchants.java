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

import org.bukkit.enchantments.Enchantment;

public enum ToolEnchants {
	DIG_SPEED,
	SILK_TOUCH,
	DURABILITY,
	LOOT_BONUS_BLOCKS,
	LUCK,
	LURE,
	MENDING,
	VANISHING_CURSE;
    	
	/**
	* Checks if the Enchantment is a Tool Enchant type.
	* 
	* @param enchant - The Enchantment being checked.
	* @return If the Enchantment is a Tool Enchant type.
	*/
	public static boolean isEnchant(final Enchantment enchant) {
		for (ToolEnchants ench: ToolEnchants.values()) {
			if (enchant.toString().split(", ")[1].replace("]", "").equalsIgnoreCase(ench.name())) {
				return true;
			}
		}
		return false;
	}
}