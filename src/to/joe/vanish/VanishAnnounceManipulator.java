package to.joe.vanish;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VanishAnnounceManipulator {
    private ArrayList<String> delayedAnnounce;
    private final Object syncLogin = new Object();
    private final VanishPlugin plugin;
    private final String fakeJoin;
    private final String fakeQuit;
    private final boolean delayedJoinTracking;
    private final HashMap<String, Boolean> status;

    public VanishAnnounceManipulator(VanishPlugin plugin, String fakeJoin, String fakeQuit, boolean delayedJoinTracking) {
        this.plugin = plugin;
        this.fakeJoin = fakeJoin.replace("&&", "§");
        this.fakeQuit = fakeQuit.replace("&&", "§");
        this.delayedJoinTracking = delayedJoinTracking;
        this.status = new HashMap<String, Boolean>();
        synchronized (this.syncLogin) {
            this.delayedAnnounce = new ArrayList<String>();
        }
    }

    public void addToDelayedAnnounce(String player) {
        this.status.put(player, false);
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

    public void fakeJoin(Player player) {
        if (this.status.containsKey(player) && this.status.get(player)) {
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.fakeJoin.replace("%p", player.getName()).replace("%d",player.getDisplayName()));
        this.plugin.log(player.getName() + " faked joining");
        this.status.put(player.getName(), true);
    }

    public void fakeQuit(Player player) {
        if (this.status.containsKey(player) && !this.status.get(player.getName())) {
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.fakeQuit.replace("%p", player.getName()).replace("%d",player.getDisplayName()));
        this.plugin.log(player.getName() + " faked quitting");
        this.status.put(player.getName(), false);
    }

    public void toggled(Player player) {
        if (!this.delayedJoinTracking || !this.delayedAnnounce.contains(player)) {
            return;
        }
        this.fakeJoin(player);
        this.delAnnounce(player.getName());
    }

    public boolean wasPlayerMarkedOnline(String player) {
        if (this.status.containsKey(player)) {
            return this.status.remove(player);
        }
        return true;
    }

    private void delAnnounce(String player) {
        synchronized (this.syncLogin) {
            this.delayedAnnounce.remove(player);
        }
    }
}
