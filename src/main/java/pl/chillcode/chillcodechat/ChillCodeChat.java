package pl.chillcode.chillcodechat;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.command.ChatCommand;
import pl.chillcode.chillcodechat.config.Config;
import pl.chillcode.chillcodechat.hook.VaultHook;
import pl.chillcode.chillcodechat.listener.AsyncPlayerChatListener;
import pl.chillcode.chillcodechat.listener.BlockBreakListener;
import pl.chillcode.chillcodechat.listener.PlayerJoinListener;
import pl.chillcode.chillcodechat.listener.PlayerQuitListener;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.chillcode.chillcodechat.storage.mongo.MongoProvider;
import pl.chillcode.chillcodechat.storage.mysql.MySQLProvider;
import pl.chillcode.chillcodechat.storage.sqlite.SQLiteProvider;
import pl.chillcode.chillcodechat.task.AutoSaveTask;
import pl.crystalek.crcapi.command.CommandRegistry;
import pl.crystalek.crcapi.core.config.FileHelper;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.database.storage.Storage;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.MessageAPIProvider;

import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ChillCodeChat extends JavaPlugin {
    Storage<Provider> storage;
    SlowModeCache slowModeCache;

    @Override
    public void onEnable() {
        final MessageAPI messageAPI = Bukkit.getServicesManager().getRegistration(MessageAPIProvider.class).getProvider().getSingleMessage(this);
        if (!messageAPI.init()) {
            return;
        }

        final FileHelper serverConfigFileHelper = new FileHelper(this, "serverConfig.yml");
        try {
            serverConfigFileHelper.checkExist();
            serverConfigFileHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku z konfiguracją serwera..");
            getLogger().severe("Wyłączanie pluginu");
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        final Config config = new Config(this, "config.yml", serverConfigFileHelper);
        try {
            config.checkExist();
            config.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        try {
            config.loadConfig();
        } catch (final ConfigLoadException exception) {
            getLogger().severe(exception.getMessage());
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        storage = new Storage<>(config.getDatabaseConfig(), this);
        if (!storage.initDatabase()) {
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        storage.initProvider(MySQLProvider.class, SQLiteProvider.class, MongoProvider.class);
        final Provider provider = storage.getProvider();

        VaultHook.init();

        slowModeCache = new SlowModeCache(provider.getGroupsDelay(), provider);

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockBreakListener(slowModeCache), this);
        pluginManager.registerEvents(new PlayerJoinListener(slowModeCache, this), this);
        pluginManager.registerEvents(new PlayerQuitListener(provider, slowModeCache, this), this);
        pluginManager.registerEvents(new AsyncPlayerChatListener(config, slowModeCache, messageAPI), this);

        CommandRegistry.register(new ChatCommand(messageAPI, config.getCommandDataMap(), config, provider, slowModeCache, this));

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoSaveTask(provider, slowModeCache), 0, config.getAutoSaveTime());
        Bukkit.getOnlinePlayers().forEach(slowModeCache::createUser);
    }

    @Override
    public void onDisable() {
        if (slowModeCache != null) {
            slowModeCache.getUserDelayMap().forEach(storage.getProvider()::saveUser);
        }

        if (storage != null) {
            storage.close();
        }
    }

}
