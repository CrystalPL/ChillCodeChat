package pl.chillcode.chillcodechat.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.chillcode.chillcodechat.user.User;

import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PlayerQuitListener implements Listener {
    Provider provider;
    SlowModeCache slowModeCache;
    JavaPlugin plugin;

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final UUID uniqueId = event.getPlayer().getUniqueId();
            final User user = slowModeCache.removeUser(uniqueId);
            provider.saveUser(uniqueId, user);
        });
    }
}
