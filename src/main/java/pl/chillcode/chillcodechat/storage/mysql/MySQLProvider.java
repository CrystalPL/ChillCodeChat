package pl.chillcode.chillcodechat.storage.mysql;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.storage.SQLProvider;
import pl.crystalek.crcapi.database.config.DatabaseConfig;
import pl.crystalek.crcapi.lib.hikari.HikariDataSource;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class MySQLProvider extends SQLProvider {
    String insertUser;

    public MySQLProvider(final DatabaseConfig databaseConfig, final HikariDataSource database) {
        super(databaseConfig, database);

        this.insertUser = String.format("INSERT INTO %suser(nickname, uuid) VALUES (?, ?) ON DUPLICATE KEY UPDATE nickname = ?;", databaseConfig.getPrefix());
        this.createTable();
    }

    @Override
    public void createUser(final Player player) {
        executeUpdateAndOpenConnection(insertUser, player.getName(), player.getUniqueId().toString(), player.getName());
    }

    @Override
    public void createTable() {
        final String userTable = "CREATE TABLE IF NOT EXISTS %suser (\n" +
                "    id INTEGER AUTO_INCREMENT PRIMARY KEY UNIQUE NOT NULL,\n" +
                "    nickname VARCHAR(16) NOT NULL,\n" +
                "    uuid CHAR(36) NOT NULL UNIQUE,\n" +
                "    time INTEGER,\n" +
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
