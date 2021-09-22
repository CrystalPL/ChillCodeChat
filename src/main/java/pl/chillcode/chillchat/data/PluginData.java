package pl.chillcode.chillchat.data;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginData extends OkaeriConfig {

    public Chat chat = new Chat();

    public static class Chat extends OkaeriConfig {

        public boolean enabled = true;

    }

}
