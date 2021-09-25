package pl.chillcode.chillchat.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import java.util.List;
import pl.chillcode.chillchat.util.ColorUtil;

public class PluginConfiguration extends OkaeriConfig {

    public Chat chat = new Chat();
    public Messages messages = new Messages();

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

            public List<String> cleared = ColorUtil.color(" ", " &8>> &7Chat zostal &bwyczyszczony &7przez &e{ADMIN}", " ");

            public List<String> enabled = ColorUtil.color(" ", " &8>> &7Chat zostal &awlaczony &7przez &e{ADMIN}", " ");

            public List<String> disabled = ColorUtil.color(" ", " &8>> &7Chat zostal &cwylaczony &7przez &e{ADMIN}", " ");

        }

    }

}
