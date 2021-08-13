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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.handlers.ConfigHandler;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;

public class SQL {
	
	private Map < String, List<DataObject> > databaseData = new HashMap < String, List<DataObject> >();
	
	private static SQL data;
	
   /**
    * Creates a new SQLData instance.
    * 
    */
	public SQL() {
		Database.kill(); {
			this.createTables(); {
				this.loadData();
			}
			ServerUtils.logDebug("{SQL} Database Connected."); 
		}
	}
	
   /**
    * Removes FakeCreative tables from the database.
    * 
    */
	public void purgeDatabase() {
		this.databaseData.clear();
		SchedulerUtils.runSingleAsync(() -> {
			for (Table table: Table.values()) {
				synchronized("FK_SQL") {
					Database.getDatabase().executeStatement("DROP TABLE IF EXISTS " + ConfigHandler.getConfig().getTable() + table.name().toLowerCase());
				}
			} { this.createTables(); }
		});
	}
	
   /**
    * Saves the table data for the specified DataObject.
    * 
    * @param object - The DataObject data being saved.
    */
	public void saveData(final DataObject object) {
		if (object != null) { 
			final String table = object.getTable().name().toLowerCase();
			if (FakeCreative.getInstance().isEnabled()) {
				SchedulerUtils.runSingleAsync(() -> {
					synchronized("FK_SQL") {
						Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + object.getTable().name().toLowerCase() + " (" + object.getTable().headers() + ") VALUES (" + object.getInsertValues() + ")");
					}
				});
			} else {
				synchronized("FK_SQL") {
					Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + object.getTable().name().toLowerCase() + " (" + object.getTable().headers() + ") VALUES (" + object.getInsertValues() + ")");
				}
			}
			if (this.databaseData.get(table) != null) {
				final List <DataObject> h1 = this.databaseData.get(table);
				h1.add(object);
				this.databaseData.put(table, h1);
			} else {
				final List <DataObject> h1 = new ArrayList<DataObject>();
				h1.add(object);
				this.databaseData.put(table, h1);
			}
		}
	}
	
   /**
    * Removes the table data for the specified DataObject.
    * 
    * @param object - The DataObject being accessed.
    */
	public void removeData(final DataObject object) {
		if (object != null) { 
			final String table = object.getTable().name().toLowerCase();
			if (this.databaseData.get(table) != null && !this.databaseData.get(table).isEmpty()) {
				final Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
				while (dataSet.hasNext()) {
					final DataObject dataObject = dataSet.next();
					if (dataObject != null && dataObject.getTable().equals(object.getTable()) && object.equalsData(object, dataObject)) {
						if (FakeCreative.getInstance().isEnabled()) {
							SchedulerUtils.runSingleAsync(() -> {
								synchronized("FK_SQL") {
									Database.getDatabase().executeStatement("DELETE FROM " + ConfigHandler.getConfig().getTable() + dataObject.getTable().name().toLowerCase() + " WHERE (" + dataObject.getTable().removal() + ") = (" + dataObject.getRemovalValues() + ")");
								}
							});
						} else {
							synchronized("FK_SQL") {
								Database.getDatabase().executeStatement("DELETE FROM " + ConfigHandler.getConfig().getTable() + dataObject.getTable().name().toLowerCase() + " WHERE (" + dataObject.getTable().removal() + ") = (" + dataObject.getRemovalValues() + ")");
							}
						}
						dataSet.remove();
					}
				}
			}
		}
	}
	
   /**
    * Gets the table data for the specified DataObject.
    * 
    * @param object - The DataObject being accessed.
    * @return The found table data.
    */
	public DataObject getData(final DataObject object) {
		if (object != null) { 
			final String table = object.getTable().name().toLowerCase();
			if (this.databaseData.get(table) != null && !this.databaseData.get(table).isEmpty()) {
				final Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
				while (dataSet.hasNext()) {
					final DataObject dataObject = dataSet.next();
					if (dataObject != null && dataObject.getTable().equals(object.getTable()) && object.equalsData(object, dataObject)) {
						return dataObject;
					}
				}
			}
		}
		return null;
	}
	
   /**
    * Gets the table data list for the specified DataObject.
    * 
    * @param object - The DataObject being accessed.
    * @return The found table data list.
    */
	public List<DataObject> getDataList(final DataObject object) {
		final List<DataObject> dataList = new ArrayList<DataObject>();
		final String table = object.getTable().name().toLowerCase();
		if (this.databaseData.get(table) != null && !this.databaseData.get(table).isEmpty()) {
			final Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
			while (dataSet.hasNext()) {
				final DataObject dataObject = dataSet.next();
				if (dataObject != null && dataObject.getTable().equals(object.getTable()) && (object.isTemporary() || object.equalsData(object, dataObject))) {
					dataList.add(dataObject);
				}
			}
		}
		return dataList;
	}
	
   /**
    * Loads all the database data into memory.
    * 
    */
	private void loadData() {
		for (Table tableEnum: Table.values()) {
			final String table = tableEnum.name().toLowerCase();
			final List<HashMap<String, String>> selectTable = Database.getDatabase().queryTableData("SELECT * FROM " + ConfigHandler.getConfig().getTable() + table, tableEnum.headers().replace("`", ""));
			if (selectTable != null && !selectTable.isEmpty()) {
				for (HashMap<String, String> sl1 : selectTable) {
					DataObject dataObject = null;
					if (tableEnum.equals(Table.ALLOW_FLIGHT) || tableEnum.equals(Table.ALLOW_HUNGER) || tableEnum.equals(Table.ALLOW_BURN) || tableEnum.equals(Table.UNBREAKABLE_ITEMS) || tableEnum.equals(Table.DROPS_BLOCK)
					 || tableEnum.equals(Table.SWORD_BLOCK) || tableEnum.equals(Table.AUTO_RESTORE) || tableEnum.equals(Table.SET_GOD) || tableEnum.equals(Table.STORE_INVENTORY) || tableEnum.equals(Table.DESTROY_PICKUPS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), Boolean.valueOf(sl1.get("Value")));
					} else if (tableEnum.equals(Table.SPEED_FLIGHT) || tableEnum.equals(Table.SPEED_BREAK) || tableEnum.equals(Table.SET_SCALE)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), Double.valueOf(sl1.get("Value")));
					} else if (tableEnum.equals(Table.SET_FOOD) || tableEnum.equals(Table.SET_HEALTH) || tableEnum.equals(Table.DELAY_GOD)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), Integer.valueOf(sl1.get("Value")));
					} else if (tableEnum.equals(Table.HOTBAR)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("Position"), sl1.get("Inventory64"));
					} else if (tableEnum.equals(Table.PLAYERSTATS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("Health"), sl1.get("Scale"), sl1.get("Food"), sl1.get("Fire_Ticks"), sl1.get("Inventory64"));
					}
					dataObject.setTimeStamp(sl1.get("Time_Stamp"));
					final List <DataObject> dataSet = (this.databaseData.get(table) != null ? this.databaseData.get(table) : new ArrayList<DataObject>());
					dataSet.add(dataObject);
					this.databaseData.put(table, dataSet);
				}
			}
		}
	}
	
   /**
    * Gets the Equal Data of the DataObject
    * 
    * @param object - The DataObject being accessed.
    * @return If the data is equal.
    */	
	public boolean hasDataSet(final DataObject object) {
		final String table = object.getTable().name().toLowerCase();
		final Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
		while (dataSet.hasNext()) {
			final DataObject dataObject = dataSet.next();
			if (dataObject.getTable().equals(object.getTable()) && object.equalsData(object, dataObject)) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Creates the missing database tables.
    * 
    */
	private void createTables() {
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "allow_flight (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "speed_flight (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "speed_break (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "set_food (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "set_health (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "set_scale (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "allow_hunger (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "allow_burn (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "unbreakable_items (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "drops_block (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "sword_block (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "auto_restore (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "set_god (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "delay_god (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "store_inventory (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "destroy_pickups (`Player_UUID` varchar(1000), `Value` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "hotbar (`Player_UUID` varchar(1000), `Position` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
		Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "playerstats (`Player_UUID` varchar(1000), `Health` varchar(1000), `Scale` varchar(1000), `Food` varchar(1000), `Fire_Ticks` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
	}
	
	public static void newData(final boolean reload) {
		if (!reload) {
			data = new SQL();
		} else if ((!ConfigHandler.getConfig().sqlEnabled() && Database.getDatabase().getConstant()) || (ConfigHandler.getConfig().sqlEnabled() && !Database.getDatabase().getConstant())) {
			data = new SQL();
		}
	}
	
   /**
    * Gets the instance of the SQLite.
    * 
    * @param regen - If the SQLite should have a new instance created.
    * @return The SQLite instance.
    */
    public static SQL getData() { 
        if (data == null) { newData(false); }
        return data; 
    } 
}