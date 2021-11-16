package pl.chillcode.chillcodechat.command.sub;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.chillcodechat.command.SubCommand;
import pl.crystalek.crcapi.lib.adventure.adventure.text.Component;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ChatClearCommand implements SubCommand {
    MessageAPI messageAPI;

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

        //wiadomosc wyswietlana, gdy gracz ma uprawnienie zabraniajace wyczyszczenia mu chatu
        if (player.hasPermission("chillcode.chat.clear.bypass")) {
            messageAPI.sendMessage("clear.bypass", sender);
            return;
        }

        for (int i = 0; i < 100; i++) {
            messageAPI.sendMessage(Component.newline(), player, ImmutableMap.of());
        }

        messageAPI.sendMessage("clear.playerClearAdmin", sender, ImmutableMap.of("{PLAYER_NAME}", player.getName()));
        messageAPI.sendMessage("clear.playerChatClear", player, ImmutableMap.of("{ADMIN_NAME}", sender.getName()));
    }

    @Override
    public int maxArgumentLength() {
        return 2;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "chillcode.chat.clear";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "clear.usage";
    }
}
