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
package me.RockinChaos.fakecreative.utils.interfaces.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.handlers.ItemHandler;
import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.handlers.modes.Creative;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.StringUtils;
import me.RockinChaos.fakecreative.utils.api.LanguageAPI;
import me.RockinChaos.fakecreative.utils.interfaces.Button;
import me.RockinChaos.fakecreative.utils.interfaces.Interface;
import me.RockinChaos.fakecreative.utils.types.Brewing;
import me.RockinChaos.fakecreative.utils.types.BuildingBlocks;
import me.RockinChaos.fakecreative.utils.types.Combat;
import me.RockinChaos.fakecreative.utils.types.Decoration;
import me.RockinChaos.fakecreative.utils.types.Miscellaneous;
import me.RockinChaos.fakecreative.utils.types.Redstone;
import me.RockinChaos.fakecreative.utils.types.ToolEnchants;
import me.RockinChaos.fakecreative.utils.types.Tools;

	
/**
* Handles the in-game GUI.
* 
*/
public class Menu {
	private String GUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7           &0&nCreative Menu") : StringUtils.colorFormat("&7           &0&n Creative Menu");
	private String HotbarGUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7            &0&nHotbar Menu") : StringUtils.colorFormat("&7            &0&n Hotbar Menu");
	private ItemStack fillerPaneBItem = ItemHandler.getItem("STAINED_GLASS_PANE:15", 1, false, false, "&7", "");
	private ItemStack fillerPaneGItem = ItemHandler.getItem("STAINED_GLASS_PANE:7", 1, false, false, "&7", "");
	private ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nExit", "&7", "&7*Return to playing the game.");
	private List<Player> modifyMenu = new ArrayList<Player>();
	
	private static Menu creator;

//  ============================================== //
//  			   Selection Menus      	       //
//	============================================== //
	
