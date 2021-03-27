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
package me.RockinChaos.fakecreative.handlers.modes.instance;

import java.util.HashMap;

import me.RockinChaos.fakecreative.utils.sql.DataObject;
import me.RockinChaos.fakecreative.utils.sql.SQL;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;

public class PlayerObject {
	
	private String playerID;
	private String inventory64;
	private double health;
	private double maxHealth;
	private int food;
	private int fireTicks;
	private HashMap<Integer, String> hotbars = new HashMap<Integer, String>();
	
   /**
    * Creates a new PlayerObject instance
    * 
    * @param playerID - The player being accessed.
    * @param object - The Object being referenced.
    */
	public PlayerObject(String playerID, String inventory64, double health, double maxHealth, int food, int fireTicks) {
		this.playerID = playerID;
		this.inventory64 = inventory64;
		this.health = health;
		this.maxHealth = maxHealth;
		this.food = food;
		this.fireTicks = fireTicks;
		for (int i = 1; i <= 9; i++) {
			DataObject dataObject = SQL.getData().getData(new DataObject(Table.HOTBAR, playerID, "", Integer.toString(i), ""));
			if (dataObject != null) {
				this.hotbars.put(Integer.valueOf(dataObject.getPosition()), dataObject.getInventory64());
			}
		}
	}
	
   /**
    * Gets the Guild of the GuildObject
    * 
    * @return The Guild.
    */
	public String getPlayer() {
		return this.playerID;
	}
	
   /**
    * Gets the Object of the GuildObject
    * 
    * @return The Object.
    */
	public double getHealth() {
		return this.health;
	}
	
   /**
    * Gets the Object of the GuildObject
    * 
    * @return The Object.
    */
	public double getMaxHealth() {
		return this.maxHealth;
	}
	
   /**
    * Gets the Object of the GuildObject
    * 
    * @return The Object.
    */
	public int getFood() {
		return this.food;
	}
	
   /**
    * Gets the Object of the GuildObject
    * 
    * @return The Object.
    */
	public int getFireTicks() {
		return this.fireTicks;
	}
	
   /**
    * Gets the Object of the GuildObject
    * 
    * @return The Object.
    */
	public String getInventory64() {
		return this.inventory64;
	}
	
	public void setHotbars(final HashMap<Integer, String> hotbars) {
		this.hotbars = hotbars;
	}
	
	public HashMap<Integer, String> getHotbars() {
		return this.hotbars;
	}
}