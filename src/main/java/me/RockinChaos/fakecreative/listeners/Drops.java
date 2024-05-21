package me.RockinChaos.fakecreative.listeners;

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class Drops implements Listener {

    /**
     * Prevents the player from dropping all items while in creative.
     *
     * @param event - PlayerDropItemEvent.
     */
    @EventHandler(ignoreCancelled = true)
    private void onSelfDrops(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().selfDrops()) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents the player from dropping all items on death.
     *
     * @param event - PlayerDeathEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onDeathDrops(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (Creative.isCreativeMode(player, true) && !Creative.get(player).getStats().selfDrops()) {
            event.getEntity().getInventory().clear();
            event.getDrops().clear();
        }
    }
}