   /**
    * Opens the MAIN CREATIVE PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void creativeMenu(final CommandSender sender, final int selected, final String search) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			boolean failure = true;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && ((search != null && !search.isEmpty() && StringUtils.containsIgnoreCase(material.name(), search)) || (search == null || search.isEmpty())) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 0)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						failure = false;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, selected); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, selected); }
			else if (failure) {
				this.addSections(pagedPane, 27, selected);
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
	public void miscellaneousMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && Miscellaneous.isMiscellaneous(material) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 0)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 1); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 1); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN FOOD PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void foodMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && material.isEdible() && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 0)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 2); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 2); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN TOOLS PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void toolsMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && Tools.isTools(material) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 2)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 3); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 3); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN COMBAT PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void combatMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && Combat.isCombat(material) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 1)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 4); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 4); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN BREWING PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void brewingMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && Brewing.isBrewing(material) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 0)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 5); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 5); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN BUILDING PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void buildingMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && BuildingBlocks.isBuildingBlocks(material) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 0)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 6); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 6); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN DECORATION PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void decorationMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && Decoration.isDecoration(material) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 0)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 7); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 7); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN REDSTONE PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void redstoneMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(true, 6, this.GUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			int currentCount = 0;
			Inventory inventoryCheck = FakeCreative.getInstance().getServer().createInventory(null, 9, this.GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && Redstone.isRedstone(material) && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
					for (ItemStack item: this.spliceMaterial(material, 0)) {
						pagedPane.addButton(new Button(item, event -> this.handleEvent(event)));
						currentCount++;
						if (currentCount == 27) { currentCount = 0; this.addSections(pagedPane, currentCount, 8); }
					}
				}
			}
			if (currentCount != 0) { currentCount = (27 - currentCount); this.addSections(pagedPane, currentCount, 8); }
			inventoryCheck.clear();
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN HOTBAR PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void hotbarMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(false, 2, this.HotbarGUIName, player);
		pagedPane.setReturnButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			for (int i = 1; i <= 9; i++) {
				final int k = i;
				String existingMats = "";
				String hotbarData = Creative.getHotbar(player, k);
				if (hotbarData != null) {
					Inventory inventory = ItemHandler.deserializeInventory(hotbarData);
					for (int j = 0; j < 9; j++) {
						if (inventory.getItem(j) != null && inventory.getItem(j).getType() != Material.AIR) {
							existingMats += "&7" + (inventory.getItem(j).getType().name() + "x" + inventory.getItem(j).getAmount()) + " /n ";
						}
					}
				}
				final boolean matExisting = !existingMats.isEmpty();
				pagedPane.addButton(new Button(ItemHandler.getItem("PAPER", k, false, false,  "&a&l[" + k + "] " + "&aHotbar", 
						((existingMats != null && !existingMats.isEmpty()) ? existingMats.substring(0, existingMats.length() - 4) : "&7No items saved for this hotbar."), "&7", 
						"&8&oLeft-click to save.", (existingMats != null && !existingMats.isEmpty() ? "&8&oShift+Left-click to load." : ""), (existingMats != null && !existingMats.isEmpty() ? "&8&oRight-click to view." : "")), event -> {
					boolean empty = true;
					if (event.getClick() == ClickType.LEFT) {
						if (PlayerHandler.isFakeCreativeMode(player)) {
							Inventory inventoryData = Bukkit.getServer().createInventory(null, 9);
							for (int j = 0; j < 9; j++) {
								if (player.getOpenInventory().getBottomInventory().getItem(j) != null && player.getOpenInventory().getBottomInventory().getItem(j).getType() != Material.AIR) {
									inventoryData.setItem(j, player.getOpenInventory().getBottomInventory().getItem(j).clone()); empty = false;
								}
							}
							if (!empty) {
								String[] placeHolders = LanguageAPI.getLang(false).newString();
								placeHolders[2] = Integer.toString(k);
								LanguageAPI.getLang(false).sendLangMessage("commands.menu.savedHotbar", sender, placeHolders);
								Creative.saveHotbar(player, ItemHandler.serializeInventory(inventoryData), k);
								SchedulerUtils.run(() -> this.hotbarMenu(sender));
							}
						}
					} else if (event.getClick() == ClickType.SHIFT_LEFT) {
						if (PlayerHandler.isFakeCreativeMode(player)) {
							String inventoryData = Creative.getHotbar(player, k);
							if (inventoryData != null) {
								Inventory inventory = ItemHandler.deserializeInventory(inventoryData);
								for (int j = 0; j < 9; j++) {
									if (inventory.getItem(j) != null && inventory.getItem(j).getType() != Material.AIR) {
										player.getOpenInventory().getBottomInventory().setItem(j, inventory.getItem(j).clone()); empty = false;
									}
								}
							}
							if (!empty) {
								String[] placeHolders = LanguageAPI.getLang(false).newString();
								placeHolders[2] = Integer.toString(k);
								LanguageAPI.getLang(false).sendLangMessage("commands.menu.loadedHotbar", sender, placeHolders);
								SchedulerUtils.run(() -> this.hotbarMenu(sender));
							}
						}
					} else if ((event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) && matExisting) {
						this.viewHotbar(player, k);
					}
				}));
			}
			pagedPane.addButton(new Button(this.exitItem, event -> player.closeInventory()));
			pagedPane.addButton(new Button(this.fillerPaneBItem), 3);
			pagedPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, true, true, "&e&l&nInformation", "&7", "&8Left-click to save", "&8your current hotbar.", "&7", "&8Shift+Left-click to load", "&8the clicked hotbar.", "&7", "&8Right-click to view", "&8the clicked hotbar.")));
			pagedPane.addButton(new Button(this.fillerPaneBItem), 3);
			pagedPane.addButton(new Button(this.exitItem, event -> player.closeInventory()));
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the MAIN VIEWING PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public void viewHotbar(final Player player, final int hotbar) {
		Interface pagedPane = new Interface(false, 2, this.HotbarGUIName, player);
		pagedPane.allowClick(true);
		SchedulerUtils.runAsync(() -> {
			if (PlayerHandler.isFakeCreativeMode(player)) {
				String inventoryData = Creative.getHotbar(player, hotbar);
				if (inventoryData != null) {
					Inventory inventory = ItemHandler.deserializeInventory(inventoryData);
					for (int j = 0; j < 9; j++) {
						if (inventory.getItem(j) != null && inventory.getItem(j).getType() != Material.AIR) {
							pagedPane.addButton(new Button(inventory.getItem(j).clone(), event -> this.handleEvent(event)));
						} else {
							pagedPane.addButton(new Button(this.fillerPaneGItem));
						}
					}
				}
			}
			pagedPane.addButton(new Button(this.exitItem, event -> this.hotbarMenu(player)));
			pagedPane.addButton(new Button(this.fillerPaneBItem), 7);
			pagedPane.addButton(new Button(this.exitItem, event -> this.hotbarMenu(player)));
		});
		pagedPane.open(player);
	}
	
   /**
    * Checks if the ItemStack is a safe Material,
    * that it actually exists and is not AIR or NULL.
    * 
    * @param item - The ItemStack to be checked.
    * @param inventoryCheck - The Inventory used for checking the ItemStack.
    */
	private boolean safeMaterial(ItemStack item, Inventory inventoryCheck) {
		inventoryCheck.setItem(0, item);
		if (inventoryCheck.getItem(0) != null && inventoryCheck.getItem(0).getType().name() != "AIR") {
			return true;
		}
		return false;
	}
	
//  ===================================================================================================================================================================================================================
	
//  ============================================== //
//  			   Menu Utilities        	       //
//  ============================================== //
	
