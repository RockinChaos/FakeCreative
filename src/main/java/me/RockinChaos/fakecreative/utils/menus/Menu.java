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
package me.RockinChaos.fakecreative.utils.menus;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.interfaces.Interface;
import me.RockinChaos.core.utils.interfaces.Query;
import me.RockinChaos.core.utils.interfaces.types.Button;
import me.RockinChaos.core.utils.types.*;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.*;

/**
 * Handles the in-game GUI.
 */
public class Menu {
    private static final ItemStack fillerPaneBItem = ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:15"), 1, false, false, "&7", "");
    private static final ItemStack fillerPaneGItem = ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:7"), 1, false, false, "&7", "");
    private static final ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nExit", "&7", "&7*Return to playing the game.");
    private static final List<String> modifyMenu = new ArrayList<>();
    private static final Map<String, Interface> typingMenu = new HashMap<>();
    private static String GUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7           &0&nCreative Menu") : StringUtils.colorFormat("&7           &0&n Creative Menu");
    private static String HotbarGUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7            &0&nHotbar Menu") : StringUtils.colorFormat("&7            &0&n Hotbar Menu");
    private static String userGUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7              &0&nUser Menu") : StringUtils.colorFormat("&7             &0&n User Menu");

    /**
     * Opens the MAIN CREATIVE PANE for the Player.
     *
     * @param sender   - The Sender to have the Pane opened.
     * @param selected - The selected creative section.
     * @param search   - The type of material to search for (if any).
     */
    public static void creativeMenu(final CommandSender sender, final int selected, final String search) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            boolean failure = true;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && ((search != null && !search.isEmpty() && StringUtils.containsIgnoreCase(material.name(), search))
                        || (search == null || search.isEmpty())) && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 0)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        failure = false;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, selected);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, selected);
            } else if (failure) {
                addSections(pagedPane, 27, selected);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN MISCELLANEOUS PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void miscellaneousMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && Miscellaneous.isMiscellaneous(material)
                        && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 0)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 1);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 1);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN FOOD PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void foodMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && material.isEdible() && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 0)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 2);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 2);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN TOOLS PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void toolsMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && Tools.isTools(material) && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 2)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 3);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 3);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN COMBAT PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void combatMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && Combat.isCombat(material) && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 1)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 4);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 4);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }    private static final Button exitButton = new Button(exitItem, event -> Menu.creativeMenu(event.getWhoClicked(), 0, null));

