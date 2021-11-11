package pl.chillcode.chillcodechat.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.chillcode.chillcodechat.config.Config;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class AsyncPlayerChatListener implements Listener {
    Config config;
    SlowModeCache slowModeCache;

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {

    }
}