   /**
    * Handles the Event when clicking a Menu item.
    * 
    * @param event - InventoryClickEvent
    */
	private void handleEvent(final InventoryClickEvent event) {
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
    * @param pagedPane - The Menu having the items added.
    * @param currentCount - The number of blank spaces to fill.
    * @param selected - The tab that is currently selected.
    */
	private void addSections(final Interface pagedPane, final int currentCount, final int selected) {
		if (currentCount != 0) { pagedPane.addButton(new Button(new ItemStack(Material.AIR)), currentCount); }
		pagedPane.addButton(new Button(this.fillerPaneBItem), 9);
		if (selected != 9) {
			pagedPane.addButton(new Button(ItemHandler.getItem("COMPASS", 1, (selected == 0 ? true : false), true, (selected == 0 ? "&a"  : "&f") + "Search Items", "&7", "&8&oClick to view", "&8&oDouble-click to search."), event -> { 
				this.creativeMenu(event.getWhoClicked(), 9, null); 
			}));
		} else if (selected == 9) {
			pagedPane.addButton(new Button(ItemHandler.getItem("COMPASS", 1, (selected == 9 ? true : false), true, (selected == 9 ? "&a"  : "&f") + "Search Items", "&7", "&8&oClick to view", "&8&oDouble-click to search."), event -> {
				event.getWhoClicked().closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[3] = "&cDIAMOND&a or &cDIA";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.searchType", event.getWhoClicked(), placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.searchExample", event.getWhoClicked(), placeHolders);
			}, event -> {
				this.creativeMenu(event.getPlayer(), 0, ChatColor.stripColor(event.getMessage())); 
			}));
		}
		pagedPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, (selected == 1 ? true : false), true, (selected == 1 ? "&a"  : "&f") + "Miscellaneous", "&7", "&8&oClick to view"), event -> this.miscellaneousMenu(event.getWhoClicked())));
		pagedPane.addButton(new Button(ItemHandler.getItem("APPLE", 1, (selected == 2 ? true : false), true, (selected == 2 ? "&a"  : "&f") + "Foodstuffs", "&7", "&8&oClick to view"), event -> this.foodMenu(event.getWhoClicked())));
		pagedPane.addButton(new Button(ItemHandler.getItem("IRON_AXE", 1, (selected == 3 ? true : false), true, (selected == 3 ? "&a"  : "&f") + "Tools", "&7", "&8&oClick to view"), event -> this.toolsMenu(event.getWhoClicked())));
		pagedPane.addButton(new Button(ItemHandler.getItem("GOLDEN_SWORD", 1, (selected == 4 ? true : false), true, (selected == 4 ? "&a"  : "&f") + "Combat", "&7", "&8&0Click to view"), event -> this.combatMenu(event.getWhoClicked())));
		pagedPane.addButton(new Button(ItemHandler.getItem("WATER_BOTTLE", 1, (selected == 5 ? true : false), true, (selected == 5 ? "&a"  : "&f") + "Brewing", "&7", "&8&0Click to view"), event -> this.brewingMenu(event.getWhoClicked())));
		pagedPane.addButton(new Button(ItemHandler.getItem("BRICKS", 1, (selected == 6 ? true : false), true, (selected == 6 ? "&a"  : "&f") + "Building Blocks", "&7", "&8&0Click to view"), event -> this.buildingMenu(event.getWhoClicked())));
		pagedPane.addButton(new Button(ItemHandler.getItem("PEONY", 1, (selected == 7 ? true : false), true, (selected == 7 ? "&a"  : "&f") + "Decoration Blocks", "&7", "&8&0Click to view"), event -> this.decorationMenu(event.getWhoClicked())));
		pagedPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, (selected == 8 ? true : false), true, (selected == 8 ? "&a"  : "&f") + "Redstone", "&7", "&8&0Click to view"), event -> this.redstoneMenu(event.getWhoClicked())));
	}
	
   /**
    * Fixes items to be set as distinguished in a menu.
    * 
    * @param material - The material being spliced.
    * @param section - The currently selected tab.
    * @retun The spliced Material ItemStacks.
    */
	private List<ItemStack> spliceMaterial(final Material material, final int section) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		boolean custom = false;
		if (material.name().contains("ENCHANTED_BOOK")) {
			custom = true;
			for (Enchantment enchant : Enchantment.values()) {
				if (section == 0 || (section == 1 && !ToolEnchants.isEnchant(enchant)) || (section == 2 && ToolEnchants.isEnchant(enchant))) {
					ItemStack enc = new ItemStack(material);
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enc.getItemMeta();
					meta.addStoredEnchant(enchant, enchant.getMaxLevel(), false);
	        		enc.setItemMeta(meta);
					items.add(enc);
				}
			}
		} if (material.name().contains("TIPPED_ARROW") || material.name().contains("POTION")) {
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
					meta.setBasePotionData(new PotionData(potion));
	        		pot.setItemMeta(meta);
					items.add(pot.clone());
					if (potion.isExtendable()) {
						meta.setBasePotionData(new PotionData(potion, true, false));
						pot.setItemMeta(meta);
						items.add(pot.clone());
					}
					if (potion.isUpgradeable()) {
						meta.setBasePotionData(new PotionData(potion, false, true));
						pot.setItemMeta(meta);
						items.add(pot.clone());
					}
				}
			}
		} 
		if (!custom) { items.add(new ItemStack(material)); }
		return items;
	}

