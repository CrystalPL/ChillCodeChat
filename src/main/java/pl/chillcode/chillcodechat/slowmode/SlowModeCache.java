package pl.chillcode.chillcodechat.slowmode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SlowModeCache {
    Map<UUID, User> userDelayMap = new HashMap<>();
    Map<String, Integer> groupDelayMap;

    public void setGroupDelay(final String groupName, final int time) {
        groupDelayMap.put(groupName, time);
    }

    public void createUser(final Player player, final User user) {
        userDelayMap.put(player.getUniqueId(), user);
    }

    public User removeUser(final UUID playerUUID) {
        return userDelayMap.remove(playerUUID);
    }

    public User getUser(final UUID playerUUID) {
        return userDelayMap.get(playerUUID);
    }
}
