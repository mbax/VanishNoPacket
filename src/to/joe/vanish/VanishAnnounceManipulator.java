package to.joe.vanish;

import java.util.ArrayList;

public class VanishAnnounceManipulator {
    private ArrayList<String> delayedAnnounce;
    private final Object syncLogin = new Object();
    private final VanishPlugin plugin;
    private final String fakeJoin;
    private final String fakeQuit;

    public VanishAnnounceManipulator(VanishPlugin plugin, String fakeJoin, String fakeQuit) {
        this.plugin = plugin;
        this.fakeJoin = fakeJoin;
        this.fakeQuit = fakeQuit;
        synchronized (this.syncLogin) {
            this.delayedAnnounce = new ArrayList<String>();
        }
    }

    public void addToDelayedAnnounce(String player) {
        synchronized (this.syncLogin) {
            this.delayedAnnounce.add(player);
        }
    }

    public boolean delayedAnnounce(String player) {
        return this.delayedAnnounce.contains(player);
    }

    public void fakeJoin(String player) {
        this.plugin.getServer().broadcastMessage(this.fakeJoin.replace("%p", player));
    }

    public void fakeQuit(String player) {
        this.plugin.getServer().broadcastMessage(this.fakeQuit.replace("%p", player));
    }

    public void toggled(String player) {
        if (!this.delayedAnnounce.contains(player)) {
            return;
        }
        final String messageJoin = this.fakeJoin.replace("%p", player);
        if (messageJoin != null) {
            this.plugin.getServer().broadcastMessage(messageJoin);
        }
        synchronized (this.syncLogin) {
            this.delayedAnnounce.remove(player);
        }
    }
}
