package org.orchidmc.queueup;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.orchidmc.queueup.model.ActiveSong;
import org.orchidmc.queueup.model.QueUpSong;
import org.orchidmc.queueup.model.User;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.orchidmc.queueup.QueueUpPlugin.debugLogging;
import static org.orchidmc.queueup.util.HtmlUtil.unescapeHtml;

public class QueUpFetcher {

    private static final String BASE_URL = "https://api.queup.net/";
    private static final String ROOM_ID = QueueUpPlugin.getPlugin(QueueUpPlugin.class).getConfig().getString("room-id");
    private static final String ACTIVE_SONG_PATH = "room/" + ROOM_ID + "/playlist/active";
    private static final String USER_PATH = "user/";
    private static final Gson gson = new Gson();

    public static QueUpSong fetchCurrentSong() {
        try {
            URL songUrl = new URL(BASE_URL + ACTIVE_SONG_PATH);
            HttpURLConnection songConn = (HttpURLConnection) songUrl.openConnection();
            songConn.setRequestMethod("GET");

            int songResponse = songConn.getResponseCode();
            if (songResponse != 200) {
                if (debugLogging) Bukkit.getLogger().info("No active song (HTTP " + songResponse + ")");
                return null;
            }

            ActiveSong activeSong = gson.fromJson(new InputStreamReader(songConn.getInputStream()), ActiveSong.class);
            if (activeSong == null || activeSong.data == null || activeSong.data.song == null || activeSong.data.songInfo == null) return null;

            String songName = unescapeHtml(activeSong.data.songInfo.name);
            String userId = activeSong.data.song.userId;

            URL userUrl = new URL(BASE_URL + USER_PATH + userId);
            HttpURLConnection userConn = (HttpURLConnection) userUrl.openConnection();
            userConn.setRequestMethod("GET");

            int userResponse = userConn.getResponseCode();
            if (userResponse != 200) {
                if (debugLogging) Bukkit.getLogger().info("Could not fetch user info (HTTP " + userResponse + ")");
                return null;
            }

            User user = gson.fromJson(new InputStreamReader(userConn.getInputStream()), User.class);
            String username = unescapeHtml(user.data.username);

            return new QueUpSong(songName, username);

        } catch (FileNotFoundException e) {
            if (debugLogging) Bukkit.getLogger().info("Endpoint returned 404 – likely no song playing.");
            return null;
        } catch (Exception e) {
            Bukkit.getLogger().warning("QueUp fetch error: " + e.getMessage());
            return null;
        }
    }
}
