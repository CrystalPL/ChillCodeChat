package pl.chillcode.chillcodechat.command.sub;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.lib.adventure.adventure.text.Component;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ChatClearSubCommand extends Command {
    public ChatClearSubCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap) {
        super(messageAPI, commandDataMap);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            for (final Player player : Bukkit.getOnlinePlayers().stream().filter(player -> !player.hasPermission("chillcode.chat.clear.bypass")).collect(Collectors.toList())) {
                for (int i = 0; i < 100; i++) {
                    messageAPI.sendMessage(Component.newline(), player, ImmutableMap.of());
                }
            }

            messageAPI.broadcast("clear.serverChatClear", ImmutableMap.of("{ADMIN_NAME}", sender.getName()));
            return;
        }

        if (!sender.hasPermission("chillcode.chat.clear.player")) {
            messageAPI.sendMessage("noPermission", sender, ImmutableMap.of("{PERMISSION}", "chillcode.chat.clear.player"));
            return;
        }

        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            messageAPI.sendMessage("playerNotFound", sender);
            return;
        }

        if (player.hasPermission("chillcode.chat.clear.bypass")) {
            messageAPI.sendMessage("clear.bypass", sender);
            return;
        }

        for (int i = 0; i < 100; i++) {
            messageAPI.sendMessage(Component.newline(), player, ImmutableMap.of());
        }
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.chat.clear.player";
    }

    @Override
    public boolean isUseConsole() {
        return true;
    }

    @Override
    public String getCommandUsagePath() {
        return "clear.usage";
    }

    @Override
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }
}
