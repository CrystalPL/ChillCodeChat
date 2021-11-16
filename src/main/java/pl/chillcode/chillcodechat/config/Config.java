package pl.chillcode.chillcodechat.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.config.FileHelper;
import pl.crystalek.crcapi.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.storage.config.DatabaseConfig;
import pl.crystalek.crcapi.storage.config.DatabaseConfigLoader;
import pl.crystalek.crcapi.util.NumberUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class Config {
    final FileConfiguration config;
    final FileHelper serverConfigFileHelper;
    final JavaPlugin plugin;
    DatabaseConfig databaseConfig;
    String commandName;
    List<String> commandAliases;
    boolean broadcastAction;
    @Setter
    int minimalStoneBreak;
    @Setter
    int serverSlowMode;
    @Setter
    boolean chatEnable;
    int autoSaveTime;

    public boolean load() {
        this.commandName = config.getString("command.chat.name");
        this.commandAliases = Arrays.asList(config.getString("command.chat.aliases").split(", "));

        try {
            this.databaseConfig = DatabaseConfigLoader.getDatabaseConfig(config.getConfigurationSection("database"), plugin);
        } catch (final ConfigLoadException exception) {
            plugin.getLogger().severe("Wystąpił błąd podczas próby załadowania konfiguracji bazy danych");
            plugin.getLogger().severe(exception.getMessage());
            return false;
        }

        this.broadcastAction = config.getBoolean("broadcastAction");
        final FileConfiguration serverSettings = serverConfigFileHelper.getConfiguration();
        final Optional<Integer> minimalStoneBreakOptional = NumberUtil.getInt(serverSettings.get("minimalStoneBreak"));
        if (!minimalStoneBreakOptional.isPresent()) {
            plugin.getLogger().severe("Wartość pola minimalStoneBreak nie jest liczbą całkowitą!");
            return false;
        }

        final Optional<Integer> serverSlowModeOptional = NumberUtil.getInt(serverSettings.get("serverSlowMode"));
        if (!serverSlowModeOptional.isPresent()) {
            plugin.getLogger().severe("Wartość pola serverSlowMode nie jest liczbą całkowitą!");
            return false;
        }

        this.minimalStoneBreak = minimalStoneBreakOptional.get();
        this.serverSlowMode = serverSlowModeOptional.get() * 1000;
        this.chatEnable = serverSettings.getBoolean("chatEnable");
        this.autoSaveTime = config.getInt("autoSaveTime") * 20;

        return true;
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
