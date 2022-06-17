package pl.chillcode.chillcodechat.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.loader.CommandLoader;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.config.ConfigHelper;
import pl.crystalek.crcapi.core.config.ConfigParserUtil;
import pl.crystalek.crcapi.core.config.FileHelper;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.database.config.DatabaseConfig;
import pl.crystalek.crcapi.database.config.DatabaseConfigLoader;

import java.io.IOException;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config extends ConfigHelper {
    final FileHelper serverConfigFileHelper;
    DatabaseConfig databaseConfig;
    Map<Class<? extends Command>, CommandData> commandDataMap;
    boolean broadcastAction;
    @Setter
    int minimalStoneBreak;
    @Setter
    int serverSlowMode;
    @Setter
    boolean chatEnable;
    int autoSaveTime;

    public Config(final JavaPlugin plugin, final String fileName, final FileHelper serverConfigFileHelper) {
        super(plugin, fileName);

        this.serverConfigFileHelper = serverConfigFileHelper;
    }

    public void loadConfig() throws ConfigLoadException {
        this.databaseConfig = DatabaseConfigLoader.getDatabaseConfig(configuration.getConfigurationSection("database"), plugin);
        this.commandDataMap = CommandLoader.loadCommands(configuration.getConfigurationSection("command"), plugin.getClass().getClassLoader());
        this.broadcastAction = ConfigParserUtil.getBoolean(configuration, "broadcastAction");
        this.autoSaveTime = ConfigParserUtil.getInt(configuration, "autoSaveTime") * 20;

        final FileConfiguration serverSettings = serverConfigFileHelper.getConfiguration();
        this.minimalStoneBreak = ConfigParserUtil.getInt(serverSettings, "minimalStoneBreak");
        this.serverSlowMode = ConfigParserUtil.getInt(serverSettings, "serverSlowMode") * 1000;
    }

    public boolean saveServerSettings() {
        final FileConfiguration serverSettings = serverConfigFileHelper.getConfiguration();
        serverSettings.set("minimalStoneBreak", this.minimalStoneBreak);
        serverSettings.set("serverSlowMode", this.serverSlowMode / 1000);
        serverSettings.set("chatEnable", this.chatEnable);
        try {
            serverConfigFileHelper.save();
        } catch (final IOException exception) {
            plugin.getLogger().severe("Wystąpił problem podczas zapisu ustawień serwera");
            exception.printStackTrace();
            return false;
        }

        return true;
    }
}
