package org.kitteh.vanish.hooks.protocollib;
/*
import java.util.HashMap;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;

public class AntiInvisibilityPotion extends Hook {

    public AntiInvisibilityPotion(VanishPlugin plugin) {
        super(plugin);
    }
    
    private static final byte INVIS = (byte)(PotionEffectType.INVISIBILITY.getId() & 0xFF);
    
    @Override
    public void onVanish(Player player){
        EntityPlayer ep = ((CraftPlayer)player).getHandle();
        System.out.println("v event");
        for(Player plr: plugin.getServer().getOnlinePlayers()){
            System.out.println("");
            if(plr.equals(player)){
                continue;
            }
            System.out.println("v check "+plr.getName());
            for(PotionEffect effect : plr.getActivePotionEffects()){
                System.out.println(effect.getType());
                if(effect.getType().equals(PotionEffectType.INVISIBILITY)){
                    System.out.println("found invis");
                    Packet42RemoveMobEffect packet = new Packet42RemoveMobEffect();
                    packet.a = player.getEntityId();
                    packet.b = INVIS;
                    ep.netServerHandler.sendPacket(packet);
                    break;
                }
            }
        }
    }
    
    
    @Override
    public void onEnable() {
        for(Player player : plugin.getServer().getOnlinePlayers()){
            onJoin(player);
        }
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("ProtocolLib");
        if (grab != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGHEST, GamePhase.PLAYING, Packets.Server.MOB_EFFECT) {

                @Override
                public void onPacketSending(PacketEvent event) {
                    System.out.println("event");
                    Player p = event.getPlayer();
                    if(!AntiInvisibilityPotion.this.plugin.getManager().isVanished(p)){
                        System.out.println(p.getName()+"not invis");
                        return;
                    }
                    try {
                        System.out.println("checking...");
                        byte effect = event.getPacket().getSpecificModifier(byte.class).read(1);
                        if(effect == INVIS ) {
                            System.out.println("Is invis packet");
                            event.setCancelled(true);
                        }
                    } catch (final FieldAccessException e) {
                    }
                }
            });
        } else {
            return;
        }
    }

}*/