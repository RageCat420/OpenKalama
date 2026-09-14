package me.matl114.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.StringVisitable.StyledVisitor;
import net.minecraft.text.StringVisitable.Visitor;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

public class ChatUtils$TextBuilder implements StyledVisitor<Unit>, CharacterVisitor, Visitor<Unit> {
    MutableText empty;
    Style style = Style.EMPTY;
    StringBuilder builder;

    public ChatUtils$TextBuilder withParent(Style parent) {
        return this.withStyle(this.style.withParent(parent));
    }

    public Style currentStyle() {
        return this.style;
    }

    // $VF: Unable to simplify switch on enum
    // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a
    // copy of the class file (if you have the rights to distribute it!)
    public ChatUtils$TextBuilder withLegacy(String value) {
        if (value == null) {
            return this;
        } else {
            ChatUtils$TextBuilder builder = this;
            Matcher matcher = ChatUtils.b.matcher(value);
            String match = null;
            StringBuilder hexColor = null;
            int currentIndex = 0;
            boolean hasReset = false;
            boolean needsAdd = false;

            while (matcher.find()) {
                int groupId = 0;

                while ((match = matcher.group(++groupId)) == null) {}

                int index = matcher.start(groupId);
                if (index > currentIndex) {
                    builder.with(value.substring(currentIndex, index));
                    needsAdd = false;
                    currentIndex = index;
                }

                switch (groupId) {
                    case 1:
                        char c = match.toLowerCase(Locale.ENGLISH).charAt(1);
                        if (c == 'x') {
                            hexColor = new StringBuilder("#");
                        } else if (hexColor != null) {
                            hexColor.append(c);
                            if (hexColor.length() == 7) {
                                builder.withStyle(ChatUtils.d.withColor((TextColor) TextColor.parse(hexColor.toString())
                                        .result()
                                        .get()));
                                hexColor = null;
                            }
                        } else {
                            Formatting format = ChatUtils.e.get(c);
                            if (format.isModifier() && format != Formatting.RESET) {
                                switch (format) {
                                    case BOLD:
                                        builder.withBold(true);
                                        break;
                                    case ITALIC:
                                        builder.withItalic(true);
                                        break;
                                    case STRIKETHROUGH:
                                        builder.withStrikethrough(true);
                                        break;
                                    case UNDERLINE:
                                        builder.withUnderline(true);
                                        break;
                                    case OBFUSCATED:
                                        builder.withObfuscated(true);
                                        break;
                                    default:
                                        throw new AssertionError("Unexpected message format");
                                }
                            } else {
                                builder.withReset(format, hasReset);
                                hasReset = true;
                            }
                        }

                        needsAdd = true;
                        break;
                    case 2:
                        if (needsAdd) {
                            builder.with(value.substring(currentIndex, index));
                        }

                        builder.withLine();
                }

                currentIndex = matcher.end(groupId);
            }

            int len = value.length();
            if (currentIndex < value.length() || needsAdd) {
                builder.with(value.substring(currentIndex, len));
            }

            return this;
        }
    }

    public MutableText peek() {
        return this.empty;
    }

    public ChatUtils$TextBuilder withColor(@Nullable TextColor color) {
        return this.withStyle(this.style.withColor(color));
    }

    public ChatUtils$TextBuilder with(long l) {
        this.builder.append(l);
        return this;
    }

    public Optional<Unit> accept(String asString) {
        this.with(asString);
        return Optional.empty();
    }

    public ChatUtils$TextBuilder withColor(int rgbColor) {
        return this.withStyle(this.style.withColor(rgbColor));
    }

    public ChatUtils$TextBuilder withStyle(Style style) {
        if (!this.builder.isEmpty() && !Objects.equals(style, this.style)) {
            this.write();
        }

        this.style = style;
        return this;
    }

    public ChatUtils$TextBuilder withStrikethrough(@Nullable Boolean strikethrough) {
        return this.withStyle(this.style.withStrikethrough(strikethrough));
    }

    public ChatUtils$TextBuilder withFormat(String format, Object... args) {
        this.builder.append(String.format(format, args));
        return this;
    }

    public ChatUtils$TextBuilder with(int i) {
        this.builder.append(i);
        return this;
    }

    public ChatUtils$TextBuilder with(double d) {
        this.builder.append(d);
        return this;
    }

    public ChatUtils$TextBuilder withFormat(Formatting format) {
        return this.withStyle(this.style.withFormatting(format));
    }

    public ChatUtils$TextBuilder appendText(Text text) {
        this.end();
        this.empty.append((Text) (this.style.isEmpty() ? text : text.copy().styled(s -> s.withParent(this.style))));
        return this;
    }

    public ChatUtils$TextBuilder withObfuscated(@Nullable Boolean obfuscated) {
        return this.withStyle(this.style.withObfuscated(obfuscated));
    }

