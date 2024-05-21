package me.RockinChaos.fakecreative.listeners;

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Storable implements Listener {
    /**
     * Prevents the player from clicking to store the creative item.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onInventoryStore(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String invType = event.getView().getType().toString();
        if (invType != null) {
            if (event.getRawSlot() >= event.getInventory().getSize() && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getRawSlot() < event.getInventory().getSize()) {
                if ((invType.contains("CHEST") || invType.contains("BARREL") || invType.contains("BREWING") || invType.contains("FURNACE") || invType.contains("GRINDSTONE") || invType.contains("SHULKER_BOX")
                        || invType.contains("HOPPER") || invType.contains("ANVIL") || invType.contains("WORKBENCH") || invType.contains("DISPENSER") || invType.contains("DROPPER")) && Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().itemStore()) {
                    event.setCancelled(true);
                    PlayerHandler.updateInventory(player, 1L);
                } else if ((invType.contains("ENCHANTING") || invType.contains("ANVIL") || invType.contains("GRINDSTONE")) && Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().itemStore()) {
                    event.setCancelled(true);
                    PlayerHandler.updateInventory(player, 1L);
                }
            }
        }
    }

    /**
     * Prevents the player from click dragging to store the creative item.
     *
     * @param event - InventoryDragEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onInventoryDragToStore(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        String invType = event.getView().getType().toString();
        int inventorySize = event.getInventory().getSize();
        for (int i : event.getRawSlots()) {
            if (i < inventorySize) {
                if (invType != null) {
                    if ((invType.contains("CHEST") || invType.contains("BARREL") || invType.contains("BREWING") || invType.contains("FURNACE") || invType.contains("GRINDSTONE") || invType.contains("SHULKER_BOX")
                            || invType.contains("HOPPER") || invType.contains("ANVIL") || invType.contains("WORKBENCH") || invType.contains("DISPENSER") || invType.contains("DROPPER")) && Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().itemStore()) {
                        event.setCancelled(true);
                        PlayerHandler.updateInventory(player, 1L);
                        break;
                    } else if ((invType.contains("ENCHANTING") || invType.contains("ANVIL") || invType.contains("GRINDSTONE")) && Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().itemStore()) {
                        event.setCancelled(true);
                        PlayerHandler.updateInventory(player, 1L);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Prevents the player from storing the creative item on entities.
     *
     * @param event - PlayerInteractEntityEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType().name().equalsIgnoreCase("ITEM_FRAME") || event.getRightClicked().getType().name().equalsIgnoreCase("FOX") || event.getRightClicked().getType().name().equalsIgnoreCase("ALLAY")) {
            Player player = event.getPlayer();
            if (Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().itemStore()) {
                event.setCancelled(true);
                PlayerHandler.updateInventory(player, 1L);
            }
        }
    }

    /**
     * Prevents the player from storing the creative item on entities.
     *
     * @param event - PlayerInteractAtEntityEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType().name().equalsIgnoreCase("ARMOR_STAND") || event.getRightClicked().getType().name().equalsIgnoreCase("FOX") || event.getRightClicked().getType().name().equalsIgnoreCase("ALLAY")) {
            Player player = event.getPlayer();
            if (!Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().itemStore()) {
                event.setCancelled(true);
                PlayerHandler.updateInventory(player, 1L);
            }
        }
    }
}