//  ==============================================================================================================================================================================================================================================================
	
   /**
    * Attemps to close all online players inventories, 
    * if they are found to have the plugin UI open.
    * 
    */
	public void closeMenu() {
		PlayerHandler.forOnlinePlayers(player -> { 
			if (this.isOpen(player) || this.modifyMenu(player)) {
				player.closeInventory();
			}
		});
	}
	
   /**
    * Sets the Player to the Modify Menu.
    * 
    * @param bool - If the Player is in the Menu.
    * @param player - The Player to be set to the Modify Menu.
    */
	public void setModifyMenu(final boolean bool, final Player player) {
		if (bool) { this.modifyMenu.add(player); } 
		else { this.modifyMenu.remove(player); }
	}
	
   /**
    * Checks if the Player is in the Modify Menu.
    * 
    * @param player - The Player to be checked.
    * @return If the Player is in the Modify Menu.
    */
	public boolean modifyMenu(final Player player) {
		return this.modifyMenu.contains(player);
	}
	
   /**
    * Checks if the Player has the GUI Menu open.
    * 
    * @param player - The Player to be checked.
    * @return If the GUI Menu is open.
    */
	public boolean isOpen(final Player player) {
		if (player != null) {
			return (this.GUIName != null && player.getOpenInventory().getTitle().toString().equalsIgnoreCase(StringUtils.colorFormat(this.GUIName))) || this.HotbarGUIName != null && player.getOpenInventory().getTitle().toString().equalsIgnoreCase(StringUtils.colorFormat(this.HotbarGUIName));
		}
		return false;
	}
	
   /**
    * Gets the instance of the UI.
    * 
    *
    * @return The UI instance.
    */
    public static Menu getCreator() { 
        if (creator == null) { creator = new Menu(); }
        return creator; 
    } 
}