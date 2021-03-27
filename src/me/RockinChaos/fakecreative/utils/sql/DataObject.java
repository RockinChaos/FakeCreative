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
	private String worldName = new String();
	private String position = new String();
	private String inventory64 = new String();
	private String timeStamp = new String();
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getPlayerId() {
		return this.playerId;
	}
	
   /**
    * Gets the World Name of the DataObject
    * 
    * @return The World Name.
    */
	public String getWorld() {
		return this.worldName;
	}
	
   /**
    * Gets the Position of the DataObject
    * 
    * @return The Position.
    */
	public String getPosition() {
		return this.position;
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
    * Gets the Time Stamp of the DataObject
    * 
    * @return The Time Stamp.
    */
	public String getTimeStamp() {
		return this.timeStamp;
	}
	
   /**
    * Sets the Time Stamp of the DataObject
    * 
    * @param stamp - The Time Stamp.
    */
	public void setTimeStamp(String stamp) {
		this.timeStamp = stamp;
	}

   /**
    * Gets the Table of the DataObject
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
    * Creates a new DataObject instance
    * 
    * @param table - The Table being accessed.
    * @param isTemporary - If the DataObject is only a reference.
    */
	public DataObject(Table table) {
		this.table = table;
		this.isTemporary = true;
	}
	
   /**
    * Creates a new DataObject instance
    * 
    * @param table - The Table being accessed.
    * @param playerId - The Player UUID being referenced.
    * @param worldName - The World Name being referenced.
    * @param object1 - The Object being referenced.
    * @param object2 - The Object being referenced.
    */
	public DataObject(Table table, String playerId, String worldName, String object1, String object2) {
		this.playerId = playerId;
		this.position = object1;
		this.inventory64 = object2;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Gets the Equal Data of the DataObject
    * 
    * @param object1 - The DataObject being compared.
    * @param object2 - The DataObject being compared.
    * @return If the data is equal.
    */	
	public boolean equalsData(DataObject object1, DataObject object2) {
		if (object1 == null || object2 == null) { return false; }
		if (object1.getTable().equals(Table.HOTBAR)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) && (object1.getPosition().equalsIgnoreCase(object2.getPosition()))) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Gets the Removal Values of the DataObject
    * 
    * @return The Removal Values.
    */	
	public String getRemovalValues() {
		String removal = "";
		for (String column : this.table.removal().split(", ")) {
			if (column.equalsIgnoreCase("Player_UUID")) { removal += "'" + this.getPlayerId() + "',"; }
			else if (column.equalsIgnoreCase("World_Name")) { removal += "'" + this.getWorld() + "',"; }
			else if (column.equalsIgnoreCase("Position")) { removal += "'" + this.getPosition() + "',"; }
		}
		return removal.substring(0, removal.length() - 1);
	}
	
   /**
    * Gets the Insert Values of the DataObject
    * 
    * @return The Insert Values.
    */	
	public String getInsertValues() {
		return 
				  (this.getWorld() != null && !this.getWorld().isEmpty() ? "'" + this.getWorld() + "'," : "")
				+ (this.getPlayerId() != null && !this.getPlayerId().isEmpty() ? "'" + this.getPlayerId() + "'," : "")
			    + (this.getPosition() != null && !this.getPosition().isEmpty() ? "'" + this.getPosition() + "'," : "")
			    + (this.getInventory64() != null && !this.getInventory64().isEmpty() ? "'" + this.getInventory64() + "'," : "")
			    + "'" + new Timestamp(System.currentTimeMillis()) + "'";
	}
	
   /**
	* Defines the existing tables.
	* 
	*/ 
    public enum Table {
       HOTBAR("`Player_UUID`, `Position`, `Inventory64`, `Time_Stamp`", "Player_UUID, Position");
    	
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