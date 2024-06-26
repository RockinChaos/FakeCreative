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

@SuppressWarnings("unused")
public class DataObject {

    private final Table table;
    private Boolean isTemporary = false;
    private String playerId = "";
    private String position = "";
    private Boolean condition = null;
    private Double numerical = -1.0;
    private Integer integer = -1;
    private double health = -1.0;
    private double maxHealth = -1.0;
    private int food = -1;
    private int fireTicks = -101;
    private String inventory64 = "";
    private String timeStamp = "";

    /**
     * Creates a new DataObject instance.
     *
     * @param table - The Table being accessed.
     */
    public DataObject(Table table) {
        this.table = table;
        this.isTemporary = true;
    }

    /**
     * Creates a new DataObject instance.
     *
     * @param table    - The Table being accessed.
     * @param playerId - The Player UUID being referenced.
     * @param object1  - The Object being referenced.
     * @param object2  - The Object being referenced.
     * @param object3  - The Object being referenced.
     * @param object4  - The Object being referenced.
     * @param object5  - The Object being referenced.
     */
    public DataObject(final Table table, final String playerId, final String object1, final String object2, final String object3, final String object4, final String object5) {
        this.table = table;
        this.playerId = playerId;
        this.health = Double.parseDouble(object1);
        this.maxHealth = Double.parseDouble(object2);
        this.food = Integer.parseInt(object3);
        this.fireTicks = Integer.parseInt(object4);
        this.inventory64 = object5;
        this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
    }

    /**
     * Creates a new DataObject instance.
     *
     * @param table    - The Table being accessed.
     * @param playerId - The Player UUID being referenced.
     * @param object1  - The Object being referenced.
     * @param object2  - The Object being referenced.
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
     * @param table    - The Table being accessed.
     * @param playerId - The Player UUID being referenced.
     * @param bool     - The Boolean being referenced.
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
     * @param table    - The Table being accessed.
     * @param playerId - The Player UUID being referenced.
     * @param num      - The Double being referenced.
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
     * @param table    - The Table being accessed.
     * @param playerId - The Player UUID being referenced.
     * @param num      - The Integer being referenced.
     */
    public DataObject(final Table table, final String playerId, final int num) {
        this.table = table;
        this.playerId = playerId;
        this.integer = num;
        this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
    }

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
     * Gets the Table Name of the DataObject
     *
     * @return The Table Name.
     */
    public String getTableName() {
        return this.table.tableName();
    }

    /**
     * Gets the Table Removal of the DataObject
     *
     * @return The Table Removal.
     */
    public String getTableRemoval() {
        return this.table.removal();
    }

    /**
     * Gets the Table Headers of the DataObject
     *
     * @return The Table Headers.
     */
    public String getTableHeaders() {
        return this.table.headers();
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
     * Gets the Equal Data of the DataObject
     *
     * @param obj1 - The DataObject being compared.
     * @param obj2 - The DataObject being compared.
     * @return If the data is equal.
     */
    public boolean equalsData(final Object obj1, final Object obj2) {
        final DataObject object1 = (DataObject) obj1;
        final DataObject object2 = (DataObject) obj2;
        if (object1 == null || object2 == null) {
            return false;
        }
        if (object1.getTable().equals(Table.HOTBAR)) {
            return object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) && (object1.getPosition().equalsIgnoreCase(object2.getPosition()));
        } else if (object1.getTable().equals(Table.ALLOW_FLIGHT) || object1.getTable().equals(Table.SPEED_FLIGHT) || object1.getTable().equals(Table.SPEED_BREAK) || object1.getTable().equals(Table.SET_FOOD) ||
                object1.getTable().equals(Table.SET_HEALTH) || object1.getTable().equals(Table.SET_SCALE) || object1.getTable().equals(Table.ALLOW_HUNGER) || object1.getTable().equals(Table.ALLOW_BURN) ||
                object1.getTable().equals(Table.UNBREAKABLE_ITEMS) || object1.getTable().equals(Table.DROPS_BLOCK) || object1.getTable().equals(Table.SWORD_BLOCK) || object1.getTable().equals(Table.AUTO_RESTORE) ||
                object1.getTable().equals(Table.SET_GOD) || object1.getTable().equals(Table.DELAY_GOD) || object1.getTable().equals(Table.STORE_INVENTORY) || object1.getTable().equals(Table.DESTROY_PICKUPS) || object1.getTable().equals(Table.SELF_DROPS) || object1.getTable().equals(Table.ITEM_STORE) || object1.getTable().equals(Table.PLAYERSTATS)) {
            return object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId());
        }
        return false;
    }

    /**
     * Gets the Removal Values of the DataObject.
     *
     * @return The Removal Values.
     */
    public String getRemovalValues() {
        StringBuilder removal = new StringBuilder();
        for (String column : this.table.removal().split(", ")) {
            if (column.equalsIgnoreCase("Player_UUID")) {
                removal.append("'").append(this.getPlayerId()).append("',");
            } else if (column.equalsIgnoreCase("Position")) {
                removal.append("'").append(this.getPosition()).append("',");
            }
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
     */
    public enum Table {
        ALLOW_FLIGHT("allow_flight", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SPEED_FLIGHT("speed_flight", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SPEED_BREAK("speed_break", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_FOOD("set_food", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_HEALTH("set_health", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_SCALE("set_scale", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        ALLOW_HUNGER("allow_hunger", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        ALLOW_BURN("allow_burn", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        UNBREAKABLE_ITEMS("unbreakable_items", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        DROPS_BLOCK("drops_block", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SWORD_BLOCK("sword_block", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        AUTO_RESTORE("auto_restore", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SET_GOD("set_god", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        DELAY_GOD("delay_god", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        STORE_INVENTORY("store_inventory", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        DESTROY_PICKUPS("destroy_pickups", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        SELF_DROPS("self_drops", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        ITEM_STORE("item_store", "`Player_UUID`, `Value`, `Time_Stamp`", "Player_UUID"),
        HOTBAR("hotbar", "`Player_UUID`, `Position`, `Inventory64`, `Time_Stamp`", "Player_UUID, Position"),
        PLAYERSTATS("playerstats", "`Player_UUID`, `Health`, `Scale`, `Food`, `Fire_Ticks`, `Inventory64`, `Time_Stamp`", "Player_UUID");

        private final String tableName;
        private final String header;
        private final String removal;

        Table(final String tableName, final String header, final String removal) {
            this.tableName = tableName;
            this.header = header;
            this.removal = removal;
        }

        public String tableName() {
            return this.tableName;
        }

        public String headers() {
            return this.header;
        }

        public String removal() {
            return this.removal;
        }
    }
}