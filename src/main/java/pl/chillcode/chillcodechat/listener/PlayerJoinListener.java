package pl.chillcode.chillcodechat.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.chillcode.chillcodechat.user.User;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PlayerJoinListener implements Listener {
    SlowModeCache slowModeCache;
    Provider provider;
    JavaPlugin plugin;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final Player player = event.getPlayer();
            final Optional<User> userOptional = provider.getUser(player);
            final User user;
            if (!userOptional.isPresent()) {
                user = new User();
                provider.createUser(player);
            } else {
                user = userOptional.get();
            }

            slowModeCache.createUser(player, user);
        });

    }
}
