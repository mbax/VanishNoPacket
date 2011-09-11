package to.joe.vanish;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class VanishAnnounceManipulator {
    private ArrayList<String> delayedAnnounce;
    private final Object syncLogin = new Object();
    private final VanishPlugin plugin;
    private final String fakeJoin;
    private final String fakeQuit;
    private final boolean delayedJoinTracking;

    public VanishAnnounceManipulator(VanishPlugin plugin, String fakeJoin, String fakeQuit, boolean delayedJoinTracking) {
        this.plugin = plugin;
        this.fakeJoin = fakeJoin;
        this.fakeQuit = fakeQuit;
        this.delayedJoinTracking = delayedJoinTracking;

        synchronized (this.syncLogin) {
            this.delayedAnnounce = new ArrayList<String>();
        }
    }

    public void addToDelayedAnnounce(String player) {
        if (!this.delayedJoinTracking) {
            return;
        }
        synchronized (this.syncLogin) {
            this.delayedAnnounce.add(player);
        }
    }

    public boolean delayedAnnounceKill(String player) {
        if (this.delayedAnnounce.contains(player)) {
            this.delAnnounce(player);
            return true;
        }
        return false;
    }

    public void fakeJoin(String player) {
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.fakeJoin.replace("%p", player));
        this.plugin.log(player + " faked joining");
    }

    public void fakeQuit(String player) {
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.fakeQuit.replace("%p", player));
        this.plugin.log(player + " faked quitting");
    }

    public void toggled(String player) {
        if (!this.delayedJoinTracking || !this.delayedAnnounce.contains(player)) {
            return;
        }
        this.fakeJoin(player);
        this.delAnnounce(player);
    }

    private void delAnnounce(String player) {
        synchronized (this.syncLogin) {
            this.delayedAnnounce.remove(player);
        }
    }
}
