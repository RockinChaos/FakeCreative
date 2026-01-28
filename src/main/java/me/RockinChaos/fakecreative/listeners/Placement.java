package me.RockinChaos.fakecreative.listeners;

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.types.PlaceHolder;
import me.RockinChaos.core.utils.types.TransformBlocks;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.modes.creative.Creative;
import me.RockinChaos.fakecreative.modes.instance.PlayerStats;
import me.RockinChaos.fakecreative.utils.sql.DataObject;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Placement implements Listener {

    /**
     * Registers version-specific placement event listeners based on server version.
     * Listeners are conditionally registered to avoid NoClassDefFoundError on older versions.
     */
    public Placement() {
        if (ServerUtils.hasSpecificUpdate("1_20") && StringUtils.isRegistered(Placement_1_20.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Placement_1_20(), FakeCreative.getCore().getPlugin());
        }
        if (ServerUtils.hasSpecificUpdate("1_14") && StringUtils.isRegistered(Placement_1_14.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Placement_1_14(), FakeCreative.getCore().getPlugin());
        }
        if (ServerUtils.hasSpecificUpdate("1_13") && StringUtils.isRegistered(Placement_1_13.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Placement_1_13(), FakeCreative.getCore().getPlugin());
        }
        if (ServerUtils.hasPreciseUpdate("1_8_7") && StringUtils.isRegistered(Placement_1_8_7.class.getSimpleName())) {
            FakeCreative.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Placement_1_8_7(), FakeCreative.getCore().getPlugin());
        }
    }

    /**
     * Used for debugging creatively placed blocks, checking if a block is owned by a player.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onDebugBlockOwner(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        final Player player = event.getPlayer();
        final String hand = ServerUtils.hasSpecificUpdate("1_9") ? event.getHand() != null ? event.getHand().name() : "UNKNOWN" : "HAND";
        if (block != null && hand.equals("HAND") && FakeCreative.getCore().getData().debugEnabled() && player.isSneaking() && player.isOp()) {
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData != null && (ownerData.state.equals(block.getType().name()) || TransformBlocks.isTransformation(ownerData.state, block.getType().name()))) {
                player.sendMessage(StringUtils.colorFormat("&a&l[FC_DEBUG]&a Got placed " + (ServerUtils.hasSpecificUpdate("1_14") && (block.getState()) instanceof TileState ? "Tile &l" : "Block &l") + block.getType() + "&a with owning player &l" + ownerData.playerId));
            } else {
                player.sendMessage(StringUtils.colorFormat("&c&l[FC_DEBUG]&c Got placed " + (ServerUtils.hasSpecificUpdate("1_14") && (block.getState()) instanceof TileState ? "Tile &l" : "Block &l") + block.getType() + "&c with no owning player"));
            }
        }
    }

    /**
     * Marks blocks placed by fake creative players with ownership data.
     *
     * @param event - BlockPlaceEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlaceBlockOwner(final BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        if (Creative.isCreativeMode(event.getPlayer(), true)) {
            setBlockOwner(block, PlayerHandler.getPlayerID(event.getPlayer()), block.getType().name());
        }
    }

    /**
     * Handles block protection and drop prevention for fake creative blocks.
     * Validates block transformations and prevents unauthorized breaking.
     *
     * @param event - BlockBreakEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBreakBlockOwner(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Player breaker = event.getPlayer();
        if (block.getType().name().equals("PISTON_HEAD") || block.getType().name().equals("PISTON_EXTENSION")) {
            Block baseBlock = null;
            if (ServerUtils.hasSpecificUpdate("1_13")) {
                final BlockData blockData = block.getBlockData();
                if (blockData instanceof PistonHead) {
                    final BlockFace facing = ((PistonHead) blockData).getFacing();
                    baseBlock = block.getLocation().clone().subtract(facing.getModX(), facing.getModY(), facing.getModZ()).getBlock();
                }
            } else {
                final MaterialData data = block.getState().getData();
                if (data instanceof PistonExtensionMaterial) {
                    final BlockFace facing = ((PistonExtensionMaterial) data).getFacing();
                    baseBlock = block.getRelative(facing.getOppositeFace());
                }
            }
            if (baseBlock != null) {
                final String baseType = baseBlock.getType().name();
                if (baseType.equals("PISTON") || baseType.equals("STICKY_PISTON") || baseType.equals("PISTON_BASE") || baseType.equals("PISTON_STICKY_BASE")) {
                    final OwnerData ownerData = getBlockOwner(baseBlock);
                    if (ownerData == null) return;
                    if (!ownerData.state.equals(baseBlock.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, baseBlock.getType().name())) {
                        removeBlockOwner(block);
                        return;
                    }
                    final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
                    if (playerStats != null && !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(breaker)) && playerStats.protectPlacements()) {
                        event.setCancelled(true);
                        final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "BREAK").with(PlaceHolder.Holder.OBJECT, ServerUtils.hasSpecificUpdate("1_13") ? Material.PISTON.name() : "PISTON").with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                        FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", breaker, placeHolders);
                        return;
                    }
                    if (playerStats != null && !playerStats.dropPlacements()) {
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType(), 6);
                        block.setType(Material.AIR);
                        event.setExpToDrop(0);
                    }
                    removeBlockOwner(baseBlock);
                    return;
                }
            }
        }
        final OwnerData ownerData = getBlockOwner(block);
        if (ownerData == null) return;
        if (!ownerData.state.equals(block.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, block.getType().name())) {
            removeBlockOwner(block);
            return;
        }
        final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
        if (playerStats != null && !playerStats.dropPlacements()) {
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType(), 6);
            block.setType(Material.AIR);
            event.setExpToDrop(0);
        }
        if (playerStats != null && !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(breaker)) && playerStats.protectPlacements()) {
            event.setCancelled(true);
            final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "BREAK").with(PlaceHolder.Holder.OBJECT, block.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
            FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", breaker, placeHolders);
            return;
        }
        removeBlockOwner(block);
    }

    /**
     * Prevents players from modifying or interacting with protected blocks (stripping logs, creating paths, tilling dirt, dragon eggs, etc.).
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onModifyBlockOwner(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) return;
        final Player player = event.getPlayer();
        final Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) && block.getType() == Material.DRAGON_EGG) {
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData == null) return;
            if (!ownerData.state.equals(block.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, block.getType().name())) {
                removeBlockOwner(block);
                return;
            }
            final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
            if (playerStats != null && !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(player)) && playerStats.protectPlacements()) {
                event.setCancelled(true);
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "INTERACT").with(PlaceHolder.Holder.OBJECT, block.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", player, placeHolders);
            }
        } else if (action == Action.RIGHT_CLICK_BLOCK && event.getItem() != null) {
            if (isBlockModification(event.getItem().getType().name(), block.getType().name())) {
                final OwnerData ownerData = getBlockOwner(block);
                if (ownerData == null) return;
                if (!ownerData.state.equals(block.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, block.getType().name())) {
                    removeBlockOwner(block);
                    return;
                }
                final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
                if (playerStats != null && !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(player)) && playerStats.protectPlacements()) {
                    event.setCancelled(true);
                    final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "MODIFY").with(PlaceHolder.Holder.OBJECT, block.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                    FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", player, placeHolders);
                }
            }
        }
    }

    /**
     * Handles ownership transfer when blocks change state (falling blocks, enderman pickups).
     * Transfers ownership data between blocks and entities when blocks fall or are picked up.
     *
     * @param event - EntityChangeBlockEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onBlockChange(final EntityChangeBlockEvent event) {
        final Block block = event.getBlock();
        if (event.getEntity() instanceof FallingBlock) {
            final FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            if (event.getTo() == Material.AIR) {
                final OwnerData ownerData = getBlockOwner(block);
                if (ownerData == null) return;
                if (!ownerData.state.equals(block.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, block.getType().name())) {
                    removeBlockOwner(block);
                    return;
                }
                setEntityOwner(fallingBlock, ownerData.playerId, ownerData.state);
                removeBlockOwner(block);
            } else if (block.getType() == Material.AIR) {
                final OwnerData ownerData = getEntityOwner(fallingBlock);
                if (ownerData != null) {
                    setBlockOwner(block, ownerData.playerId, ownerData.state);
                }
            }
        } else if (event.getEntity() instanceof Enderman) {
            final Enderman enderman = (Enderman) event.getEntity();
            if (event.getTo() == Material.AIR) {
                final OwnerData ownerData = getBlockOwner(block);
                if (ownerData == null) return;
                if (!ownerData.state.equals(block.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, block.getType().name())) {
                    removeBlockOwner(block);
                    return;
                }
                final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
                if (playerStats == null || !playerStats.protectPlacements()) {
                    setEntityOwner(enderman, ownerData.playerId, ownerData.state);
                    removeBlockOwner(block);
                    ServerUtils.logDebug("Enderman picked Block owned by " + ownerData.playerId + " at " + block.getLocation().getX() + "x, " + block.getLocation().getY() + "y, " + block.getLocation().getZ() + "z");
                } else {
                    event.setCancelled(true);
                }
            } else {
                final OwnerData ownerData = getEntityOwner(enderman);
                if (ownerData != null) {
                    setBlockOwner(block, ownerData.playerId, ownerData.state);
                    removeEntityOwner(enderman);
                    ServerUtils.logDebug("Enderman placed owned by " + ownerData.playerId + " at " + block.getLocation().getX() + "x, " + block.getLocation().getY() + "y, " + block.getLocation().getZ() + "z");
                }
            }
        } else if (event.getTo() == Material.AIR) {
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData != null) {
                if (!ownerData.state.equals(block.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, block.getType().name())) {
                    removeBlockOwner(block);
                    return;
                }
                final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
                if (playerStats != null && playerStats.protectPlacements()) {
                    event.setCancelled(true);
                    ServerUtils.logDebug("Protected block from " + event.getEntity().getType() + " owned by " + ownerData.playerId);
                } else {
                    removeBlockOwner(block);
                }
            }
        }
    }

    /**
     * Prevents drops when an enderman dies while carrying a protected block.
     *
     * @param event - EntityDeathEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEndermanDeath(final EntityDeathEvent event) {
        if (ServerUtils.hasSpecificUpdate("1_14") && event.getEntity() instanceof Enderman) {
            final Enderman enderman = (Enderman) event.getEntity();
            final OwnerData ownerData = getEntityOwner(enderman);
            if (ownerData != null) {
                final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
                if (playerStats != null && !playerStats.dropPlacements()) {
                    event.getDrops().removeIf(item -> item.getType().isBlock());
                }
                removeEntityOwner(enderman);
                ServerUtils.logDebug("Enderman died while carrying block owned by " + ownerData.playerId);
            }
        } else {
            final Entity entity = event.getEntity();
            final OwnerData ownerData = getEntityOwner(entity);
            if (ownerData == null) return;
            if (!ownerData.state.equals(String.valueOf(entity.getEntityId()))) {
                removeEntityOwner(entity);
                return;
            }
            final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
            if (playerStats != null && !playerStats.dropPlacements()) {
                event.getDrops().clear();
            }
            removeEntityOwner(entity);
        }
    }

    /**
     * Marks hanging entities (paintings, item frames) placed by fake creative players.
     *
     * @param event - HangingPlaceEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlaceHangingOwner(final HangingPlaceEvent event) {
        final Entity entity = event.getEntity();
        if (event.getPlayer() != null && Creative.isCreativeMode(event.getPlayer(), true)) {
            setEntityOwner(entity, PlayerHandler.getPlayerID(event.getPlayer()), String.valueOf(entity.getEntityId()));
        }
    }

    /**
     * Prevents non-entity sources from breaking protected hanging entities.
     *
     * @param event - HangingBreakEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreakHangingOwner(final HangingBreakEvent event) {
        final Entity entity = event.getEntity();
        final OwnerData ownerData = getEntityOwner(entity);
        if (ownerData == null) return;
        if (!ownerData.state.equals(String.valueOf(entity.getEntityId()))) {
            removeEntityOwner(entity);
            return;
        }
        if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) {
            final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
            if (playerStats != null && !playerStats.dropPlacements()) {
                event.setCancelled(true);
                entity.remove();
            }
            if (playerStats != null && playerStats.protectPlacements()) {
                event.setCancelled(true);
                return;
            }
            removeEntityOwner(entity);
        }
    }

    /**
     * Prevents players from breaking protected hanging entities.
     *
     * @param event - HangingBreakByEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBreakEntityOwner(final HangingBreakByEntityEvent event) {
        final Entity remover = event.getRemover();
        final Entity entity = event.getEntity();
        if (remover instanceof Player) {
            final Player player = (Player) remover;
            final OwnerData ownerData = getEntityOwner(entity);
            if (ownerData == null) {
                return;
            }
            if (!ownerData.state.equals(String.valueOf(entity.getEntityId()))) {
                removeEntityOwner(entity);
                return;
            }
            final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
            if (playerStats != null && !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(player)) && playerStats.protectPlacements()) {
                event.setCancelled(true);
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "BREAK").with(PlaceHolder.Holder.OBJECT, entity.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", player, placeHolders);
                return;
            }
            if (playerStats != null && !playerStats.dropPlacements()) {
                event.setCancelled(true);
                entity.remove();
            }
        }
        removeEntityOwner(entity);
    }

    /**
     * Prevents players from interacting with protected entities.
     *
     * @param event - PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntityOwner(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        final OwnerData ownerData = getEntityOwner(entity);
        if (ownerData == null) return;
        if (!ownerData.state.equals(String.valueOf(entity.getEntityId()))) {
            removeEntityOwner(entity);
            return;
        }
        final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
        if (playerStats != null && !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(player)) && playerStats.protectPlacements()) {
            event.setCancelled(true);
            final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "INTERACT").with(PlaceHolder.Holder.OBJECT, entity.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
            FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", player, placeHolders);
        }
    }

    /**
     * Prevents players from interacting with protected armor stands, minecarts, boats, and hanging entities.
     *
     * @param event - PlayerInteractAtEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractAtEntityOwner(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        if (!(entity instanceof ArmorStand || entity instanceof Minecart || entity instanceof Boat || entity instanceof Hanging)) {
            return;
        }
        final OwnerData ownerData = getEntityOwner(entity);
        if (ownerData == null) {
            return;
        }
        if (!ownerData.state.equals(String.valueOf(entity.getEntityId()))) {
            removeEntityOwner(entity);
            return;
        }
        final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
        if (playerStats != null && !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(player)) && playerStats.protectPlacements()) {
            event.setCancelled(true);
            final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "INTERACT").with(PlaceHolder.Holder.OBJECT, entity.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
            FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", player, placeHolders);
        }
    }

    /**
     * Prevents damage to protected entities from players and non-player sources.
     *
     * @param event - EntityDamageByEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamageEntityOwner(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Enderman) return;
        final OwnerData ownerData = getEntityOwner(entity);
        if (ownerData == null) return;
        if (!ownerData.state.equals(String.valueOf(entity.getEntityId()))) {
            removeEntityOwner(entity);
            return;
        }
        final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            if (playerStats != null) {
                if (!Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(player)) && playerStats.protectPlacements()) {
                    event.setCancelled(true);
                    final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "DAMAGE").with(PlaceHolder.Holder.OBJECT, entity.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                    FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", player, placeHolders);
                } else if (entity instanceof ItemFrame && !playerStats.dropPlacements() && ((ItemFrame)entity).getItem().getType() != Material.AIR) {
                    if (!Creative.isCreativeMode(player, true) || Blocks.canBreak(player)) {
                        entity.remove();
                    }
                    event.setCancelled(true);
                }
            }
        } else if (playerStats != null) {
            if (playerStats.protectPlacements()) {
                event.setCancelled(true);
            } else if ((entity instanceof ItemFrame && !playerStats.dropPlacements())) {
                event.setCancelled(true);
                entity.remove();
            }
        }
    }

    /**
     * Prevents destruction of protected vehicles (boats, minecarts).
     *
     * @param event - VehicleDestroyEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDestroyVehicleOwner(final VehicleDestroyEvent event) {
        final Entity attacker = event.getAttacker();
        final Entity entity = event.getVehicle();
        final OwnerData ownerData = getEntityOwner(entity);
        if (ownerData == null) return;
        if (!ownerData.state.equals(String.valueOf(entity.getEntityId()))) {
            removeEntityOwner(entity);
            return;
        }
        final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
        if (playerStats != null && (!(attacker instanceof Player) || !Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID((Player)attacker))) && playerStats.protectPlacements()) {
            event.setCancelled(true);
            if ((attacker instanceof Player)) {
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "BREAK").with(PlaceHolder.Holder.OBJECT, entity.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", attacker, placeHolders);
            }
        }
    }

    /**
     * Prevents pistons from pushing protected blocks and transfers ownership data when blocks are moved.
     *
     * @param event - BlockPistonExtendEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPushBlockOwner(final BlockPistonExtendEvent event) {
        final Block pistonBlock = event.getBlock();
        final OwnerData pistonOwnerData = getBlockOwner(pistonBlock);
        final Set<String> blockOwners = new HashSet<>();
        final Map<Location, OwnerData> dataToMove = new HashMap<>();
        for (final Block block : event.getBlocks()) {
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData != null) {
                blockOwners.add(ownerData.playerId);
                dataToMove.put(block.getLocation().clone(), ownerData);
            }
        }
        if (blockOwners.isEmpty()) return;
        for (final String blockOwnerId : blockOwners) {
            if (pistonOwnerData != null && blockOwnerId.equals(pistonOwnerData.playerId)) continue;
            final PlayerStats playerStats = Creative.getOfflineStats(blockOwnerId);
            if (playerStats != null && playerStats.protectPlacements()) {
                event.setCancelled(true);
                ServerUtils.logDebug("Piston movement blocked as " + blockOwnerId + " has protection enabled");
                return;
            }
        }
        if (!dataToMove.isEmpty()) moveBlockData(dataToMove, event.getDirection());
    }

    /**
     * Prevents pistons from pulling protected blocks and transfers ownership data when blocks are moved.
     *
     * @param event - BlockPistonRetractEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPullBlockOwner(final BlockPistonRetractEvent event) {
        Block pistonBlock = event.getBlock();
        final OwnerData pistonOwnerData = getBlockOwner(pistonBlock);
        final Set<String> blockOwners = new HashSet<>();
        final Map<Location, OwnerData> dataToMove = new HashMap<>();
        for (final Block block : event.getBlocks()) {
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData != null) {
                blockOwners.add(ownerData.playerId);
                dataToMove.put(block.getLocation().clone(), ownerData);
            }
        }
        if (blockOwners.isEmpty()) return;
        for (final String blockOwnerId : blockOwners) {
            if (pistonOwnerData != null && blockOwnerId.equals(pistonOwnerData.playerId)) {
                ServerUtils.logDebug("Skipping protection check both pushed block(s) and piston are owned by the same player");
                continue;
            }
            final PlayerStats playerStats = Creative.getOfflineStats(blockOwnerId);
            if (playerStats != null && playerStats.protectPlacements()) {
                event.setCancelled(true);
                ServerUtils.logDebug("Block movement from piston blocked as " + blockOwnerId + " has protection enabled");
                return;
            }
        }
        if (!dataToMove.isEmpty()) moveBlockData(dataToMove, event.getDirection());
    }

    /**
     * Prevents entity explosions (TNT, Creepers, Withers) from destroying protected fake creative blocks.
     *
     * @param event - EntityExplodeEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onExplodeEntityOwner(final EntityExplodeEvent event) {
        final List<Block> blockDrops = new ArrayList<>();
        event.blockList().removeIf(block -> {
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData != null) {
                final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
                if (playerStats != null && playerStats.protectPlacements()) {
                    return true;
                }
                if (playerStats != null && !playerStats.dropPlacements()) {
                    blockDrops.add(block);
                    return true;
                }
            }
            return false;
        });
        if (!blockDrops.isEmpty()) {
            SchedulerUtils.run(() -> {
                for (final Block block : blockDrops) {
                    block.setType(Material.AIR);
                    removeBlockOwner(block);
                }
            });
        }
    }

    /**
     * Prevents dragon eggs from teleporting when being broken in creative mode.
     *
     * @param event - BlockFromToEvent
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDragonEggTeleport(final BlockFromToEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != Material.DRAGON_EGG) return;
        for (Player player : block.getWorld().getPlayers()) {
            if (!Creative.isCreativeMode(player, true)) continue;
            if (player.getLocation().distance(block.getLocation()) > 6) continue;
            final String playerId = PlayerHandler.getPlayerID(player);
            final Blocks.InteractionData interaction = Blocks.lastInteraction.get(playerId);
            if (interaction != null && interaction.action.name().contains("LEFT_CLICK")) {
                event.setCancelled(true);
                return;
            }
        }
        final OwnerData ownerData = getBlockOwner(block);
        if (ownerData != null) {
            final Block toBlock = event.getToBlock();
            removeBlockOwner(block);
            SchedulerUtils.run(() -> {
                setBlockOwner(toBlock, ownerData.playerId, ownerData.state);
                ServerUtils.logDebug("Dragon egg owned by " + ownerData.playerId + " teleported");
            });
        }
    }

    /**
     * Listeners for placed block protection.
     * Automatically registered when the parent Placement class is instantiated on 1.20+ servers.
     *
     * @since 1.20
     */
    private static class Placement_1_20 implements Listener {

        /**
         * Prevents protected TNT from being primed and transfers ownership when allowed.
         *
         * @param event - TNTPrimeEvent
         */
        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onPrimeBlockOwner(final TNTPrimeEvent event) {
            final Block block = event.getBlock();
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData == null) return;
            final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
            if (event.getPrimingEntity() instanceof Player) {
                final Player primer = (Player) event.getPrimingEntity();
                if (playerStats != null && !ownerData.playerId.equals(PlayerHandler.getPlayerID(primer)) && playerStats.protectPlacements()) {
                    event.setCancelled(true);
                    final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "IGNITE").with(PlaceHolder.Holder.OBJECT, block.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                    FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", primer, placeHolders);
                }
            } else if (playerStats != null && playerStats.protectPlacements()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Listeners for placed block protection.
     * Automatically registered when the parent Placement class is instantiated on 1.14+ servers.
     *
     * @since 1.14
     */
    private static class Placement_1_14 implements Listener {

        private static final NamespacedKey ownerKey = new NamespacedKey(FakeCreative.getCore().getPlugin(), "fc_owner");
        private static final NamespacedKey stateKey = new NamespacedKey(FakeCreative.getCore().getPlugin(), "fc_state");

        /**
         * Marks entities placed by fake creative players with ownership data.
         *
         * @param event - EntityPlaceEvent
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlaceEntityOwner(final EntityPlaceEvent event) {
            final Entity entity = event.getEntity();
            if (event.getPlayer() != null && Creative.isCreativeMode(event.getPlayer(), true)) {
                setEntityOwner(entity, PlayerHandler.getPlayerID(event.getPlayer()), String.valueOf(entity.getEntityId()));
            }
        }

        /**
         * Creates a NamespacedKey for storing block data in chunk PDC.
         * Uses {@link #getBlockKey(Block)} to generate a unique identifier.
         *
         * @param block - The block to create a key for
         * @return NamespacedKey for use with PersistentDataContainer
         */
        private static NamespacedKey getChunkBlockKey(final Block block) {
            return new NamespacedKey(FakeCreative.getCore().getPlugin(), getBlockKey(block));
        }
    }

    /**
     * Listeners for placed block protection.
     * Automatically registered when the parent Placement class is instantiated on 1.13+ servers.
     *
     * @since 1.13
     */
    private static class Placement_1_13 implements Listener {

        /**
         * Prevents bone meal usage on protected blocks (crops, saplings, etc.).
         *
         * @param event - BlockFertilizeEvent
         */
        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onFertilizeBlockOwner(final BlockFertilizeEvent event) {
            final Block block = event.getBlock();
            final OwnerData ownerData = getBlockOwner(block);
            if (ownerData == null || event.getPlayer() == null) return;
            if (!ownerData.state.equals(block.getType().name()) && !TransformBlocks.isTransformation(ownerData.state, block.getType().name())) {
                removeBlockOwner(block);
                return;
            }
            final Player player = event.getPlayer();
            if (Objects.equals(ownerData.playerId, PlayerHandler.getPlayerID(player))) return;
            final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
            if (playerStats != null && playerStats.protectPlacements()) {
                event.setCancelled(true);
                final PlaceHolder placeHolders = new PlaceHolder().with(PlaceHolder.Holder.ACTION, "FERTILIZE").with(PlaceHolder.Holder.OBJECT, block.getType().name()).with(PlaceHolder.Holder.OWNER, playerStats.getPlayerName());
                FakeCreative.getCore().getLang().sendLangMessage("general.protectionDenied", player, placeHolders);
            }
        }
    }

    /**
     * Listeners for placed block protection.
     * Automatically registered when the parent Placement class is instantiated on 1.8.7+ servers.
     *
     * @since 1.8.7
     */
    private static class Placement_1_8_7 implements Listener {

        /**
         * Prevents block explosions from destroying protected fake creative blocks.
         *
         * @param event - BlockExplodeEvent
         */
        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        private void onExplodeBlockOwner(final BlockExplodeEvent event) {
            final List<Block> blockDrops = new ArrayList<>();
            event.blockList().removeIf(block -> {
                final OwnerData ownerData = getBlockOwner(block);
                if (ownerData != null) {
                    final PlayerStats playerStats = Creative.getOfflineStats(ownerData.playerId);
                    if (playerStats != null && playerStats.protectPlacements()) {
                        return true;
                    }
                    if (playerStats != null && !playerStats.dropPlacements()) {
                        blockDrops.add(block);
                        return true;
                    }
                }
                return false;
            });
            if (!blockDrops.isEmpty()) {
                SchedulerUtils.run(() -> {
                    for (final Block block : blockDrops) {
                        block.setType(Material.AIR);
                        removeBlockOwner(block);
                    }
                });
            }
        }
    }

    /**
     * Checks if an item can modify a block (stripping, pathing, tilling, waxing, etc.).
     *
     * @param itemType - The item type name
     * @param blockType - The block type name
     * @return True if this is a modifying action
     */
    private boolean isBlockModification(final String itemType, final String blockType) {
        if (itemType.contains("_AXE") && (blockType.contains("_LOG") || blockType.contains("_WOOD") || blockType.contains("_STEM") || blockType.contains("_HYPHAE") || blockType.contains("COPPER"))) {
            return true;
        } else if (itemType.contains("_SHOVEL") && (blockType.equals("GRASS_BLOCK") || blockType.equals("DIRT"))) {
            return true;
        } else if (itemType.contains("_HOE") && (blockType.equals("GRASS_BLOCK") || blockType.equals("DIRT") || blockType.equals("COARSE_DIRT"))) {
            return true;
        } else return itemType.equals("HONEYCOMB") && blockType.contains("COPPER") && !blockType.contains("WAXED");
    }

    /**
     * Marks a block with owner Id and material type.
     * Uses TileState PDC for tile entities, Chunk PDC for regular blocks.
     * If PDC is unavailable, data is stored in the database.
     *
     * @param block - The block to mark
     * @param playerId - The id of the player who placed it
     * @param blockState - The material name of the block
     */
    private void setBlockOwner(final Block block, final String playerId, final String blockState) {
        if (ServerUtils.hasSpecificUpdate("1_17")) {
            final BlockState state = block.getState();
            if (state instanceof TileState) {
                final PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();
                container.set(Placement_1_14.ownerKey, PersistentDataType.STRING, playerId);
                container.set(Placement_1_14.stateKey, PersistentDataType.STRING, blockState);
                state.update();
                ServerUtils.logDebug("Saved placed Tile " + block.getType() + " as owning player " + playerId);
            } else {
                final PersistentDataContainer container = block.getChunk().getPersistentDataContainer();
                final String data = playerId + "|" + blockState;
                container.set(Placement_1_14.getChunkBlockKey(block), PersistentDataType.STRING, data);
                ServerUtils.logDebug("Saved placed Block " + block.getType() + " as owning player " + playerId);
            }
        } else {
            FakeCreative.getCore().getSQL().removeData(new DataObject(DataObject.Table.OWNERSHIP_DATA, getBlockKey(block), "", "", ""));
            FakeCreative.getCore().getSQL().saveData(new DataObject(DataObject.Table.OWNERSHIP_DATA, getBlockKey(block), playerId, blockState, ""));
            ServerUtils.logDebug("Saved placed Block " + block.getType() + " as owning player " + playerId);
        }
    }

    /**
     * Gets the owner data of a block.
     *
     * @param block - The block to check
     * @return OwnerData containing playerId and material, or null if not owned
     */
    private static OwnerData getBlockOwner(final Block block) {
        if (ServerUtils.hasSpecificUpdate("1_17")) {
            final BlockState state = block.getState();
            if (block.getType() != Material.MOVING_PISTON && state instanceof TileState) {
                final PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();
                final String id = container.get(Placement_1_14.ownerKey, PersistentDataType.STRING);
                final String blockState = container.get(Placement_1_14.stateKey, PersistentDataType.STRING);
                if (id != null && blockState != null) {
                    try {
                        ServerUtils.logDebug("Got placed Tile " + block.getType() + " with owning player " + id);
                        return new OwnerData(id, blockState);
                    } catch (IllegalArgumentException ignored) { }
                }
            } else {
                final PersistentDataContainer container = block.getChunk().getPersistentDataContainer();
                final String data = container.get(Placement_1_14.getChunkBlockKey(block), PersistentDataType.STRING);
                if (data != null && data.contains("|")) {
                    try {
                        final String[] parts = data.split("\\|", 2);
                        ServerUtils.logDebug("Got placed Block " + block.getType() + " with owning player " + parts[0]);
                        return new OwnerData(parts[0], parts[1]);
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            return null;
        } else {
            final DataObject dataObject = (DataObject) FakeCreative.getCore().getSQL().getData(new DataObject(DataObject.Table.OWNERSHIP_DATA, getBlockKey(block), "", "", ""));
            if (dataObject != null) {
                ServerUtils.logDebug("Got placed Block " + block.getType() + " with owning player " + dataObject.getPlayerId());
                return new OwnerData(dataObject.getPlayerId(), dataObject.getState());
            }
            return null;
        }
    }

    /**
     * Removes ownership data from a block.
     *
     * @param block - The block to remove ownership from
     */
    private static void removeBlockOwner(final Block block) {
        if (ServerUtils.hasSpecificUpdate("1_17")) {
            final BlockState state = block.getState();
            if (state instanceof TileState) {
                final PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();
                container.remove(Placement_1_14.ownerKey);
                container.remove(Placement_1_14.stateKey);
                state.update();
            } else {
                final PersistentDataContainer container = block.getChunk().getPersistentDataContainer();
                container.remove(Placement_1_14.getChunkBlockKey(block));
            }
        } else {
            FakeCreative.getCore().getSQL().removeData(new DataObject(DataObject.Table.OWNERSHIP_DATA, getBlockKey(block), "", "", ""));
        }
    }

    /**
     * Marks an entity with owner Id and entity ID.
     *
     * @param entity - The entity to mark
     * @param playerId - The Id of the player who placed it
     * @param entityState - The entity ID as a string
     */
    private static void setEntityOwner(final Entity entity, final String playerId, final String entityState) {
        if (ServerUtils.hasSpecificUpdate("1_14")) {
            final PersistentDataContainer container = entity.getPersistentDataContainer();
            container.set(Placement_1_14.ownerKey, PersistentDataType.STRING, playerId);
            container.set(Placement_1_14.stateKey, PersistentDataType.STRING, entityState);
            ServerUtils.logDebug("Saved Entity " + entity.getType() + " as owning player " + playerId);
        } else {
            FakeCreative.getCore().getSQL().removeData(new DataObject(DataObject.Table.OWNERSHIP_DATA, entity.getType().name() + "_" + entity.getUniqueId(), "", "", ""));
            FakeCreative.getCore().getSQL().saveData(new DataObject(DataObject.Table.OWNERSHIP_DATA, entity.getType().name() + "_" + entity.getUniqueId(), playerId, entityState, ""));
        }
    }

    /**
     * Gets the owner data of an entity.
     *
     * @param entity - The entity to check
     * @return OwnerData containing playerId and entity ID, or null if not owned
     */
    public static OwnerData getEntityOwner(final Entity entity) {
        if (ServerUtils.hasSpecificUpdate("1_14")) {
            final PersistentDataContainer container = entity.getPersistentDataContainer();
            final String id = container.get(Placement_1_14.ownerKey, PersistentDataType.STRING);
            final String state = container.get(Placement_1_14.stateKey, PersistentDataType.STRING);
            if (id != null && state != null) {
                try {
                    ServerUtils.logDebug("Got Entity " + entity.getType() + " with owning player " + id);
                    return new OwnerData(id, state);
                } catch (IllegalArgumentException ignored) { }
            }
            return null;
        } else {
            final DataObject dataObject = (DataObject) FakeCreative.getCore().getSQL().getData(new DataObject(DataObject.Table.OWNERSHIP_DATA, entity.getType().name() + "_" + entity.getUniqueId(), "", "", ""));
            if (dataObject != null) {
                ServerUtils.logDebug("Got Entity " + entity.getType() + " with owning player " + dataObject.getPlayerId());
                return new OwnerData(dataObject.getPlayerId(), dataObject.getState());
            }
            return null;
        }
    }

    /**
     * Removes ownership data from an entity.
     *
     * @param entity - The entity to remove ownership from
     */
    private void removeEntityOwner(final Entity entity) {
        if (ServerUtils.hasSpecificUpdate("1_14")) {
            final PersistentDataContainer container = entity.getPersistentDataContainer();
            container.remove(Placement_1_14.ownerKey);
            container.remove(Placement_1_14.stateKey);
        } else {
            FakeCreative.getCore().getSQL().removeData(new DataObject(DataObject.Table.OWNERSHIP_DATA, entity.getType().name() + "_" + entity.getUniqueId(), "", "", ""));
        }
    }

    /**
     * Moves ownership data when blocks are pushed/pulled in a set direction.
     * Processes blocks in correct order to prevent data loss.
     *
     * @param oldData - Map of original locations to ownership data
     * @param direction - The direction blocks are moving
     */
    private void moveBlockData(final Map<Location, OwnerData> oldData, final BlockFace direction) {
        ServerUtils.logDebug("Attempting to move " + oldData.size() + " owner data entries resulting from piston movement");
        final List<Map.Entry<Location, OwnerData>> sortedEntries = new ArrayList<>(oldData.entrySet());
        sortedEntries.sort((e1, e2) -> {
            final Location loc1 = e1.getKey();
            final Location loc2 = e2.getKey();
            final double dist1 = loc1.getX() * direction.getModX() + loc1.getY() * direction.getModY() + loc1.getZ() * direction.getModZ();
            final double dist2 = loc2.getX() * direction.getModX() + loc2.getY() * direction.getModY() + loc2.getZ() * direction.getModZ();
            return Double.compare(dist2, dist1);
        });
        for (final Map.Entry<Location, OwnerData> entry : sortedEntries) {
            final OwnerData ownerData = entry.getValue();
            final Location oldLocation = entry.getKey();
            final Location newLocation = oldLocation.clone().add(direction.getModX(), direction.getModY(), direction.getModZ());
            final Block oldBlock = oldLocation.getBlock();
            final Block newBlock = newLocation.getBlock();
            removeBlockOwner(oldBlock);
            setBlockOwner(newBlock, ownerData.playerId, ownerData.state);
            ServerUtils.logDebug("Piston moved a Block owned by " + ownerData.playerId + " from " + oldLocation.getBlockX() + "x," + oldLocation.getBlockY() + "y," + oldLocation.getBlockZ() + "z to " + newLocation.getBlockX() + "x," + newLocation.getBlockY() + "y," + newLocation.getBlockZ() + "z");
        }
    }

    /**
     * Creates a unique string key for a block's location.
     * Converts negative coordinates to safe format (replaces - with m for minus).
     * For 1.14+, uses short format. For older versions, includes world UID.
     *
     * @param block - The block to create a key for
     * @return String key in format "fc_x_y_z" or "worldUID_x_y_z"
     */
    private static String getBlockKey(final Block block) {
        final String x = String.valueOf(block.getX()).replace("-", "m");
        final String y = String.valueOf(block.getY()).replace("-", "m");
        final String z = String.valueOf(block.getZ()).replace("-", "m");
        return (ServerUtils.hasSpecificUpdate("1_14") ? "fc_" : block.getWorld().getUID() + "_") + x + "_" + y + "_" + z;
    }

    /**
     * Simple data class to hold block/entity owner information.
     */
    public static class OwnerData {
        final String playerId;
        final String state;
        OwnerData(final String playerId, final String state) {
            this.playerId = playerId;
            this.state = state;
        }
    }
}