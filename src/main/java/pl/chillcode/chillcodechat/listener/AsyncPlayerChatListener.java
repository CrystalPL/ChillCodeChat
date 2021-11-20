package pl.chillcode.chillcodechat.listener;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.chillcode.chillcodechat.config.Config;
import pl.chillcode.chillcodechat.hook.VaultHook;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.user.User;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class AsyncPlayerChatListener implements Listener {
    Config config;
    SlowModeCache slowModeCache;
    MessageAPI messageAPI;

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final User user = slowModeCache.getUser(player.getUniqueId());

        if (user.getBreakStone() < config.getMinimalStoneBreak() && !player.hasPermission("chillcode.chat.stone.bypass")) {
            messageAPI.sendMessage("noEnoughStone", player, ImmutableMap.of("{MINIMAL_STONE}", config.getMinimalStoneBreak(), "{ACTUAL_STONE}", user.getBreakStone()));
            event.setCancelled(true);
            return;
        }

        if (player.hasPermission("chillcode.chat.slowmode.bypass")) {
            return;
        }

        int slowDownTime = config.getServerSlowMode();
        if (user.getSlowDownTime() != 0) {
            slowDownTime = user.getSlowDownTime();
        } else if (VaultHook.isEnableVault()) {
            final String primaryGroup = VaultHook.getPermission().getPrimaryGroup(player);
            final Map<String, Integer> groupDelayMap = slowModeCache.getGroupDelayMap();
            if (groupDelayMap.containsKey(primaryGroup)) {
                slowDownTime = groupDelayMap.get(primaryGroup);
            }
        }

        final long timeToSendMessage = System.currentTimeMillis() - user.getLastMessage();
        if (timeToSendMessage < slowDownTime) {
            messageAPI.sendMessage("timeToNextMessage", player, ImmutableMap.of("{SLOWDOWN}", (slowDownTime - timeToSendMessage) / 1000D));
            event.setCancelled(true);
            return;
        }

        user.setLastMessage(System.currentTimeMillis());
    }
}
