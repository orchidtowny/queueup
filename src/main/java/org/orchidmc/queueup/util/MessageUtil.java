package org.orchidmc.queueup.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.orchidmc.queueup.QueueUpPlugin;
import org.orchidmc.queueup.model.QueUpSong;

public class MessageUtil {
    public static Component formatSongMessage(QueUpSong song) {
        String url = QueueUpPlugin.getPlugin(QueueUpPlugin.class).getConfig().getString("url", "");
        String noSongMsg = QueueUpPlugin.getPlugin(QueueUpPlugin.class).getConfig().getString("no-song-msg", "");
        String songMsg = QueueUpPlugin.getPlugin(QueueUpPlugin.class).getConfig().getString("song-msg", "");

        if (song == null) return MiniMessage.miniMessage().deserialize(noSongMsg); // song.songName, song.username

        String message = songMsg
                .replace("$songName", song.songName)
                .replace("$username", song.username)
                .replace("$url", url);
        return MiniMessage.miniMessage().deserialize(message);
    }
}
