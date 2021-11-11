package pl.chillcode.chillcodechat.storage.mysql;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.storage.SQLProvider;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.util.SQLUtil;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class MySQLProvider extends SQLProvider {
    String insertUser;
    String insertGroupDelay;

    public MySQLProvider(final SQLUtil sqlUtil, final DatabaseConfig databaseConfig) {
        super(sqlUtil, databaseConfig);

        this.insertUser = String.format("INSERT INTO %suser(nickname, uuid) VALUES (?, ?) ON DUPLICATE KEY UPDATE nickname = ?;", databaseConfig.getPrefix());
        this.insertGroupDelay = String.format("INSERT INTO %sgroup_slowmode VALUES (?, ?) ON DUPLICATE KEY UPDATE time = ?;", databaseConfig.getPrefix());
        this.createTable();
    }

    @Override
    public void createUser(final Player player) {
        sqlUtil.executeUpdateAndOpenConnection(insertUser, player.getName(), player.getUniqueId().toString(), player.getName());
    }

    @Override
    public void setGroupDelay(final String group, final int time) {
        sqlUtil.executeUpdateAndOpenConnection(insertGroupDelay, group, time, time);
    }

    @Override
    public void createTable() {
        final String userTable = "CREATE TABLE IF NOT EXISTS %suser (\n" +
                "    id INTEGER AUTO_INCREMENT PRIMARY KEY UNIQUE NOT NULL,\n" +
                "    nickname VARCHAR(16) NOT NULL,\n" +
                "    uuid CHAR(36) NOT NULL UNIQUE,\n" +
                "    slowMode_time INTEGER,\n" +
                "    break_stone_amount INTEGER\n" +
                ");";

        final String groupSlowModeTable = "CREATE TABLE IF NOT EXISTS %sgroup_slowmode (\n" +
                "    id INTEGER AUTO_INCREMENT PRIMARY KEY UNIQUE NOT NULL,\n" +
                "    group_name TEXT NOT NULL,\n" +
                "    time INTEGER NOT NULL\n" +
                ");";

        this.createTable(userTable, groupSlowModeTable);
    }
}