//  ============================================== //
//  			   Selection Menus      	       //
//	============================================== //

    /**
     * Opens the MAIN BREWING PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void brewingMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && Brewing.isBrewing(material) && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 0)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 5);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 5);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN BUILDING PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void buildingMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && BuildingBlocks.isBuildingBlocks(material) && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 0)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 6);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 6);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN DECORATION PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void decorationMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && Decoration.isDecoration(material) && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 0)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 7);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 7);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN REDSTONE PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void redstoneMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(true, 6, exitButton, GUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            int currentCount = 0;
            Inventory inventoryCheck = FakeCreative.getCore().getPlugin().getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equalsIgnoreCase("AIR") && Redstone.isRedstone(material) && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    for (ItemStack item : spliceMaterial(material, 0)) {
                        pagedPane.addButton(new Button(item, Menu::handleEvent));
                        currentCount++;
                        if (currentCount == 27) {
                            currentCount = 0;
                            addSections(pagedPane, currentCount, 8);
                        }
                    }
                }
            }
            if (currentCount != 0) {
                currentCount = (27 - currentCount);
                addSections(pagedPane, currentCount, 8);
            }
            inventoryCheck.clear();
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN HOTBAR PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void hotbarMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(false, 2, exitButton, HotbarGUIName, player);
        pagedPane.setReturnButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            for (int i = 1; i <= 9; i++) {
                final int k = i;
                StringBuilder existingMats = new StringBuilder();
                String hotbarData = Creative.get(player).getStats().getHotbars().get(k);
                if (hotbarData != null) {
                    Inventory inventory = ItemHandler.deserializeInventory(hotbarData);
                    if (inventory != null) {
                        for (int j = 0; j < 9; j++) {
                            final ItemStack item = inventory.getItem(j);
                            if (item != null && item.getType() != Material.AIR) {
                                existingMats.append("&7").append(item.getType().name()).append("x").append(item.getAmount()).append(" /n ");
                            }
                        }
                    }
                }
                final boolean matExisting = existingMats.length() > 0;
                pagedPane.addButton(new Button(ItemHandler.getItem("PAPER", k, false, false, "&a&l[" + k + "] " + "&aHotbar",
                        (existingMats.length() > 0) ? existingMats.substring(0, existingMats.length() - 4) : "&7No items saved for this hotbar.", "&7",
                        "&8&oLeft-click to save.", (existingMats.length() > 0) ? "&8&oShift+Left-click to load." : "", (existingMats.length() > 0) ? "&8&oRight-click to view." : ""), event -> {
                    boolean empty = true;
                    if (event.getClick() == ClickType.LEFT) {
                        if (Creative.isCreativeMode(player, true)) {
                            Inventory inventoryData = Bukkit.getServer().createInventory(null, 9);
                            for (int j = 0; j < 9; j++) {
                                final ItemStack item = player.getOpenInventory().getBottomInventory().getItem(j);
                                if (item != null && item.getType() != Material.AIR) {
                                    inventoryData.setItem(j, item.clone());
                                    empty = false;
                                }
                            }
                            if (!empty) {
                                String[] placeHolders = FakeCreative.getCore().getLang().newString();
                                placeHolders[20] = Integer.toString(k);
                                FakeCreative.getCore().getLang().sendLangMessage("commands.menu.savedHotbar", sender, placeHolders);
                                Creative.get(player).getStats().saveHotbar(player, ItemHandler.serializeInventory(inventoryData), k);
                                SchedulerUtils.run(() -> hotbarMenu(sender));
                            }
                        }
                    } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (Creative.isCreativeMode(player, true)) {
                            String inventoryData = Creative.get(player).getStats().getHotbars().get(k);
                            if (inventoryData != null) {
                                Inventory inventory = ItemHandler.deserializeInventory(inventoryData);
                                if (inventory != null) {
                                    for (int j = 0; j < 9; j++) {
                                        final ItemStack item = inventory.getItem(j);
                                        if (item != null && item.getType() != Material.AIR) {
                                            player.getOpenInventory().getBottomInventory().setItem(j, item.clone());
                                            empty = false;
                                        }
                                    }
                                }
                            }
                            if (!empty) {
                                String[] placeHolders = FakeCreative.getCore().getLang().newString();
                                placeHolders[20] = Integer.toString(k);
                                FakeCreative.getCore().getLang().sendLangMessage("commands.menu.loadedHotbar", sender, placeHolders);
                                SchedulerUtils.run(() -> hotbarMenu(sender));
                            }
                        }
                    } else if ((event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) && matExisting) {
                        viewHotbar(player, k);
                    }
                }));
            }
            pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
            pagedPane.addButton(new Button(fillerPaneBItem), 3);
            pagedPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, true, true, "&e&l&nInformation", "&7", "&7Left-click to save", "&7your current hotbar.", "&7", "&7Shift+Left-click to load", "&7the clicked hotbar.", "&7", "&7Right-click to view", "&7the clicked hotbar.")));
            pagedPane.addButton(new Button(fillerPaneBItem), 3);
            pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
        });
        pagedPane.open(player);
    }

    /**
     * Opens the MAIN VIEWING PANE for the Player.
     *
     * @param player - The Player to have the Pane opened.
     * @param hotbar - The hotbar slot.
     */
    public static void viewHotbar(final Player player, final int hotbar) {
        Interface pagedPane = new Interface(false, 2, exitButton, HotbarGUIName, player);
        pagedPane.allowClick(true);
        SchedulerUtils.runAsync(() -> {
            if (Creative.isCreativeMode(player, true)) {
                String inventoryData = Creative.get(player).getStats().getHotbars().get(hotbar);
                if (inventoryData != null) {
                    Inventory inventory = ItemHandler.deserializeInventory(inventoryData);
                    if (inventory != null) {
                        for (int j = 0; j < 9; j++) {
                            final ItemStack item = inventory.getItem(j);
                            if (item != null && item.getType() != Material.AIR) {
                                pagedPane.addButton(new Button(item.clone(), Menu::handleEvent));
                            } else {
                                pagedPane.addButton(new Button(fillerPaneGItem));
                            }
                        }
                    }
                }
            }
            pagedPane.addButton(new Button(exitItem, event -> hotbarMenu(player)));
            pagedPane.addButton(new Button(fillerPaneBItem), 7);
            pagedPane.addButton(new Button(exitItem, event -> hotbarMenu(player)));
        });
        pagedPane.open(player);
    }

    /**
     * Opens the USER PREFERENCES PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void userMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(false, 3, exitButton, userGUIName, player);
        SchedulerUtils.runAsync(() -> {
            pagedPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, Creative.get(player).getStats().allowFlight(), true, "&fAllow Flight", "&7*If you should be allowed to fly", "&7using double-space to ascend.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().allowFlight()),
                    event -> {
                        Creative.get(player).getStats().setFlight(player, !Creative.get(player).getStats().allowFlight());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, true, "&fFlight Speed", "&7*The desired speed while flying.",
                    "&7", "&a&lSpeed: &b&l" + Creative.get(player).getStats().flySpeed()), event -> {
                final InventoryHolder inventoryHolder = event.getInventory().getHolder();
                if (inventoryHolder != null) {
                    ((Interface) inventoryHolder).onTyping((Player) event.getView().getPlayer());
                    Menu.setTypingMenu(true, (Player) event.getWhoClicked(), ((Interface) inventoryHolder));
                }
            }
                    , query -> query.onClose(stateSnapshot -> userMenu(player))
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != Query.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        final String input = ChatColor.stripColor(stateSnapshot.getText().replace(" ", ""));
                        if (StringUtils.isInt(input) && Integer.parseInt(input) <= 10) {
                            Creative.get(player).getStats().setFlySpeed(player, Integer.parseInt(input));
                            Menu.setTypingMenu(false, stateSnapshot.getPlayer(), null);
                            return Collections.singletonList(Query.ResponseAction.close());
                        } else if (StringUtils.isInt(input)) {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInteger too Large"));
                        } else {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInvalid Integer"));
                        }
                    })
                    .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&7"))
                    .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputType").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputExample").replace("%input_example%", "BOAT").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), "&7", "&aMaximum Speed is 10."))
                    .itemOutput(ItemHandler.getItem("FEATHER", 1, false, true, "&bStart typing...", "&8Preview of what", "&8you have entered.'"))
                    .title("Flight Speed:"), 0));
            pagedPane.addButton(new Button(ItemHandler.getItem("DIAMOND_PICKAXE", 1, false, true, "&fBreak Speed", "&7*The desired speed", "&7in-between each block-break.",
                    "&7", "&a&lSpeed: &b&l" + Creative.get(player).getStats().breakSpeed()), event -> {
                final InventoryHolder inventoryHolder = event.getInventory().getHolder();
                if (inventoryHolder != null) {
                    ((Interface) inventoryHolder).onTyping((Player) event.getView().getPlayer());
                    Menu.setTypingMenu(true, (Player) event.getWhoClicked(), ((Interface) inventoryHolder));
                }
            }
                    , query -> query.onClose(stateSnapshot -> userMenu(player))
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != Query.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        final String input = ChatColor.stripColor(stateSnapshot.getText().replace(" ", ""));
                        if (StringUtils.isInt(input)) {
                            Creative.get(player).getStats().setBreakSpeed(player, Integer.parseInt(input));
                            Menu.setTypingMenu(false, stateSnapshot.getPlayer(), null);
                            return Collections.singletonList(Query.ResponseAction.close());
                        } else {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInvalid Integer"));
                        }
                    })
                    .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&7"))
                    .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputType").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputExample").replace("%input_example%", "BOAT").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number")))
                    .itemOutput(ItemHandler.getItem("FEATHER", 1, false, true, "&bStart typing...", "&8Preview of what", "&8you have entered.'"))
                    .title("Break Speed:"), 0));
            pagedPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, Creative.get(player).getStats().swordBlock(), true, "&fSword Block", "&7*When holding a sword you will not", "&7be able to break any blocks.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().swordBlock()),
                    event -> {
                        Creative.get(player).getStats().setSwordBlock(player, !Creative.get(player).getStats().swordBlock());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COOKED_BEEF" : "364"), 1, false, true, "&fFood", "&7*The desired food/hunger level.",
                    "&7", "&a&lFood: &b&l" + Creative.get(player).getStats().foodLevel()), event -> {
                final InventoryHolder inventoryHolder = event.getInventory().getHolder();
                if (inventoryHolder != null) {
                    ((Interface) inventoryHolder).onTyping((Player) event.getView().getPlayer());
                    Menu.setTypingMenu(true, (Player) event.getWhoClicked(), ((Interface) inventoryHolder));
                }
            }
                    , query -> query.onClose(stateSnapshot -> userMenu(player))
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != Query.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        final String input = ChatColor.stripColor(stateSnapshot.getText().replace(" ", ""));
                        if (StringUtils.isInt(input)) {
                            Creative.get(player).getStats().setFoodLevel(player, Integer.parseInt(input));
                            Menu.setTypingMenu(false, stateSnapshot.getPlayer(), null);
                            return Collections.singletonList(Query.ResponseAction.close());
                        } else {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInvalid Integer"));
                        }
                    })
                    .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&7"))
                    .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputType").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputExample").replace("%input_example%", "BOAT").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number")))
                    .itemOutput(ItemHandler.getItem("FEATHER", 1, false, true, "&bStart typing...", "&8Preview of what", "&8you have entered.'"))
                    .title("Food Level:"), 0));
            pagedPane.addButton(new Button(ItemHandler.getItem("APPLE", 1, false, true, "&fHealth", "&7*The desired health level.",
                    "&7", "&a&lHealth: &b&l" + Creative.get(player).getStats().health()), event -> {
                final InventoryHolder inventoryHolder = event.getInventory().getHolder();
                if (inventoryHolder != null) {
                    ((Interface) inventoryHolder).onTyping((Player) event.getView().getPlayer());
                    Menu.setTypingMenu(true, (Player) event.getWhoClicked(), ((Interface) inventoryHolder));
                }
            }
                    , query -> query.onClose(stateSnapshot -> userMenu(player))
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != Query.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        final String input = ChatColor.stripColor(stateSnapshot.getText().replace(" ", ""));
                        if (StringUtils.isInt(input) && Integer.parseInt(input) <= (((int) Creative.get(player).getStats().heartScale()) * 2)) {
                            Creative.get(player).getStats().setHealth(player, Integer.parseInt(input), false);
                            Menu.setTypingMenu(false, stateSnapshot.getPlayer(), null);
                            return Collections.singletonList(Query.ResponseAction.close());
                        } else if (StringUtils.isInt(input)) {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInteger too Large"));
                        } else {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInvalid Integer"));
                        }
                    })
                    .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&7"))
                    .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputType").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputExample").replace("%input_example%", "BOAT").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), "&7", "&cCannot exceed your set heart-scale.", "&cCurrently cannot exceed " + (((int) Creative.get(player).getStats().heartScale()) * 2) + "."))
                    .itemOutput(ItemHandler.getItem("FEATHER", 1, false, true, "&bStart typing...", "&8Preview of what", "&8you have entered.'"))
                    .title("Heart Level:"), 0));
            pagedPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, true, "&fHeart Scale", "&7*The desired health scale.", "&7This is the number of hearts to", "&7be displayed in the user interface.",
                    "&7", "&a&lScale: &b&l" + Creative.get(player).getStats().heartScale()), event -> {
                final InventoryHolder inventoryHolder = event.getInventory().getHolder();
                if (inventoryHolder != null) {
                    ((Interface) inventoryHolder).onTyping((Player) event.getView().getPlayer());
                    Menu.setTypingMenu(true, (Player) event.getWhoClicked(), ((Interface) inventoryHolder));
                }
            }
                    , query -> query.onClose(stateSnapshot -> userMenu(player))
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != Query.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        final String input = ChatColor.stripColor(stateSnapshot.getText().replace(" ", ""));
                        if (StringUtils.isInt(input)) {
                            if (Integer.parseInt(input) < (Creative.get(player).getStats().health())) {
                                Creative.get(player).getStats().setHealth(player, Integer.parseInt(input) * 2, true);
                            }
                            Creative.get(player).getStats().setScale(player, Integer.parseInt(input));
                            Menu.setTypingMenu(false, stateSnapshot.getPlayer(), null);
                            return Collections.singletonList(Query.ResponseAction.close());
                        } else {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInvalid Integer"));
                        }
                    })
                    .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&7"))
                    .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputType").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputExample").replace("%input_example%", "BOAT").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number")))
                    .itemOutput(ItemHandler.getItem("FEATHER", 1, false, true, "&bStart typing...", "&8Preview of what", "&8you have entered.'"))
                    .title("Heart Scale:"), 0));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ROTTEN_FLESH" : "367"), 1, Creative.get(player).getStats().allowHunger(), true, "&fAllow Hunger", "&7*If your hunger should decrease.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().allowHunger()),
                    event -> {
                        Creative.get(player).getStats().setAllowHunger(player, !Creative.get(player).getStats().allowHunger());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, Creative.get(player).getStats().allowBurn(), true, "&fAllow Burn", "&7*If lava or fire should do damage.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().allowBurn()),
                    event -> {
                        Creative.get(player).getStats().setAllowBurn(player, !Creative.get(player).getStats().allowBurn());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem("BEDROCK", 1, Creative.get(player).getStats().unbreakableItems(), true, "&fUnbreakable Items", "&7*If items in creative should be unbreakable.",
                    "&7This is only effective in creative", "&7unbreakable items will automatically", "&7become breakable when exiting creative.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().unbreakableItems()),
                    event -> {
                        Creative.get(player).getStats().setUnbreakableItems(player, !Creative.get(player).getStats().unbreakableItems());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, Creative.get(player).getStats().blockDrops(), true, "&fBlock Drops", "&7*If blocks should drop when you", "&7break them using your hand or an item.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().blockDrops()),
                    event -> {
                        Creative.get(player).getStats().setBlockDrops(player, !Creative.get(player).getStats().blockDrops());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem("DISPENSER", 1, Creative.get(player).getStats().destroyPickups(), true, "&fDestroy Pickups", "&7*If items should be destroyed when", "&7the your inventory is full and", "&7you try to pick up items on the ground.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().destroyPickups()),
                    event -> {
                        Creative.get(player).getStats().setPickups(player, !Creative.get(player).getStats().destroyPickups());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "HOPPER" : "154"), 1, Creative.get(player).getStats().selfDrops(), true, "&fSelf Drops", "&7*If items should be allowed to be", "&7dropped manually and on player death.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().selfDrops()),
                    event -> {
                        Creative.get(player).getStats().setSelfDrops(player, !Creative.get(player).getStats().selfDrops());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CHEST_MINECART" : "342"), 1, Creative.get(player).getStats().itemStore(), true, "&fItem Store", "&7*If items should be allowed to be", "&7placed or stored in storage related", "&7items like chests and hoppers.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().itemStore()),
                    event -> {
                        Creative.get(player).getStats().setItemStore(player, !Creative.get(player).getStats().itemStore());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem("BEACON", 1, Creative.get(player).getStats().autoRestore(), true, "&fAuto Restore", "&7*If you exit the server or the server", "&7restarts while you are in creative, you", "&7will automatically be set back to", "&7creative upon entering the server.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().autoRestore()),
                    event -> {
                        Creative.get(player).getStats().setRestore(player, !Creative.get(player).getStats().autoRestore());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, Creative.get(player).getStats().god(), true, "&fInvulnerable", "&7*If you should take damage.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().god()),
                    event -> {
                        Creative.get(player).getStats().setGod(player, !Creative.get(player).getStats().god());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, true, "&fInvulnerable Delay", "&7*How long to wait after exiting creative", "&7before you can take damage again.",
                    "&7", "&a&lDelay: &b&l" + Creative.get(player).getStats().godDelay()), event -> {
                final InventoryHolder inventoryHolder = event.getInventory().getHolder();
                if (inventoryHolder != null) {
                    ((Interface) inventoryHolder).onTyping((Player) event.getView().getPlayer());
                    Menu.setTypingMenu(true, (Player) event.getWhoClicked(), ((Interface) inventoryHolder));
                }
            }
                    , query -> query.onClose(stateSnapshot -> userMenu(player))
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != Query.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        final String input = ChatColor.stripColor(stateSnapshot.getText().replace(" ", ""));
                        if (StringUtils.isInt(input)) {
                            Creative.get(player).getStats().setGodDelay(player, Integer.parseInt(input));
                            Menu.setTypingMenu(false, stateSnapshot.getPlayer(), null);
                            return Collections.singletonList(Query.ResponseAction.close());
                        } else {
                            return Collections.singletonList(Query.ResponseAction.replaceInputText(" ", "&cInvalid Integer"));
                        }
                    })
                    .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&7"))
                    .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputType").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number"), FakeCreative.getCore().getLang().getLangMessage("commands.menu.inputExample").replace("%input_example%", "BOAT").replace("%prefix% ", "").replace("%prefix%", "").replace("%input_example%", "10").replace("%input%", "Custom Number")))
                    .itemOutput(ItemHandler.getItem("FEATHER", 1, false, true, "&bStart typing...", "&8Preview of what", "&8you have entered.'"))
                    .title("Invulnerable Delay:"), 0));
            pagedPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, Creative.get(player).getStats().storeInventory(), true, "&fStore Inventory", "&7*If the previous gamemodes inventory", "&7should be saved when entering creative.", "&7This inventory will automatically", "&7be restored when exiting creative.",
                    "&7", "&a&lEnabled: &b&l" + Creative.get(player).getStats().storeInventory()),
                    event -> {
                        Creative.get(player).getStats().setStore(player, !Creative.get(player).getStats().storeInventory());
                        userMenu(player);
                    }));
            pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
            pagedPane.addButton(new Button(fillerPaneBItem), 7);
            pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
        });
        pagedPane.open(player);
    }

    /**
     * Checks if the ItemStack is a safe Material,
     * that it actually exists and is not AIR or NULL.
     *
     * @param item           - The ItemStack to be checked.
     * @param inventoryCheck - The Inventory used for checking the ItemStack.
     */
    private static boolean safeMaterial(final ItemStack item, final Inventory inventoryCheck) {
        inventoryCheck.setItem(0, item);
        final ItemStack itemStack = inventoryCheck.getItem(0);
        return itemStack != null && !itemStack.getType().name().equalsIgnoreCase("AIR") && !Creative.isBlackListed(item);
    }

    /**
     * Handles the Event when clicking a Menu item.
     *
     * @param event - InventoryClickEvent
     */
    private static void handleEvent(final InventoryClickEvent event) {
        Player clickPlayer = (Player) event.getWhoClicked();
        ItemStack clickItem = (event.getCurrentItem() != null ? event.getCurrentItem().clone() : event.getCurrentItem());
        ItemStack cursorItem = event.getCursor();
        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
            if (cursorItem == null || cursorItem.getType() == Material.AIR) {
                clickPlayer.setItemOnCursor(clickItem);
            } else {
                clickPlayer.setItemOnCursor(new ItemStack(Material.AIR));
            }
        } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.MIDDLE) {
            if (cursorItem == null || cursorItem.getType() == Material.AIR) {
                clickItem.setAmount(clickItem.getMaxStackSize());
                clickPlayer.setItemOnCursor(clickItem);
            } else {
                clickPlayer.setItemOnCursor(new ItemStack(Material.AIR));
            }
        } else if (event.getClick() == ClickType.NUMBER_KEY) {
            if (cursorItem == null || cursorItem.getType() == Material.AIR) {
                clickItem.setAmount(clickItem.getMaxStackSize());
                clickPlayer.getOpenInventory().getBottomInventory().setItem(event.getHotbarButton(), clickItem);
            }
        } else if (event.getClick() == ClickType.DROP) {
            PlayerHandler.dropItem(clickPlayer, clickItem);
        } else if (event.getClick() == ClickType.CONTROL_DROP) {
            clickItem.setAmount(clickItem.getMaxStackSize());
            PlayerHandler.dropItem(clickPlayer, clickItem);
        }
    }

    /**
     * Adds the Selectable Creative Tabs to the Menu.
     *
     * @param pagedPane    - The Menu having the items added.
     * @param currentCount - The number of blank spaces to fill.
     * @param selected     - The tab that is currently selected.
     */
    private static void addSections(final Interface pagedPane, final int currentCount, final int selected) {
        if (currentCount != 0) {
            pagedPane.addButton(new Button(new ItemStack(Material.AIR)), currentCount);
        }
        pagedPane.addButton(new Button(fillerPaneBItem), 9);
        pagedPane.addButton(new Button(ItemHandler.getItem("COMPASS", 1, (selected == 0), true, (selected == 0 ? "&a" : "&f") + "Search Items", "&7", "&8&oClick to view", "&8&oShift+Left-click to search."), event -> {
            final InventoryHolder inventoryHolder = event.getInventory().getHolder();
            if (event.getClick() == ClickType.SHIFT_LEFT && inventoryHolder != null) {
                ((Interface) inventoryHolder).onTyping((Player) event.getView().getPlayer());
                Menu.setTypingMenu(true, (Player) event.getWhoClicked(), ((Interface) inventoryHolder));
            } else {
                creativeMenu(event.getWhoClicked(), 0, null);
                pagedPane.allowChat(true);
            }
        }, query -> query.onClose(stateSnapshot -> creativeMenu(stateSnapshot.getPlayer(), 0, ChatColor.stripColor(stateSnapshot.getText())))
                .onClick((slot, stateSnapshot) -> {
                    if (slot != Query.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    Menu.setTypingMenu(false, stateSnapshot.getPlayer(), null);
                    return Collections.singletonList(Query.ResponseAction.close());
                })
                .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&7"))
                .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", FakeCreative.getCore().getLang().getLangMessage("commands.menu.searchType").replace("%prefix% ", "").replace("%prefix%", ""), FakeCreative.getCore().getLang().getLangMessage("commands.menu.searchExample").replace("%input_example%", "BOAT").replace("%prefix% ", "").replace("%prefix%", "")))
                .itemOutput(ItemHandler.getItem("FEATHER", 1, false, true, "&bStart typing...", "&8Preview of what", "&8you have entered.'"))
                .title("Search Items:"), 0));
        pagedPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, (selected == 1), true, (selected == 1 ? "&a" : "&f") + "Miscellaneous", "&7", "&8&oClick to view"), event -> miscellaneousMenu(event.getWhoClicked())));
        pagedPane.addButton(new Button(ItemHandler.getItem("APPLE", 1, (selected == 2), true, (selected == 2 ? "&a" : "&f") + "Foodstuffs", "&7", "&8&oClick to view"), event -> foodMenu(event.getWhoClicked())));
        pagedPane.addButton(new Button(ItemHandler.getItem("IRON_AXE", 1, (selected == 3), true, (selected == 3 ? "&a" : "&f") + "Tools", "&7", "&8&oClick to view"), event -> toolsMenu(event.getWhoClicked())));
        pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_SWORD" : "283"), 1, (selected == 4), true, (selected == 4 ? "&a" : "&f") + "Combat", "&7", "&8&oClick to view"), event -> combatMenu(event.getWhoClicked())));
        pagedPane.addButton(new Button(ItemHandler.getItem("WATER_BOTTLE", 1, (selected == 5), true, (selected == 5 ? "&a" : "&f") + "Brewing", "&7", "&8&oClick to view"), event -> brewingMenu(event.getWhoClicked())));
        pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BRICKS" : "45"), 1, (selected == 6), true, (selected == 6 ? "&a" : "&f") + "Building Blocks", "&7", "&8&oClick to view"), event -> buildingMenu(event.getWhoClicked())));
        pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PEONY" : "175:5"), 1, (selected == 7), true, (selected == 7 ? "&a" : "&f") + "Decoration Blocks", "&7", "&8&oClick to view"), event -> decorationMenu(event.getWhoClicked())));
        pagedPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, (selected == 8), true, (selected == 8 ? "&a" : "&f") + "Redstone", "&7", "&8&oClick to view"), event -> redstoneMenu(event.getWhoClicked())));
    }

    /**
     * Fixes items to be set as distinguished in a menu.
     *
     * @param material - The material being spliced.
     * @param section  - The currently selected tab.
     * @return The spliced Material ItemStacks.
     */
    private static List<ItemStack> spliceMaterial(final Material material, final int section) {
        List<ItemStack> items = new ArrayList<>();
        boolean custom = false;
        if (material.name().contains("ENCHANTED_BOOK")) {
            custom = true;
            for (Enchantment enchant : ItemHandler.getEnchants()) {
                if (section == 0 || (section == 1 && !ToolEnchants.isEnchant(enchant)) || (section == 2 && ToolEnchants.isEnchant(enchant))) {
                    ItemStack enc = new ItemStack(material);
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enc.getItemMeta();
                    if (meta != null) {
                        meta.addStoredEnchant(enchant, enchant.getMaxLevel(), false);
                        enc.setItemMeta(meta);
                        items.add(enc);
                    }
                }
            }
        } else if (material.name().contains("TIPPED_ARROW") || material.name().contains("POTION")) {
            custom = true;
            for (PotionType potion : PotionType.values()) {
                if ((!potion.name().equalsIgnoreCase("UNCRAFTABLE") &&
                        !potion.name().equalsIgnoreCase("WATER") &&
                        !potion.name().equalsIgnoreCase("MUNDANE") &&
                        !potion.name().equalsIgnoreCase("THICK") &&
                        !potion.name().equalsIgnoreCase("AWKWARD"))
                        || (material.name().contains("POTION") && !potion.name().equalsIgnoreCase("UNCRAFTABLE"))) {
                    ItemStack pot = new ItemStack(material);
                    PotionMeta meta = (PotionMeta) pot.getItemMeta();
                    if (ServerUtils.hasSpecificUpdate("1_9") && meta != null) {
                        if (ServerUtils.hasPreciseUpdate("1_20_3")) {
                            meta.setBasePotionType(potion);
                        } else {
                            LegacyAPI.setPotionData(meta, potion);
                        }
                        pot.setItemMeta(meta);
                        items.add(pot.clone());
                        if (potion.isExtendable() && !ServerUtils.hasPreciseUpdate("1_20_3")) {
                            LegacyAPI.setPotionData(meta, potion, true, false);
                            pot.setItemMeta(meta);
                            items.add(pot.clone());
                        }
                        if (potion.isUpgradeable() && !ServerUtils.hasPreciseUpdate("1_20_3")) {
                            LegacyAPI.setPotionData(meta, potion, false, true);
                            pot.setItemMeta(meta);
                            items.add(pot.clone());
                        }
                    } else {
                        for (int data : Potion.getData(potion)) {
                            items.add(LegacyAPI.newItemStack(material, 1, (short) data).clone());
                        }
                    }
                }
            }
        } else if (material.name().contains("MONSTER_EGG")) {
            custom = true;
            for (EntityType entity : EntityType.values()) {
                try {
                    items.add(new ItemStack(ItemHandler.getMaterial(entity.name().toUpperCase() + "_SPAWN_EGG", null)));
                } catch (Exception e) {
                    final short entityId = (short) Monster.getId(entity);
                    if (entityId > 0) {
                        items.add(LegacyAPI.newItemStack(ItemHandler.getMaterial("MONSTER_EGG", null), 1, entityId));
                    }
                }
            }
        }
        if (!custom || !ServerUtils.hasSpecificUpdate("1_9")) {
            items.add(new ItemStack(material));
        }
        return items;
    }

    /**
     * Attempts to close all online players inventories,
     * if they are found to have the plugin UI open.
     */
    public static void closeMenu() {
        PlayerHandler.forOnlinePlayers(player -> {
            if (isOpen(player) || modifyMenu(player)) {
                player.closeInventory();
            } else if (typingMenu(player)) {
                typingMenu.get(PlayerHandler.getPlayerID(player)).closeQuery(player);
            }
        });
    }

    /**
     * Sets the Player to the Modify Menu.
     *
     * @param bool   - If the Player is in the Menu.
     * @param player - The Player to be set to the Modify Menu.
     */
    public static void setModifyMenu(final boolean bool, final Player player) {
        try {
            SchedulerUtils.runAsync(() -> {
                if (bool) {
                    modifyMenu.add(PlayerHandler.getPlayerID(player));
                } else if (!modifyMenu.isEmpty()) {
                    modifyMenu.remove(PlayerHandler.getPlayerID(player));
                }
            });
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

//  ===================================================================================================================================================================================================================

//  ============================================== //
//  			   Menu Utilities        	       //
//  ============================================== //

    /**
     * Sets the Player to the Typing Menu.
     *
     * @param bool   - If the Player is in the Menu.
     * @param player - The Player to be set to the Typing Menu.
     */
    public static void setTypingMenu(final boolean bool, final Player player, final Interface interFace) {
        try {
            SchedulerUtils.runAsync(() -> {
                if (bool) {
                    typingMenu.put(PlayerHandler.getPlayerID(player), interFace);
                } else if (!typingMenu.isEmpty()) {
                    typingMenu.remove(PlayerHandler.getPlayerID(player));
                }
            });
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Checks if the Player is in the Modify Menu.
     *
     * @param player - The Player to be checked.
     * @return If the Player is in the Modify Menu.
     */
    public static boolean modifyMenu(final Player player) {
        return modifyMenu.contains(PlayerHandler.getPlayerID(player));
    }

    /**
     * Checks if the Player is in the typing Menu.
     *
     * @param player - The Player to be checked.
     * @return If the Player is in the typing Menu.
     */
    public static boolean typingMenu(final Player player) {
        return typingMenu.get(PlayerHandler.getPlayerID(player)) != null;
    }

//  ==============================================================================================================================================================================================================================================================

    /**
     * Checks if the Player has the GUI Menu open.
     *
     * @param player - The Player to be checked.
     * @return If the GUI Menu is open.
     */
    public static boolean isOpen(final Player player) {
        if (GUIName == null) {
            GUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7           &0&nCreative Menu") : StringUtils.colorFormat("&7           &0&n Creative Menu");
        }
        if (HotbarGUIName == null) {
            HotbarGUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7            &0&nHotbar Menu") : StringUtils.colorFormat("&7            &0&n Hotbar Menu");
        }
        if (userGUIName == null) {
            userGUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7               &0&nUser Menu") : StringUtils.colorFormat("&7              &0&n User Menu");
        }
        return player != null &&
                ((GUIName != null && player.getOpenInventory().getTitle().equalsIgnoreCase(StringUtils.colorFormat(GUIName)))
                        || (HotbarGUIName != null && player.getOpenInventory().getTitle().equalsIgnoreCase(StringUtils.colorFormat(HotbarGUIName)))
                        || (userGUIName != null && player.getOpenInventory().getTitle().equalsIgnoreCase(StringUtils.colorFormat(userGUIName))));
    }




}