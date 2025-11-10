package com.m4ssive.totemcounterv2.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

public final class TextFormatter {

    private TextFormatter() {}

    public static MutableText build(Text base, String template, String playerName, int count) {
        if (base == null) {
            base = Text.empty();
        }
        if (template == null || template.isBlank()) {
            return base.copy();
        }

        String replaced = template;
        if (playerName != null) {
            replaced = replaced.replace("{name}", playerName);
        }
        String display = base.getString();
        if (display != null) {
            replaced = replaced.replace("{display}", display);
        }
        replaced = replaced.replace("{count}", String.valueOf(count));
        if (replaced.isBlank()) {
            return base.copy();
        }

        MutableText parsed = parse(replaced);
        if (containsNamePlaceholder(template)) {
            return parsed;
        }

        MutableText result = base.copy();
        result.append(parsed);
        return result;
    }

    private static boolean containsNamePlaceholder(String template) {
        String lower = template.toLowerCase(Locale.ROOT);
        return lower.contains("{name}") || lower.contains("{display}");
    }

    public static MutableText parse(String input) {
        MutableText result = Text.empty();
        if (input == null || input.isEmpty()) {
            return result;
        }

        Style currentStyle = Style.EMPTY;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c == '\u00A7' || c == '&') && i + 1 < input.length()) {
                Formatting formatting = Formatting.byCode(Character.toLowerCase(input.charAt(i + 1)));
                if (formatting != null) {
                    i++;
                    if (buffer.length() > 0) {
                        result.append(Text.literal(buffer.toString()).setStyle(currentStyle));
                        buffer.setLength(0);
                    }
                    currentStyle = applyFormatting(currentStyle, formatting);
                    continue;
                }
            }
            buffer.append(c);
        }

        if (buffer.length() > 0) {
            result.append(Text.literal(buffer.toString()).setStyle(currentStyle));
        }

        return result;
    }

    private static Style applyFormatting(Style style, Formatting formatting) {
        if (formatting == Formatting.RESET) {
            return Style.EMPTY;
        }

        if (formatting.isColor()) {
            return style.withColor(formatting.getColorValue());
        }

        switch (formatting) {
            case BOLD:
                return style.withBold(true);
            case ITALIC:
                return style.withItalic(true);
            case UNDERLINE:
                return style.withUnderline(true);
            case STRIKETHROUGH:
                return style.withStrikethrough(true);
            case OBFUSCATED:
                return style.withObfuscated(true);
            default:
                return style;
        }
    }
}


