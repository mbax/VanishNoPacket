package org.kitteh.vanish.listeners;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;
import net.minecraft.server.Packet41MobEffect;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.kitteh.vanish.Messages;
import org.kitteh.vanish.Settings;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.metrics.MetricsOverlord;

public class ListenPlayerOther implements Listener {

    private final VanishPlugin plugin;

    public ListenPlayerOther(VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (this.plugin.getManager().isVanished(player) && VanishPerms.canNotHunger(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType() == Material.CHEST)) {
            if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canReadChestsSilently(event.getPlayer())) {
                event.setCancelled(true);
                final Chest chest = (Chest) event.getClickedBlock().getState();
                final Inventory i = this.plugin.getServer().createInventory(event.getPlayer(), chest.getInventory().getSize());
                i.setContents(chest.getInventory().getContents());
                event.getPlayer().openInventory(i);
                this.plugin.chestFakeOpen(event.getPlayer().getName());
                event.getPlayer().sendMessage(ChatColor.AQUA + "[VNP]" + Messages.getString("ListenPlayerOther.OpeningChestSilently"));
                return;
            }
        }
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock().getType() == Material.SOIL)) {
            if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotTrample(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotPickUp(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (this.plugin.getManager().isVanished(player)) {
            this.plugin.messageStatusUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + Messages.getString("ListenPlayerOther.QuitVanished"));
        }
        this.plugin.getManager().playerQuit(player);
        this.plugin.hooksQuit(player);
        this.plugin.getManager().getAnnounceManipulator().dropDelayedAnnounce(player.getName());
        if (!this.plugin.getManager().getAnnounceManipulator().wasPlayerMarkedOnline(player.getName()) || VanishPerms.silentQuit(player)) {
            MetricsOverlord.quitinvis.increment();
            event.setQuitMessage(null);
        }
        this.plugin.chestFakeClose(event.getPlayer().getName());
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (Settings.getWorldChangeCheck()) {
            this.plugin.getManager().playerRefresh(event.getPlayer());
        }
        if (this.plugin.getManager().isVanished(event.getPlayer())) {
            final CraftPlayer cplr = ((CraftPlayer) event.getPlayer());
            cplr.getHandle().netServerHandler.sendPacket(new Packet41MobEffect(cplr.getEntityId(), new MobEffect(MobEffectList.INVISIBILITY.getId(), 0, 0)));
        }
    }
}
