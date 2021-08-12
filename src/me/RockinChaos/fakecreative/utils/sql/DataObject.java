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
package me.RockinChaos.fakecreative.utils.sql;

import java.sql.Timestamp;

public class DataObject {
	
	private Table table = Table.HOTBAR;
	private Boolean isTemporary = false;
	private String playerId = new String();
	private String position = new String();
	private Boolean condition = null;
	private Double numerical = -1.0;
	private Integer integer = -1;
	private double health = -1.0;
	private double maxHealth = -1.0;
	private int food = -1;
	private int fireTicks = -101;
	private String inventory64 = new String();
	private String timeStamp = new String();
	
   /**
    * Gets the Player ID of the DataObject.
    * 
    * @return The Player ID.
    */
	public String getPlayerId() {
		return this.playerId;
	}
	
   /**
    * Gets the Position of the DataObject.
    * 
    * @return The Position.
    */
	public String getPosition() {
		return this.position;
	}
	
   /**
    * Gets the Health of the DataObject.
    * 
    * @return The Health.
    */
	public double getHealth() {
		return this.health;
	}
	
   /**
    * Gets the Health Scale of the DataObject.
    * 
    * @return The Health Scale.
    */
	public double getMaxHealth() {
		return this.maxHealth;
	}
	
   /**
    * Gets the Food Level of the DataObject.
    * 
    * @return The Food Level.
    */
	public int getFood() {
		return this.food;
	}
	
   /**
    * Gets the Fire Ticks of the DataObject.
    * 
    * @return The Fire Ticks.
    */
	public int getFireTicks() {
		return this.fireTicks;
	}
	
   /**
    * Gets the Inventory in 64 Hash of the DataObject
    * 
    * @return The Inventory in 64 Hash.
    */
	public String getInventory64() {
		return this.inventory64;
	}
	
   /**
    * Gets the Boolean of the DataObject.
    * 
    * @return The Boolean.
    */
	public Boolean getBoolean() {
		return this.condition;
	}
	
   /**
    * Gets the Double of the DataObject.
    * 
    * @return The Double.
    */
	public Double getDouble() {
		return this.numerical;
	}
	
   /**
    * Gets the Integer of the DataObject.
    * 
    * @return The Integer.
    */
	public Integer getInt() {
		return this.integer;
	}
	
   /**
    * Gets the Time Stamp of the DataObject.
    * 
    * @return The Time Stamp.
    */
	public String getTimeStamp() {
		return this.timeStamp;
	}
	
   /**
    * Sets the Time Stamp of the DataObject.
    * 
    * @param stamp - The Time Stamp.
    */
	public void setTimeStamp(final String stamp) {
		this.timeStamp = stamp;
	}

   /**
    * Gets the Table of the DataObject.
    * 
    * @return The Table.
    */	
	public Table getTable() {
		return this.table;
	}
	
   /**
    * Gets if the DataObject is only a reference.
    * 
    * @return If the DataObject is a reference.
    */	
	public boolean isTemporary() {
		return this.isTemporary;
	}
	
   /**
    * Creates a new DataObject instance.
    * 
    * @param table - The Table being accessed.
    * @param isTemporary - If the DataObject is only a reference.
    */
	public DataObject(Table table) {
		this.table = table;
		this.isTemporary = true;
	}
	