    public ChatUtils$TextBuilder with(char[] chars) {
        this.builder.append(chars);
        return this;
    }

    public ChatUtils$TextBuilder withContent(TextContent content) {
        content.visit(this, this.style.withParent(Style.EMPTY));
        return this;
    }

    public ChatUtils$TextBuilder with(char[] chars, int offset, int len) {
        this.builder.append(chars, offset, len);
        return this;
    }

    public ChatUtils$TextBuilder withItalic(@Nullable Boolean italic) {
        return this.withStyle(this.style.withItalic(italic));
    }

    public ChatUtils$TextBuilder withText(StringVisitable text) {
        text.visit(this, Style.EMPTY);
        return this;
    }

    public ChatUtils$TextBuilder withClickEvent(@Nullable ClickEvent clickEvent) {
        return this.withStyle(this.style.withClickEvent(clickEvent));
    }

    public ChatUtils$TextBuilder withColorString(String value) {
        return value == null ? this : this.withLegacy(ChatUtils.translateAlternateColorCodes('&', '§', value));
    }

    public ChatUtils$TextBuilder with(String string) {
        this.builder.append(string);
        return this;
    }

    public ChatUtils$TextBuilder with(CharSequence cs) {
        this.builder.append(cs);
        return this;
    }

    public ChatUtils$TextBuilder withColor(@Nullable Formatting color) {
        return this.withStyle(this.style.withColor(color));
    }

    public Optional<Unit> accept(Style style, String asString) {
        this.withStyle(style).with(asString);
        return Optional.empty();
    }

    public ChatUtils$TextBuilder with(boolean b) {
        this.builder.append(b);
        return this;
    }

    public ChatUtils$TextBuilder() {
        this.empty = Text.empty();
        this.builder = new StringBuilder();
    }

    public ChatUtils$TextBuilder withFormatting(Formatting formatting) {
        return this.withStyle(this.style.withFormatting(formatting));
    }

    public ChatUtils$TextBuilder withUnderline(@Nullable Boolean underline) {
        return this.withStyle(this.style.withUnderline(underline));
    }

    public ChatUtils$TextBuilder withReset(Formatting color, boolean hasReset) {
        Style previous = this.style;
        Style currentStyle = (!hasReset ? ChatUtils.d : ChatUtils.c).withColor(color);
        if (previous.isBold()) {
            currentStyle = currentStyle.withBold(false);
        }

        if (previous.isItalic()) {
            currentStyle = currentStyle.withItalic(false);
        }

        if (previous.isObfuscated()) {
            currentStyle = currentStyle.withObfuscated(false);
        }

        if (previous.isStrikethrough()) {
            currentStyle = currentStyle.withStrikethrough(false);
        }

        if (previous.isUnderlined()) {
            currentStyle = currentStyle.withUnderline(false);
        }

        return this.withStyle(currentStyle);
    }

    public ChatUtils$TextBuilder withGlobal(Style parent) {
        this.empty.setStyle(this.empty.getStyle().withParent(parent));
        return this;
    }

    public ChatUtils$TextBuilder end() {
        this.write();
        return this;
    }

    public ChatUtils$TextBuilder withFormatting(Formatting... formattings) {
        return this.withStyle(this.style.withFormatting(formattings));
    }

    public ChatUtils$TextBuilder withInsertion(@Nullable String insertion) {
        return this.withStyle(this.style.withInsertion(insertion));
    }

    public ChatUtils$TextBuilder withBold(@Nullable Boolean bold) {
        return this.withStyle(this.style.withBold(bold));
    }

    public ChatUtils$TextBuilder withExclusiveFormatting(Formatting formatting) {
        return this.withStyle(this.style.withExclusiveFormatting(formatting));
    }

    public ChatUtils$TextBuilder with(float f) {
        this.builder.append(f);
        return this;
    }

    public ChatUtils$TextBuilder with(char c) {
        this.builder.append(c);
        return this;
    }

    public boolean accept(int index, Style style, int codePoint) {
        this.withStyle(style).with(codePoint);
        return true;
    }

    private void write() {
        if (!this.builder.isEmpty()) {
            String str = this.builder.toString();
            this.builder = new StringBuilder();
            this.empty.append(Text.literal(str).setStyle(this.style));
        }
    }

    public ChatUtils$TextBuilder withHoverEvent(@Nullable HoverEvent hoverEvent) {
        return this.withStyle(this.style.withHoverEvent(hoverEvent));
    }

    public MutableText build() {
        MutableText text = this.empty;
        this.empty = Text.empty();
        return text;
    }

    public ChatUtils$TextBuilder withText(StringVisitable text, Style style) {
        text.visit(this, style);
        return this;
    }

    public ChatUtils$TextBuilder withLine() {
        this.builder.append('\n');
        return this;
    }
}
