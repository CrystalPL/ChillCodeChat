package pl.chillcode.chillcodechat.storage;

import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.user.User;
import pl.crystalek.crcapi.database.provider.BaseProvider;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface Provider extends BaseProvider {

    void createUser(final Player player);

    Map<String, Integer> getGroupsDelay();

    void saveUser(final UUID userUUID, final User user);

    Optional<User> getUser(final String nickname);

    Optional<User> getUser(final Player player);

    Optional<UUID> getPlayerUUID(final String nickname);

    void setGroupDelay(final String group, final int time);

    void setPlayerDelay(final UUID playerUUID, final int time);
}
