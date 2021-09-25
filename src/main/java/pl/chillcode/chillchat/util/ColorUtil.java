package pl.chillcode.chillchat.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;

public final class ColorUtil {

    private static final char COLOR_CHAR = '\u00A7';
    private static final char ALT_COLOR_CHAR = '&';

    private ColorUtil() {
    }

    public static String color(String text) {
        if (text == null || text.isEmpty()) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(List<String> text) {
        if (text == null || text.isEmpty()) return Collections.singletonList("");
        return text.stream()
                .map(ColorUtil::color).collect(Collectors.toList());
    }

    public static List<String> color(String... text) {
        return color(Arrays.asList(text));
    }

    public static String decolor(String text) {
        return StringUtils.replace(text, COLOR_CHAR + "", ALT_COLOR_CHAR + "");
    }

    public static List<String> decolor(List<String> text) {
        return text.stream().map(ColorUtil::decolor).collect(Collectors.toList());
    }

    public static List<String> decolor(String... text) {
        return decolor(Arrays.asList(text));
    }

}
