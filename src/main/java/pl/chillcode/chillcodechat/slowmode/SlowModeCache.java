package pl.chillcode.chillcodechat.slowmode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.chillcode.chillcodechat.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SlowModeCache {
    Map<UUID, User> userDelayMap = new HashMap<>();
    Map<String, Integer> groupDelayMap;
    Provider provider;

    public void setGroupDelay(final String groupName, final int time) {
        groupDelayMap.put(groupName, time);
    }

    public void createUser(final Player player) {
        final Optional<User> userOptional = provider.getUser(player);
        final User user;
        if (!userOptional.isPresent()) {
            user = new User();
            provider.createUser(player);
        } else {
            user = userOptional.get();
        }

        userDelayMap.put(player.getUniqueId(), user);
    }

    public User removeUser(final UUID playerUUID) {
        return userDelayMap.remove(playerUUID);
    }

    public User getUser(final UUID playerUUID) {
        return userDelayMap.get(playerUUID);
    }
}
