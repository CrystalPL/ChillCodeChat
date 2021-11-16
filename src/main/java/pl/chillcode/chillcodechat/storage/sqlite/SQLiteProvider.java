package pl.chillcode.chillcodechat.storage.sqlite;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.storage.SQLProvider;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.util.SQLUtil;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SQLiteProvider extends SQLProvider {
    String insertUser;

    public SQLiteProvider(final SQLUtil sqlUtil, final DatabaseConfig databaseConfig) {
        super(sqlUtil, databaseConfig);

        this.insertUser = String.format("INSERT OR REPLACE INTO %suser(nickname, uuid) VALUES (?, ?);", databaseConfig.getPrefix());
        this.createTable();
    }

    @Override
    public void createUser(final Player player) {
        sqlUtil.executeUpdateAndOpenConnection(insertUser, player.getName(), player.getUniqueId().toString());
    }

    @Override
    public void createTable() {
        final String userTable = "CREATE TABLE IF NOT EXISTS %suser (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,\n" +
                "    nickname VARCHAR(16) NOT NULL,\n" +
                "    uuid CHAR(36) NOT NULL UNIQUE,\n" +
                "    time INTEGER,\n" +
                "    break_stone_amount INTEGER\n" +
                ");";

        final String groupSlowModeTable = "CREATE TABLE IF NOT EXISTS %sgroup_slowmode (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,\n" +
                "    group_name TEXT NOT NULL,\n" +
                "    time INTEGER NOT NULL\n" +
                ");";

        this.createTable(userTable, groupSlowModeTable);
    }
}
