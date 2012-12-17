package org.kitteh.vanish.compat;


public class NMSManager {

    private static NMSCallProvider provider;

    public static NMSCallProvider getProvider() {
        if (provider == null) {
            provider = needProviderPlox();
        }
        return provider;
    }

    private static NMSCallProvider needProviderPlox() {
        try {
            Class.forName("net.minecraft.server.Packet");
            return new org.kitteh.vanish.compat.pre.NMSHandler();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("net.minecraft.server.v1_4_5.Packet");
            return new org.kitteh.vanish.compat.v1_4_5.NMSHandler();
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}
