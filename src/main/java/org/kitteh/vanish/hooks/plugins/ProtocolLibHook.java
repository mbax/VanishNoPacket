package org.kitteh.vanish.hooks.plugins;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;

public final class ProtocolLibHook extends Hook {
    public ProtocolLibHook(VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("ProtocolLib");
        if (grab != null) {
        	ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.KICK_DISCONNECT) {
            //ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGHEST, GamePhase.LOGIN, PacketType.Play.Server.KICK_DISCONNECT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    try {
                        final StructureModifier<String> stringModifier = event.getPacket().getSpecificModifier(String.class);
                        final String replyString = stringModifier.read(0);
                        int offset = 0;
                        String splitter = String.valueOf(ChatColor.COLOR_CHAR); // 1.3 and earlier
                        if (replyString.startsWith(splitter)) { // 1.4 and onward
                            splitter = "\u0000";
                            offset = 3;
                        }
                        final String[] split = replyString.split(splitter);
                        if (split.length == (3 + offset)) {
                            int online;
                            try {
                                online = Integer.parseInt(split[1 + offset]);
                            } catch (final NumberFormatException e) {
                                return;
                            }
                            online -= ProtocolLibHook.this.plugin.getManager().numVanished();
                            final StringBuilder builder = new StringBuilder();
                            for (int x = 0; x < split.length; x++) {
                                if (builder.length() > 0) {
                                    builder.append(splitter);
                                }
                                if (x == (1 + offset)) {
                                    builder.append(online);
                                    continue;
                                }
                                builder.append(split[x]);
                            }
                            stringModifier.write(0, builder.toString());
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