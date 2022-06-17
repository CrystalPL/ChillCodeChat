package pl.chillcode.chillcodechat.storage;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.user.User;
import pl.crystalek.crcapi.database.config.DatabaseConfig;
import pl.crystalek.crcapi.database.provider.sql.BaseSQLProvider;
import pl.crystalek.crcapi.database.provider.sql.model.SQLFunction;
import pl.crystalek.crcapi.lib.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class SQLProvider extends BaseSQLProvider implements Provider {
    String selectGroupSlowMode;
    String selectPlayerUUID;
    String updatePlayerSlowMode;
    String saveUser;
    String getUserByUUID;
    String getUserByNickname;
    String insertGroupDelay;
    String selectGroup;
    String updateGroup;

    public SQLProvider(final DatabaseConfig databaseConfig, final HikariDataSource database) {
        super(databaseConfig, database);

        final String prefix = databaseConfig.getPrefix();
        this.selectGroupSlowMode = String.format("SELECT * FROM %sgroup_slowmode;", prefix);
        this.selectPlayerUUID = String.format("SELECT uuid FROM %suser WHERE nickname = ?;", prefix);
        this.updatePlayerSlowMode = String.format("UPDATE %suser SET time = ? WHERE uuid = ?;", prefix);
        this.saveUser = String.format("UPDATE %suser SET time = ?, break_stone_amount = ? WHERE uuid = ?;", prefix);
        this.getUserByUUID = String.format("SELECT time, break_stone_amount FROM %suser WHERE uuid = ? LIMIT 1;", prefix);
        this.getUserByNickname = String.format("SELECT time, break_stone_amount FROM %suser WHERE nickname = ? LIMIT 1;", prefix);
        this.insertGroupDelay = String.format("INSERT INTO %sgroup_slowmode(group_name, time) VALUES (?, ?);", prefix);
        this.selectGroup = String.format("SELECT id FROM %sgroup_slowmode WHERE group_name = ?;", prefix);
        this.updateGroup = String.format("UPDATE %sgroup_slowmode SET time = ? WHERE group_name = ?;", prefix);
    }

    @Override
    public Map<String, Integer> getGroupsDelay() {
        final SQLFunction<ResultSet, Map<String, Integer>> function = resultSet -> {
            if (resultSet == null || !resultSet.next()) {
                return new HashMap<>();
            }

            final Map<String, Integer> groupDelayMap = new HashMap<>();

            do {
                final String groupName = resultSet.getString("group_name");
                final int slowModeTime = resultSet.getInt("time") * 1000;

                groupDelayMap.put(groupName, slowModeTime);
            } while (resultSet.next());

            return groupDelayMap;
        };

        return executeQueryAndOpenConnection(selectGroupSlowMode, function);
    }

    @Override
    public void saveUser(final UUID userUUID, final User user) {
        executeUpdateAndOpenConnection(saveUser, user.getSlowDownTime() / 1000, user.getBreakStone(), userUUID.toString());
    }

    @Override
    public Optional<User> getUser(final String nickname) {
        return getUser(getUserByNickname, nickname);
    }

    @Override
    public Optional<User> getUser(final Player player) {
        return getUser(getUserByUUID, player.getUniqueId().toString());
    }

    private Optional<User> getUser(final String sql, final Object... params) {
        final SQLFunction<ResultSet, Optional<User>> function = resultSet -> {
            if (resultSet == null || !resultSet.next()) {
                return Optional.empty();
            }

            final int slowModeTime = resultSet.getInt("time");
            final int breakStoneAmount = resultSet.getInt("break_stone_amount");

            return Optional.of(new User(breakStoneAmount, slowModeTime * 1000));
        };

        return executeQueryAndOpenConnection(sql, function, params);
    }

    @Override
    public Optional<UUID> getPlayerUUID(final String nickname) {
        final SQLFunction<ResultSet, Optional<UUID>> function = resultSet -> {
            if (resultSet == null || !resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(UUID.fromString(resultSet.getString("uuid")));
        };

        return executeQueryAndOpenConnection(selectPlayerUUID, function, nickname);
    }

    @Override
    public void setGroupDelay(final String group, final int time) {
        openConnection(connection -> {
            final Boolean isGroupExist = executeQuery(connection, selectGroup, resultSet -> resultSet != null && resultSet.next(), group);
            if (isGroupExist) {
                executeUpdate(connection, updateGroup, time, group);
            } else {
                executeUpdate(connection, insertGroupDelay, group, time);
            }
        });
    }

    @Override
    public void setPlayerDelay(final UUID playerUUID, final int time) {
        executeUpdateAndOpenConnection(updatePlayerSlowMode, time, playerUUID.toString());
    }

    public void createTable(final String userTable, final String groupSlowModeTable) {
        final String prefix = databaseConfig.getPrefix();

        openConnection(connection -> {
            @Cleanup final PreparedStatement userTableStatement = connection.prepareStatement(String.format(userTable, prefix));
            @Cleanup final PreparedStatement grupSlowModeTableStatement = connection.prepareStatement(String.format(groupSlowModeTable, prefix));

            userTableStatement.executeUpdate();
            grupSlowModeTableStatement.executeUpdate();
        });
    }
}