   /**
    * Creates a new DataObject instance.
    * 
    * @param table - The Table being accessed.
    * @param playerId - The Player UUID being referenced.
    * @param object1 - The Object being referenced.
    * @param object2 - The Object being referenced.
    * @param object3 - The Object being referenced.
    * @param object4 - The Object being referenced.
    * @param object5 - The Object being referenced.
    */
	public DataObject(final Table table, final String playerId, final String object1, final String object2, final String object3, final String object4, final String object5) {
		this.table = table;
		this.playerId = playerId;
		this.health = Double.valueOf(object1);
		this.maxHealth = Double.valueOf(object2);
		this.food = Integer.valueOf(object3);
		this.fireTicks = Integer.valueOf(object4);
		this.inventory64 = object5;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Creates a new DataObject instance.
    * 
    * @param table - The Table being accessed.
    * @param playerId - The Player UUID being referenced.
    * @param object1 - The Object being referenced.
    * @param object2 - The Object being referenced.
    */
	public DataObject(final Table table, String playerId, final String object1, final String object2) {
		this.table = table;
		this.playerId = playerId;
		this.position = object1;
		this.inventory64 = object2;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Creates a new DataObject instance.
    * 
    * @param table - The Table being accessed.
    * @param playerId - The Player UUID being referenced.
    * @param bool - The Boolean being referenced.
    */
	public DataObject(final Table table, final String playerId, final boolean bool) {
		this.table = table;
		this.playerId = playerId;
		this.condition = bool;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Creates a new DataObject instance.
    * 
    * @param table - The Table being accessed.
    * @param playerId - The Player UUID being referenced.
    * @param num - The Double being referenced.
    */
	public DataObject(final Table table, final String playerId, final double num) {
		this.table = table;
		this.playerId = playerId;
		this.numerical = num;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Creates a new DataObject instance.
    * 
    * @param table - The Table being accessed.
    * @param playerId - The Player UUID being referenced.
    * @param num - The Integer being referenced.
    */
	public DataObject(final Table table, final String playerId, final int num) {
		this.table = table;
		this.playerId = playerId;
		this.integer = num;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Gets the Equal Data of the DataObject.
    * 
    * @param object1 - The DataObject being compared.
    * @param object2 - The DataObject being compared.
    * @return If the data is equal.
    */	
	public boolean equalsData(final DataObject object1, final DataObject object2) {
		if (object1 == null || object2 == null) { return false; }
		if (object1.getTable().equals(Table.HOTBAR)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) && (object1.getPosition().equalsIgnoreCase(object2.getPosition()))) {
				return true;
			}
		} else if (object1.getTable().equals(Table.ALLOW_FLIGHT) || object1.getTable().equals(Table.SPEED_FLIGHT) || object1.getTable().equals(Table.SPEED_BREAK) || object1.getTable().equals(Table.SET_FOOD) || 
				object1.getTable().equals(Table.SET_HEALTH) || object1.getTable().equals(Table.SET_SCALE) || object1.getTable().equals(Table.ALLOW_HUNGER) || object1.getTable().equals(Table.ALLOW_BURN) || 
				object1.getTable().equals(Table.UNBREAKABLE_ITEMS) || object1.getTable().equals(Table.DROPS_BLOCK) || object1.getTable().equals(Table.SWORD_BLOCK) || object1.getTable().equals(Table.AUTO_RESTORE) || 
				object1.getTable().equals(Table.SET_GOD) || object1.getTable().equals(Table.DELAY_GOD) || object1.getTable().equals(Table.STORE_INVENTORY) || object1.getTable().equals(Table.PLAYERSTATS)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId())) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Gets the Removal Values of the DataObject.
    * 
    * @return The Removal Values.
    */	
	public String getRemovalValues() {
		String removal = "";
		for (String column : this.table.removal().split(", ")) {
			if (column.equalsIgnoreCase("Player_UUID")) { removal += "'" + this.getPlayerId() + "',"; }
			else if (column.equalsIgnoreCase("Position")) { removal += "'" + this.getPosition() + "',"; }
		}
		return removal.substring(0, removal.length() - 1);
	}
	
   /**
    * Gets the Insert Values of the DataObject.
    * 
    * @return The Insert Values.
    */	
	public String getInsertValues() {
		return 
				  (this.getPlayerId() != null && !this.getPlayerId().isEmpty() ? "'" + this.getPlayerId() + "'," : "")
			    + (this.getPosition() != null && !this.getPosition().isEmpty() ? "'" + this.getPosition() + "'," : "")
			    + (this.getBoolean() != null ? "'" + this.getBoolean() + "'," : "")
			    + (this.getDouble() != null && this.getDouble() >= 0 ? "'" + this.getDouble() + "'," : "")
			    + (this.getInt() != null && this.getInt() >= 0 ? "'" + this.getInt() + "'," : "")
			    + (this.getHealth() >= 0 ? "'" + this.getHealth() + "'," : "")
			    + (this.getMaxHealth() >= 0 ? "'" + this.getMaxHealth() + "'," : "")
			    + (this.getFood() >= 0 ? "'" + this.getFood() + "'," : "")
			    + (this.getFireTicks() != -101 ? "'" + this.getFireTicks() + "'," : "")
			    + (this.getInventory64() != null && !this.getInventory64().isEmpty() ? "'" + this.getInventory64() + "'," : "")
			    + "'" + new Timestamp(System.currentTimeMillis()) + "'";
	}
	
   /**
	* Defines the existing tables.
	* 
	*/ 
    public enum Table {
    	ALLOW_FLIGHT("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SPEED_FLIGHT("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SPEED_BREAK("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_FOOD("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_HEALTH("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_SCALE("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        ALLOW_HUNGER("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        ALLOW_BURN("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        UNBREAKABLE_ITEMS("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        DROPS_BLOCK("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SWORD_BLOCK("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        AUTO_RESTORE("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_GOD("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        DELAY_GOD("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        STORE_INVENTORY("`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        HOTBAR("`Player_UUID`, `Position`, `Inventory64`, `Time_Stamp`", "Player_UUID, Position"),
        PLAYERSTATS("`Player_UUID`, `Health`, `Scale`, `Food`, `Fire_Ticks`, `Inventory64`, `Time_Stamp`", "Player_UUID");
    	
    	private String header;
    	private String removal;
    	private Table(String header, String removal) {
    		this.header = header;
    		this.removal = removal;
    	}
    	public String headers() { return this.header; }
    	public String removal() { return this.removal; }
    }
}