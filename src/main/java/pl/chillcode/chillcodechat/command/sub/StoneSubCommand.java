package pl.chillcode.chillcodechat.command.sub;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.command.SubCommand;
import pl.chillcode.chillcodechat.config.Config;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class StoneSubCommand implements SubCommand {
    Config config;
    JavaPlugin plugin;
    MessageAPI messageAPI;

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
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "chillcode.chat.stone";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "stone.usage";
    }
}
