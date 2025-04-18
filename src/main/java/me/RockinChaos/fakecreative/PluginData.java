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
package me.RockinChaos.fakecreative;

import com.google.common.collect.ImmutableMap;
import me.RockinChaos.core.listeners.Interfaces;
import me.RockinChaos.core.listeners.PlayerLogin;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.MetricsAPI;
import me.RockinChaos.core.utils.api.ProtocolAPI;
import me.RockinChaos.core.utils.protocol.ProtocolManager;
import me.RockinChaos.core.utils.sql.Database;
import me.RockinChaos.fakecreative.listeners.*;
import me.RockinChaos.fakecreative.utils.api.LegacyAPI;
import me.RockinChaos.fakecreative.utils.sql.DataObject;
import me.RockinChaos.fakecreative.utils.sql.DataObject.Table;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.util.*;

public class PluginData {

    public static PluginData data;

    /**
     * Gets the instance of the ItemData.
     *
     * @return The ItemData instance.
     */
    public static PluginData getData() {
        if (data == null) {
            data = new PluginData();
        }
        return data;
    }

    /**
     * Registers the command executors and events.
     */
    public void registerEvents() {
        Objects.requireNonNull(FakeCreative.getCore().getPlugin().getCommand("gamemode")).setExecutor(new ChatExecutor());
        Objects.requireNonNull(FakeCreative.getCore().getPlugin().getCommand("gamemode")).setTabCompleter(new ChatTab());
        Objects.requireNonNull(FakeCreative.getCore().getPlugin().getCommand("fakecreative")).setExecutor(new ChatExecutor());
        Objects.requireNonNull(FakeCreative.getCore().getPlugin().getCommand("fakecreative")).setTabCompleter(new ChatTab());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Blocks(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Clicking(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Crafting(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Drops(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Storable(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Gamemode(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interact(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Targeting(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerJoin(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerQuit(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerClear(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerRespawn(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerLogin(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interfaces(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interface(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Movement(), FakeCreative.getCore().getPlugin());
        FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Pickups(), FakeCreative.getCore().getPlugin());
        LegacyAPI.registerDepletion();
        LegacyAPI.registerInvulnerable();
    }

    /**
     * Registers new instances of the plugin classes.
     *
     * @param silent - If any messages should be sent.
     */
    public void registerClasses(final boolean silent) {
        final boolean isRunning = FakeCreative.getCore().isStarted();
        ServerUtils.clearErrorStatements();
        FakeCreative.getCore().getData().refresh();
        FakeCreative.getCore().getData().setStarted(false);
        FakeCreative.getCore().getData().setPluginPrefix("&7[&6FakeCreative&7]");
        FakeCreative.getCore().getData().setConfig(ImmutableMap.of("config.yml", 0, "lang.yml", 1));
        FakeCreative.getCore().getData().setLanguages(Arrays.asList("English", "German", "Spanish"));
        FakeCreative.getCore().getData().setPermissions(Arrays.asList("FakeCreative.use", "FakeCreative.dump", "FakeCreative.reload", "FakeCreative.updates", "FakeCreative.upgrade", "FakeCreative.permissions", "FakeCreative.purge", "FakeCreative.preferences",
                "FakeCreative.mode.creative", "FakeCreative.mode.survival", "FakeCreative.mode.adventure", "FakeCreative.mode.spectator"));
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        // -=-=-=-=-=-=   Copy The Configuration Files to Disk and Load them into Memory.   =-=-=-=-=-=- //
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        FakeCreative.getCore().getConfiguration().reloadFiles(); // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        FakeCreative.getCore().getData().setCheckforUpdates(FakeCreative.getCore().getConfig("config.yml").getBoolean("General.CheckforUpdates"));
        FakeCreative.getCore().getData().setDebug(FakeCreative.getCore().getConfig("config.yml").getBoolean("General.Debugging"));
        FakeCreative.getCore().getData().setIgnoreErrors(FakeCreative.getCore().getConfig("config.yml").getBoolean("General.ignoreErrors"));
        FakeCreative.getCore().getData().setSQL(FakeCreative.getCore().getConfig("config.yml").getString("Database.MySQL") != null && FakeCreative.getCore().getConfig("config.yml").getBoolean("Database.MySQL"));
        FakeCreative.getCore().getData().setTablePrefix(FakeCreative.getCore().getConfig("config.yml").getString("Database.prefix") != null ? FakeCreative.getCore().getConfig("config.yml").getString("Database.prefix") : "fc_");
        FakeCreative.getCore().getData().setSQLHost(FakeCreative.getCore().getConfig("config.yml").getString("Database.host"));
        FakeCreative.getCore().getData().setSQLPort(FakeCreative.getCore().getConfig("config.yml").getString("Database.port"));
        FakeCreative.getCore().getData().setSQLUser(FakeCreative.getCore().getConfig("config.yml").getString("Database.user"));
        FakeCreative.getCore().getData().setSQLPass(FakeCreative.getCore().getConfig("config.yml").getString("Database.pass"));
        FakeCreative.getCore().getData().setSQLDatabase(FakeCreative.getCore().getConfig("config.yml").getString("Database.database"));
        FakeCreative.getCore().getData().setAlterTables(this.getAlterTables());
        FakeCreative.getCore().getData().setCreateTables(this.getCreateTables());
        FakeCreative.getCore().getDependencies().refresh();
        if (!FakeCreative.getCore().getDependencies().protocolEnabled() && ProtocolManager.isDead()) {
            ProtocolManager.handleProtocols();
        } else if (FakeCreative.getCore().getDependencies().protocolEnabled() && !ProtocolAPI.isHandling()) {
            ProtocolAPI.handleProtocols();
        }
        SchedulerUtils.runAsync(() -> {
            final String compileVersion = Objects.requireNonNull(YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(FakeCreative.getCore().getPlugin().getResource("plugin.yml")))).getString("nms-version")).split("-")[0].replace(".", "_");
            final String serverVersion = ServerUtils.getVersion();
            if (!silent) {
                if (StringUtils.containsIgnoreCase(compileVersion, "spigot_version")) {
                    ServerUtils.logInfo("Running a developer version ... skipping NMS check.");
                } else if (!compileVersion.equalsIgnoreCase(serverVersion) && ServerUtils.hasPreciseUpdate(compileVersion)) {
                    ServerUtils.logSevere("Detected a unsupported version of Minecraft!");
                    ServerUtils.logSevere("Attempting to run in NMS compatibility mode...");
                    ServerUtils.logSevere("Things may not work as expected, please check for plugin updates.");
                }
                FakeCreative.getCore().getDependencies().sendUtilityDepends();
            }
            if (isRunning && FakeCreative.getCore().getSQL().refresh()) {
                FakeCreative.getCore().getData().setDatabaseData(this.getDatabaseData());
                FakeCreative.getCore().getSQL().load();
            }
        });
        if (!isRunning) {
            FakeCreative.getCore().getSQL();
            FakeCreative.getCore().getData().setDatabaseData(this.getDatabaseData());
            FakeCreative.getCore().getSQL().load();
        }
        SchedulerUtils.runSingleAsync(() -> FakeCreative.getCore().getData().setStarted(true));
        SchedulerUtils.runAsyncLater(100L, () -> {
            if (FakeCreative.getCore().getConfig("config.yml").getBoolean("General.Metrics-Logging")) {
                final MetricsAPI metrics = new MetricsAPI(FakeCreative.getCore().getPlugin(), 10818);
                FakeCreative.getCore().getDependencies().addCustomCharts(metrics);
                ServerUtils.sendErrorStatements(null);
            }
        });
    }

    /**
     * Gets all active database data for the plugin directly from the SQL database connection.
     *
     * @return The Map of database data.
     */
    public Map<String, List<Object>> getDatabaseData() {
        final Map<String, List<Object>> databaseData = new HashMap<>();
        for (final Table tableEnum : Table.values()) {
            final String table = tableEnum.tableName();
            final List<HashMap<String, String>> selectTable = Database.getDatabase().queryTableData("SELECT * FROM " + FakeCreative.getCore().getData().getTablePrefix() + table, tableEnum.headers().replace("`", ""));
            if (!selectTable.isEmpty()) {
                for (final HashMap<String, String> sl1 : selectTable) {
                    DataObject dataObject = null;
                    if (tableEnum.equals(Table.ALLOW_FLIGHT) || tableEnum.equals(Table.ALLOW_HUNGER) || tableEnum.equals(Table.ALLOW_BURN) || tableEnum.equals(Table.UNBREAKABLE_ITEMS) || tableEnum.equals(Table.DROPS_BLOCK)
                            || tableEnum.equals(Table.SWORD_BLOCK) || tableEnum.equals(Table.AUTO_RESTORE) || tableEnum.equals(Table.SET_GOD) || tableEnum.equals(Table.STORE_INVENTORY) || tableEnum.equals(Table.DESTROY_PICKUPS) || tableEnum.equals(Table.SELF_DROPS) || tableEnum.equals(Table.ITEM_STORE)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), Boolean.parseBoolean(sl1.get("Value")));
                    } else if (tableEnum.equals(Table.SPEED_FLIGHT) || tableEnum.equals(Table.SPEED_BREAK) || tableEnum.equals(Table.SET_SCALE)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), Double.parseDouble(sl1.get("Value")));
                    } else if (tableEnum.equals(Table.SET_FOOD) || tableEnum.equals(Table.SET_HEALTH) || tableEnum.equals(Table.DELAY_GOD)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), Integer.parseInt(sl1.get("Value")));
                    } else if (tableEnum.equals(Table.HOTBAR)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("Position"), sl1.get("Inventory64"));
                    } else if (tableEnum.equals(Table.PLAYERSTATS)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("Health"), sl1.get("Scale"), sl1.get("Food"), sl1.get("Fire_Ticks"), sl1.get("Inventory64"));
                    }
                    dataObject.setTimeStamp(sl1.get("Time_Stamp"));
                    final List<Object> dataSet = (databaseData.get(table) != null ? databaseData.get(table) : new ArrayList<>());
                    dataSet.add(dataObject);
                    databaseData.put(table, dataSet);
                }
            }
        }
        return databaseData;
    }

    /**
     * Softly reloads the configuration files.
     * Usefully when editing booleans.
     */
    @SuppressWarnings("unused")
    public void softReload() {
        FakeCreative.getCore().getConfiguration().reloadFiles();
    }

    /**
     * Creates the data set for the SQL database.
     *
     * @return The Runnable to create the data set.
     */
    public Runnable getCreateTables() {
        final String prefix = FakeCreative.getCore().getData().getTablePrefix();
        return () -> {
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "allow_flight (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "speed_flight (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "speed_break (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "set_food (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "set_health (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "set_scale (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "allow_hunger (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "allow_burn (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "unbreakable_items (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "drops_block (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "sword_block (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "auto_restore (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "set_god (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "delay_god (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "store_inventory (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "destroy_pickups (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "self_drops (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "item_store (`Player_UUID` varchar(64), `Value` varchar(16), `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "hotbar (`Player_UUID` varchar(64), `Position` varchar(12), `Inventory64` text, `Time_Stamp` varchar(64));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + prefix + "playerstats (`Player_UUID` varchar(64), `Health` varchar(64), `Scale` varchar(64), `Food` varchar(64), `Fire_Ticks` varchar(128), `Inventory64` text, `Time_Stamp` varchar(64));");
        };
    }

    /**
     * Alters the existing data set to fit the new SQL standards.
     *
     * @return The Runnable to alter the existing data set.
     */
    public Runnable getAlterTables() {
        return () -> {
        };
    }

    /**
     * Harshly reloads the configuration files and re-registers events.
     *
     * @param silent - If any messages should be sent.
     */
    public void hardReload(final boolean silent) {
        FakeCreative.getCore().getConfiguration().reloadFiles();
        {
            SchedulerUtils.run(() -> PluginData.getData().registerClasses(silent));
        }
    }
}