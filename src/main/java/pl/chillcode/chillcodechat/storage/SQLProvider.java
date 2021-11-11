package pl.chillcode.chillcodechat.storage;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.user.User;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.util.SQLFunction;
import pl.crystalek.crcapi.storage.util.SQLUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class SQLProvider extends Provider {
    protected final SQLUtil sqlUtil;
    private final DatabaseConfig databaseConfig;
    private String selectGroupSlowMode;
    private String selectPlayerUUID;
    private String updatePlayerSlowMode;
    private String saveUser;
    private String getUserByUUID;
    private String getUserByNickname;

    @Override
    public Map<String, Integer> getGroupsDelay() {
        final SQLFunction<ResultSet, Map<String, Integer>> function = resultSet -> {
            if (resultSet == null || !resultSet.next()) {
                return new HashMap<>();
            }

            final Map<String, Integer> groupDelayMap = new HashMap<>();

            do {
                final String groupName = resultSet.getString("group_name");
                final int slowModeTime = resultSet.getInt("time");

                groupDelayMap.put(groupName, slowModeTime);
            } while (resultSet.next());

            return groupDelayMap;
        };

        return sqlUtil.executeQueryAndOpenConnection(selectGroupSlowMode, function);
    }

    @Override
    public void saveUser(final UUID userUUID, final User user) {
        sqlUtil.executeUpdateAndOpenConnection(saveUser, user.getSlowDownTime(), user.getBreakStone(), userUUID.toString());
    }

    private Optional<User> getUser(final String sql, final Object... params) {
        final SQLFunction<ResultSet, Optional<User>> function = resultSet -> {
            if (resultSet == null || !resultSet.next()) {
                return Optional.empty();
            }

            final int slowModeTime = resultSet.getInt("slowMode_time");
            final int breakStoneAmount = resultSet.getInt("break_stone_amount");

            return Optional.of(new User(breakStoneAmount, slowModeTime));
        };

        return sqlUtil.executeQueryAndOpenConnection(sql, function, params);
    }

    @Override
    public Optional<User> getUser(final String nickname) {
        return getUser(getUserByNickname, nickname);
    }

    @Override
    public Optional<User> getUser(final Player player) {
        return getUser(getUserByUUID, player.getUniqueId().toString());
    }

    @Override
    public Optional<UUID> getPlayerUUID(final String nickname) {
        final SQLFunction<ResultSet, Optional<UUID>> function = resultSet -> {
            if (resultSet == null || !resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(UUID.fromString(resultSet.getString("uuid")));
        };

        return sqlUtil.executeQueryAndOpenConnection(selectPlayerUUID, function, nickname);
    }

    @Override
    public void setPlayerDelay(final UUID playerUUID, final int time) {
        sqlUtil.executeUpdateAndOpenConnection(updatePlayerSlowMode, time, playerUUID.toString());
    }

    public void createTable(final String userTable, final String groupSlowModeTable) {
        final String prefix = databaseConfig.getPrefix();
        selectGroupSlowMode = String.format("SELECT * FROM %sgroup_slowmode;", prefix);
        selectPlayerUUID = String.format("SELECT uuid FROM %suser WHERE nickname = ?;", prefix);
        updatePlayerSlowMode = String.format("UPDATE %suser SET slowMode_time = ? WHERE uuid = ?;", prefix);
        saveUser = String.format("UPDATE %suser SET slowMode_time = ?, break_stone_amount = ? WHERE uuid = ?;", prefix);
        getUserByUUID = String.format("SELECT slowMode_time, break_stone_amount FROM %suser WHERE uuid = ? LIMIT 1;", prefix);
        getUserByNickname = String.format("SELECT slowMode_time, break_stone_amount FROM %suser WHERE nickname = ? LIMIT 1;", prefix);

        sqlUtil.openConnection(connection -> {
            @Cleanup final PreparedStatement userTableStatement = connection.prepareStatement(String.format(userTable, prefix));
            @Cleanup final PreparedStatement grupSlowModeTableStatement = connection.prepareStatement(String.format(groupSlowModeTable, prefix));

            userTableStatement.executeUpdate();
            grupSlowModeTableStatement.executeUpdate();
        });
    }
}
