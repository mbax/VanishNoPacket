package org.kitteh.vanish.hooks;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;

public class ProtocolLibHook extends Hook {

    public ProtocolLibHook(VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("ProtocolLib");
        if (grab != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGHEST, GamePhase.LOGIN, Packets.Server.KICK_DISCONNECT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    try {
                        final StructureModifier<String> stringModifier = event.getPacket().getSpecificModifier(String.class);
                        final String[] replyString = stringModifier.read(0).split(ChatColor.COLOR_CHAR + "");
                        if (replyString.length == 3) {
                            int online;
                            try {
                                online = Integer.parseInt(replyString[1]);
                            } catch (final NumberFormatException e) {
                                return;
                            }
                            online -= ProtocolLibHook.this.plugin.getManager().numVanished();
                            stringModifier.write(0, replyString[0] + ChatColor.COLOR_CHAR + online + ChatColor.COLOR_CHAR + replyString[2]);
                        }
                    } catch (final FieldAccessException e) {
                    }
                }
            });
        } else {
            return;
        }
    }

}