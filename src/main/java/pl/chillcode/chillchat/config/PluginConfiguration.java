package pl.chillcode.chillchat.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PluginConfiguration extends OkaeriConfig {

    @Exclude
    public static final Pattern DECOLOR_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");
    public Chat chat = new Chat();
    public Messages messages = new Messages();

    public static String decolor(String coloredString) {
        return DECOLOR_PATTERN.matcher(coloredString).replaceAll("&$1");
    }

    public static class Chat extends OkaeriConfig {

        public Clear clear = new Clear();

        public static class Clear extends OkaeriConfig {

            @Comment("Czy chat ma byc czyszczony, kiedy jest wlaczany.")
            @CustomKey("on-enable")
            public boolean onEnable = false;

            @Comment("Czy chat ma byc czyszczony, kiedy jest wylaczany.")
            @CustomKey("on-disable")
            public boolean onDisable = false;

            @Comment("Wartosci ponizej 100 sa w wiekszosci przypadkow nieuzytyeczne - najlepiej zostawic standardowe ustawienie (100).")
            @CustomKey("iterations")
            public int iterations = 100;

        }

    }

    public static class Messages extends OkaeriConfig {

        public Chat chat = new Chat();

        public static class Chat extends OkaeriConfig {

            public List<String> cleared = Arrays.asList(" ", " &8>> &7Chat zostal &bwyczyszczony &7przez &e{ADMIN}", " ");

            public List<String> enabled = Arrays.asList(" ", " &8>> &7Chat zostal &awlaczony &7przez &e{ADMIN}", " ");

            public List<String> disabled = Arrays.asList(" ", " &8>> &7Chat zostal &cwylaczony &7przez &e{ADMIN}", " ");

        }

    }

}
