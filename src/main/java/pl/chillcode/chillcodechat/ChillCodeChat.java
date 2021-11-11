package pl.chillcode.chillcodechat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.command.ChatCommand;
import pl.chillcode.chillcodechat.config.Config;
import pl.chillcode.chillcodechat.hook.VaultHook;
import pl.chillcode.chillcodechat.listener.BlockBreakListener;
import pl.chillcode.chillcodechat.listener.PlayerJoinListener;
import pl.chillcode.chillcodechat.listener.PlayerQuitListener;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.chillcode.chillcodechat.storage.Storage;
import pl.chillcode.chillcodechat.task.AutoSaveTask;
import pl.crystalek.crcapi.command.CommandRegistry;
import pl.crystalek.crcapi.config.ConfigHelper;
import pl.crystalek.crcapi.config.FileHelper;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.storage.BaseStorage;

import java.io.IOException;

public final class ChillCodeChat extends JavaPlugin {
    private Storage storage;

    @Override
    public void onEnable() {
        final ConfigHelper configHelper = new ConfigHelper("config.yml", this);
        try {
            configHelper.checkExist();
            configHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu");
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        final FileHelper serverConfigFileHelper = new FileHelper("serverConfig.yml", this);
        try {
            serverConfigFileHelper.checkExist();
            serverConfigFileHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku z konfiguracją serwera..");
            getLogger().severe("Wyłączanie pluginu");
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        final Config config = new Config(configHelper.getConfiguration(), serverConfigFileHelper, this);
        if (!config.load()) {
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        final MessageAPI messageAPI = new MessageAPI(this);
        if (!messageAPI.init()) {
            return;
        }

        storage = new Storage(new BaseStorage<>(config.getDatabaseConfig(), this));
        if (!storage.init()) {
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        VaultHook.init();

        final Provider provider = storage.getStorage().getProvider();
        final SlowModeCache slowModeCache = new SlowModeCache(provider.getGroupsDelay());

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockBreakListener(slowModeCache), this);
        pluginManager.registerEvents(new PlayerJoinListener(slowModeCache, provider, this), this);
        pluginManager.registerEvents(new PlayerQuitListener(provider, slowModeCache, this), this);

        final ChatCommand chatCommand = new ChatCommand(config, this, provider, slowModeCache, messageAPI);
        CommandRegistry.register(chatCommand);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoSaveTask(provider, slowModeCache), 0, config.getAutoSaveTime());
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.getStorage().getDatabase().close();
        }
    }

}
