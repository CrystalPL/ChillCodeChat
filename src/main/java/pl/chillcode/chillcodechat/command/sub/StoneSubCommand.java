package pl.chillcode.chillcodechat.command.sub;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.config.Config;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.util.NumberUtil;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class StoneSubCommand extends Command {
    Config config;
    JavaPlugin plugin;

    public StoneSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Config config, final JavaPlugin plugin) {
        super(messageAPI, commandDataMap);

        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Optional<Integer> stoneBreakAmountOptional = NumberUtil.getInt(args[1]);
        if (!stoneBreakAmountOptional.isPresent()) {
            messageAPI.sendMessage("stone.stoneAmountError", sender);
            return;
        }

        final Integer stoneBreakAmount = stoneBreakAmountOptional.get();
        config.setMinimalStoneBreak(stoneBreakAmount);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!config.saveServerSettings()) {
                messageAPI.sendMessage("saveError", sender);
                return;
            }

            messageAPI.sendMessage("stone.setStone", sender, ImmutableMap.of("{STONE_AMOUNT}", stoneBreakAmount));
        });
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.chat.stone";
    }

    @Override
    public boolean isUseConsole() {
        return true;
    }

    @Override
    public String getCommandUsagePath() {
        return "stone.usage";
    }

    @Override
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 2;
    }
}
