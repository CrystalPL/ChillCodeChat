package pl.chillcode.chillcodechat.storage.mongo;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.chillcode.chillcodechat.user.User;
import pl.crystalek.crcapi.lib.bson.Document;
import pl.crystalek.crcapi.lib.mongodb.client.MongoCollection;
import pl.crystalek.crcapi.lib.mongodb.client.MongoDatabase;
import pl.crystalek.crcapi.lib.mongodb.client.model.ReplaceOptions;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class MongoProvider extends Provider {
    final ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);
    final MongoDatabase mongoDatabase;
    final DatabaseConfig databaseConfig;
    MongoCollection<Document> userSlowModeCollection;
    MongoCollection<Document> groupSlowModeCollection;

    @Override
    public void createUser(final Player player) {
        final Document userDocument = new Document()
                .append("_id", player.getUniqueId().toString())
                .append("nickname", player.getName());

        userSlowModeCollection.replaceOne(new Document("_id", player.getUniqueId().toString()), userDocument, replaceOptions);
    }

    @Override
    public Map<String, Integer> getGroupsDelay() {
        final Map<String, Integer> groupMap = new HashMap<>();

        for (final Document document : groupSlowModeCollection.find()) {
            final String groupName = document.get("groupName", String.class);
            final Integer slowMode = document.get("slowMode", Integer.class) * 1000;

            groupMap.put(groupName, slowMode);
        }

        return groupMap;
    }

    @Override
    public void saveUser(final UUID userUUID, final User user) {
        final Document userDocument = new Document("_id", userUUID.toString());

        final Document updateDocument = new Document()
                .append("slowMode", user.getSlowDownTime() / 1000)
                .append("breakStoneAmount", user.getBreakStone());

        userSlowModeCollection.updateOne(userDocument, updateDocument);
    }

    @Override
    public Optional<User> getUser(final String nickname) {
        return getUser(new Document("nickname", Pattern.compile(".*" + nickname + "*", Pattern.CASE_INSENSITIVE)));
    }

    @Override
    public Optional<User> getUser(final Player player) {
        return getUser(new Document("_id", player.getUniqueId().toString()));
    }

    private Optional<User> getUser(final Document playerDocument) {
        final Document foundUserDocument = userSlowModeCollection.find(playerDocument).first();
        if (foundUserDocument == null) {
            return Optional.empty();
        }

        final Integer slowMode = foundUserDocument.get("slowMode", Integer.class);
        final Integer breakStoneAmount = foundUserDocument.get("breakStoneAmount", Integer.class);

        return Optional.of(new User(breakStoneAmount, slowMode * 1000));
    }

    @Override
    public Optional<UUID> getPlayerUUID(final String nickname) {
        final Document playerUUIDDocument = new Document("nickname", Pattern.compile(".*" + nickname + "*", Pattern.CASE_INSENSITIVE));
        final Document foundUserDocument = userSlowModeCollection.find(playerUUIDDocument).first();

        return foundUserDocument == null ? Optional.empty() : Optional.of(UUID.fromString(foundUserDocument.get("_id", String.class)));
    }

    @Override
    public void setGroupDelay(final String group, final int time) {
        final Document groupDocument = new Document()
                .append("groupName", group)
                .append("slowMode", time / 1000);

        userSlowModeCollection.replaceOne(new Document("groupName", Pattern.compile(".*" + group + "*", Pattern.CASE_INSENSITIVE)), groupDocument, replaceOptions);
    }

    @Override
    public void setPlayerDelay(final UUID playerUUID, final int time) {
        userSlowModeCollection.replaceOne(new Document("_id", playerUUID.toString()), new Document("slowMode", time), replaceOptions);
    }

    @Override
    public void createTable() {
        this.userSlowModeCollection = mongoDatabase.getCollection(String.format("%suserSlowMode", databaseConfig.getPrefix()));
        this.groupSlowModeCollection = mongoDatabase.getCollection(String.format("%sgroupSlowMode", databaseConfig.getPrefix()));
    }
}
