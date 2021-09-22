package pl.chillcode.chillchat;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.SimpleObjectTransformer;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillchat.command.chat.ChatCommand;
import pl.chillcode.chillchat.config.PluginConfiguration;
import pl.chillcode.chillchat.data.PluginData;

public class ChillChat extends JavaPlugin {

    private PluginConfiguration pluginConfiguration;

    private PluginData pluginData;

    @Override
    public void onEnable() {
        this.pluginConfiguration = ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withBindFile(new File(this.getDataFolder(), "config.yml"));
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withSerdesPack(registry -> registry.register(SimpleObjectTransformer.of(String.class, String.class, PluginConfiguration::decolor)));
            it.saveDefaults();
            it.load(true);
        });

        this.pluginData = ConfigManager.create(PluginData.class, (it) -> {
            it.withBindFile(new File(this.getDataFolder(), "data.yml"));
           it.withConfigurer(new YamlBukkitConfigurer());
           it.saveDefaults();
           it.load(true);
        });

        getCommand("chat").setExecutor(new ChatCommand(this));
    }

    public PluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }

    public PluginData getPluginData() {
        return pluginData;
    }

}
