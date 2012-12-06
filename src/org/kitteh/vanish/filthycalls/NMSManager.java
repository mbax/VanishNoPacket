package org.kitteh.vanish.filthycalls;

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
            return new NMS144();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("net.minecraft.server.v1_4_5.Packet");
            return new NMS145();
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}
