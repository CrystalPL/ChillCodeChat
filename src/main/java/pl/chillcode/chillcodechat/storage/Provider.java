package pl.chillcode.chillcodechat.storage;

import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.user.User;
import pl.crystalek.crcapi.storage.BaseProvider;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class Provider extends BaseProvider {

    public abstract void createUser(final Player player);

    public abstract Map<String, Integer> getGroupsDelay();

    public abstract void saveUser(final UUID userUUID, final User user);

    public abstract Optional<User> getUser(final String nickname);

    public abstract Optional<User> getUser(final Player player);

    public abstract Optional<UUID> getPlayerUUID(final String nickname);

    public abstract void setGroupDelay(final String group, final int time);

    public abstract void setPlayerDelay(final UUID playerUUID, final int time);
}
