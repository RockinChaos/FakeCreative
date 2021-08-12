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

import me.RockinChaos.fakecreative.handlers.PlayerHandler;

public class PlayerObject {
	
	private PlayerStats playerStats;
	private String playerID;
	private String inventory64;
	private double health;
	private double maxHealth;
	private int food;
	private int fireTicks;
	
   /**
    * Creates a new PlayerObject instance
    * 
    * @param playerID - The player being accessed.
    * @param object - The Object being referenced.
    */
	public PlayerObject(final String playerID, final double health, final double maxHealth, final int food, final int fireTicks) {
		this.playerID = playerID;
		this.health = health;
		this.maxHealth = maxHealth;
		this.food = food;
		this.fireTicks = fireTicks;
	}
	
   /**
    * Gets the Player UUID of the PlayerObject.
    * 
    * @return The Player UUID.
    */
	public String getPlayer() {
		return this.playerID;
	}
	
   /**
    * Gets the Health of the PlayerObject.
    * 
    * @return The Health.
    */
	public double getHealth() {
		return this.health;
	}
	
   /**
    * Gets the Health Scale of the PlayerObject.
    * 
    * @return The Health Scale.
    */
	public double getMaxHealth() {
		return this.maxHealth;
	}
	
   /**
    * Gets the Food Level of the PlayerObject.
    * 
    * @return The Food Level.
    */
	public int getFood() {
		return this.food;
	}
	
   /**
    * Gets the Fire Ticks of the PlayerObject.
    * 
    * @return The Fire Ticks.
    */
	public int getFireTicks() {
		return this.fireTicks;
	}
	
   /**
    * Gets the Inventory64 of the PlayerObject.
    * 
    * @return The Inventory64.
    */
	public String getInventory64() {
		return this.inventory64;
	}
	
   /**
    * Sets the String Byte of the Inventory64 for the PlayerObject.
    * 
    * @param inventory64 - The Inventory64 to be saved.
    */
	public void setInventory64(final String inventory64) {
		this.inventory64 = inventory64;
	}
	
   /**
    * Gets the PlayerStats of the PlayerObject.
    * 
    * @return The PlayerStats.
    */
	public PlayerStats getStats() {
		if (this.playerStats == null) { this.playerStats = new PlayerStats(PlayerHandler.getPlayerString(playerID)); }
		return this.playerStats;
	}
}