package org.orchidmc.queueup;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.orchidmc.queueup.model.QueUpSong;

import static org.orchidmc.queueup.util.MessageUtil.formatSongMessage;

public class QueueUpPlugin extends JavaPlugin {
    private Thread mainThread;

    private BukkitAudiences adventure;
    private String lastSongName = "";
    static boolean debugLogging = false;

    @Override
    public void onEnable() {
        debugLogging = false;
        adventure = BukkitAudiences.create(this);

        this.getConfig().addDefault("url", "https://www.queup.net/join/orchid");
        this.getConfig().addDefault("room-id", "689386756ad9fd0007f2ad79");
        this.getConfig().addDefault("poll-interval", 60);
        this.getConfig().addDefault("song-msg", "<gray>We are listening to <yellow>$songName</yellow> (queued by <yellow>$username</yellow>) <u><yellow><click:open_url:'$url'>Come join us!</click></yellow></u>");
        this.getConfig().addDefault("no-song-msg", "<red>No song is currently playing.");
        this.getConfig().options().copyDefaults(true);
        saveConfig();

        mainThread = new Thread(() -> {
            try {
                QueUpSong song = QueUpFetcher.fetchCurrentSong();
                if (song != null && !song.songName.equalsIgnoreCase(lastSongName)) {
                    lastSongName = song.songName;
                    Component message = formatSongMessage(song);
                    adventure.all().sendMessage(message);
                }
                Thread.sleep(this.getConfig().getInt("poll-interval", 60) * 1000L);
            } catch (Exception e) { return; }
        });
        mainThread.start();
    }

    @Override
    public void onDisable() {
        mainThread.interrupt();
        if (adventure != null) adventure.close();
    }
}
