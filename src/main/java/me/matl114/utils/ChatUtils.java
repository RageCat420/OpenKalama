package me.matl114.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.chat.SimpleOrderedTextVisitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextColor;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatUtils {
   public static final Style d = Style.EMPTY.withBold(false).withItalic(false).withUnderline(false).withStrikethrough(false).withObfuscated(false);
   public static final Map<Character, Formatting> e;
   public static final Style c = Style.EMPTY.withItalic(false);
   private static final Map<TextColor, Formatting> f;
   private static final MinecraftClient a = MinecraftClient.getInstance();
   private static final Gson g;
   public static final Pattern b = Pattern.compile("(§[0-9a-fk-orx])|(\\n)", 2);

   public static boolean b(char c) {
      return c >= '\udc00' && c <= '\udfff';
   }

   static {
      Builder var0 = ImmutableMap.builder();

      for (Formatting var4 : Formatting.values()) {
         var0.put(Character.toLowerCase(var4.toString().charAt(1)), var4);
      }

      e = var0.build();
      f = new HashMap<>();
      TextColor.FORMATTING_TO_COLOR.forEach((f, t) -> ChatUtils.f.put(t, f));
      g = new GsonBuilder().disableHtmlEscaping().create();
   }

   @Modifiable
   public static MutableText y(String literal) {
      String var1 = "[%s]".formatted(literal);
      return z(var1, literal);
   }

   @Modifiable
   public static ChatUtils$TextBuilder builder() {
      return new ChatUtils$TextBuilder();
   }

   @Modifiable
   public static Text getDisplayedLong(long l) {
      return Text.literal("[" + Long.toString(l) + "]")
         .setStyle(
            Style.EMPTY
               .withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, Long.toString(l)))
               .withHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, Text.literal("click to copy coord")))
         )
         .formatted(Formatting.GREEN);
   }

   @Modifiable
   public static List<Text> parseTranslation(String key, String defaultVal) {
      String var2 = Language.getInstance().get(key, defaultVal);
      if (var2 != null && !var2.isEmpty()) {
         String[] var3 = var2.split("\n");
         return Arrays.stream(var3).<Object>map(Text::literal).map(Text.class::cast).toList();
      } else {
         return List.of();
      }
   }

   @Modifiable
   public static Text textFromJsonString(String jsonRaw) {
      try {
         if (jsonRaw == null) {
            return null;
         } else {
            JsonElement var1 = JsonParser.parseString(jsonRaw);
            return var1 == null
               ? null
               : (Text)TextCodecs.CODEC.parse(ItemStackUtils.registry().getOps(JsonOps.INSTANCE), var1).getOrThrow(JsonParseException::new);
         }
      } catch (Throwable var2) {
         return null;
      }
   }

   @Modifiable
   public static Text getDisplayedLocationDouble(double x, double y, double z) {
      String var6 = "";
      if (a.player != null) {
         int var7 = (int)MathUtils.j(x - a.player.getX());
         int var8 = (int)MathUtils.j(z - a.player.getZ());
         var6 = "\n" + MathUtils.getDirectionName(var7, var8) + " X" + (var7 >= 0 ? "+" : "-") + "Z" + (var8 >= 0 ? "+" : "-");
      }

      return Text.literal("[%d,%d,%d]".formatted((int)x, (int)y, (int)z))
         .setStyle(
            Style.EMPTY
               .withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, "%.2f %.2f %.2f".formatted(x, y, z)))
               .withHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, Text.literal("click to copy coord" + var6)))
         )
         .formatted(Formatting.GREEN);
   }

   @Modifiable
   public static String m(String str) {
      return str.replaceAll("§.", "");
   }

   public static char toFullWidth(char c) {
      if (c >= 'a' && c <= 'z') {
         return (char)(c + 'ﻠ');
      } else if (c >= 'A' && c <= 'Z') {
         return (char)(c + 'ﻠ');
      } else {
         return c >= '0' && c <= '9' ? (char)(c + 'ﻠ') : c;
      }
   }

   @Modifiable
   public static Stream<Text> textStream(Text comp) {
      return Streams.concat(new Stream[]{Stream.of(comp), comp.getSiblings().stream().flatMap(ChatUtils::textStream)});
   }

   @Modifiable
   public static MutableText getHoverShowText(String literal, List<Text> showText) {
      return Text.literal(literal)
         .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, concatLineText(showText))));
   }

   @Modifiable
   public static MutableText textFromLegacyString(String origin) {
      try {
         String var1 = translateAlternateColorCodes('&', '§', origin);
         return f(var1);
      } catch (Throwable var2) {
         return Text.empty();
      }
   }

   public static int cutStringWithWidth(String string, Style style, int limit, MutableFloat widthCounter) {
      int var4 = string.length();

      for (int var5 = 0; var5 < var4; var5++) {
         int var6 = string.codePointAt(var5);
         OrderedText var7 = OrderedText.styled(var6, style);
         float var8 = a.textRenderer.getTextHandler().getWidth(var7);
         if (widthCounter.getValue() + var8 > limit) {
            return var5;
         }

         widthCounter.add(var8);
      }

      return var4;
   }

   @Modifiable
   public static ClickEvent getClickCopyText(String copy) {
      return new ClickEvent(Action.COPY_TO_CLIPBOARD, copy);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Modifiable
   public static List<Text> multiLineTextFromLegacyString(String value, int widthLimit) {
      if (value == null) {
         return List.of();
      } else {
         ArrayList var2 = new ArrayList();
         MutableFloat var3 = new MutableFloat(0.0);
         ChatUtils$TextBuilder var4 = new ChatUtils$TextBuilder();
         Matcher var5 = b.matcher(value);
         String var6 = null;
         StringBuilder var7 = null;
         int var8 = 0;
         boolean var9 = false;
         boolean var10 = false;

         while (var5.find()) {
            int var11 = 0;

            while ((var6 = var5.group(++var11)) == null) {
            }

            int var12 = var5.start(var11);
            if (var12 > var8) {
               String var13 = value.substring(var8, var12);
               var10 = false;

               while (true) {
                  int var14 = cutStringWithWidth(var13, var4.currentStyle(), widthLimit, var3);
                  if (var14 == var13.length()) {
                     var4.with(var13);
                     var8 = var12;
                     break;
                  }

                  if (var14 != 0) {
                     var4.with(var13.substring(0, var14));
                  }

                  var2.add(var4.end().build());
                  var3.setValue(0.0F);
                  var13 = var13.substring(var14);
               }
            }

            switch (var11) {
               case 1:
                  char var19 = var6.toLowerCase(Locale.ENGLISH).charAt(1);
                  if (var19 == 'x') {
                     var7 = new StringBuilder("#");
                  } else if (var7 != null) {
                     var7.append(var19);
                     if (var7.length() == 7) {
                        var4.withStyle(d.withColor((TextColor)TextColor.parse(var7.toString()).result().get()));
                        var7 = null;
                     }
                  } else {
                     Formatting var22 = e.get(var19);
                     if (var22.isModifier() && var22 != Formatting.RESET) {
                        switch (var22) {
                           case BOLD:
                              var4.withBold(true);
                              break;
                           case ITALIC:
                              var4.withItalic(true);
                              break;
                           case STRIKETHROUGH:
                              var4.withStrikethrough(true);
                              break;
                           case UNDERLINE:
                              var4.withUnderline(true);
                              break;
                           case OBFUSCATED:
                              var4.withObfuscated(true);
                              break;
                           default:
                              throw new AssertionError("Unexpected message format");
                        }
                     } else {
                        var4.withReset(var22, var9);
                     }
                  }

                  var10 = true;
                  break;
               case 2:
                  if (var10) {
                     String var21 = value.substring(var8, var12);

                     while (true) {
                        int var15 = cutStringWithWidth(var21, var4.currentStyle(), widthLimit, var3);
                        if (var15 == var21.length()) {
                           var4.with(var21);
                           break;
                        }

                        if (var15 != 0) {
                           var4.with(var21.substring(0, var15));
                        }

                        var2.add(var4.end().build());
                        var3.setValue(0.0F);
                        var21 = var21.substring(var15);
                     }
                  }

                  var2.add(var4.end().build());
                  var3.setValue(0.0F);
                  var10 = false;
            }

            var8 = var5.end(var11);
         }

         int var17 = value.length();
         if (var8 < value.length() || var10) {
            String var18 = value.substring(var8, var17);

            while (true) {
               int var20 = cutStringWithWidth(var18, var4.currentStyle(), widthLimit, var3);
               if (var20 == var18.length()) {
                  var4.with(var18);
                  var2.add(var4.end().build());
                  break;
               }

               if (var20 != 0) {
                  var4.with(var18.substring(0, var20));
               }

               var2.add(var4.end().build());
               var3.setValue(0.0F);
               var18 = var18.substring(var20);
            }
         }

         return var2;
      }
   }

   @Modifiable
   public static Text getDisplayedLocation(Vec3d vec3d) {
      return v(vec3d.x, vec3d.y, vec3d.z);
   }

   @Modifiable
   public static List<Text> splitToMultiLineText(Text text, int widthLimit) {
      ArrayList var2 = new ArrayList();
      ChatUtils$TextBuilder var3 = new ChatUtils$TextBuilder();
      MutableFloat var4 = new MutableFloat(0.0);
      text.visit((style, asbString) -> {
         var3.withStyle(style);

         while (true) {
            int var6 = asbString.indexOf(10);
            String var7;
            if (var6 == -1) {
               var7 = asbString;
            } else {
               var7 = asbString.substring(0, var6);
               asbString = asbString.substring(var6 + 1);
            }

            while (true) {
               int var8 = cutStringWithWidth(var7, style, widthLimit, var4);
               if (var8 == var7.length()) {
                  var3.with(var7);
                  if (var6 == -1) {
                     return Optional.empty();
                  }

                  var2.add(var3.end().build());
                  var4.setValue(0.0F);
                  break;
               }

               if (var8 != 0) {
                  var3.with(var7.substring(0, var8));
               }

               var2.add(var3.end().build());
               var4.setValue(0.0F);
               var7 = var7.substring(var8);
            }
         }
      }, Style.EMPTY);
      if (var4.floatValue() > 0.0F) {
         var2.add(var3.end().build());
      }

      return var2;
   }

   @Modifiable
   public static Text v(double x, double y, double z) {
      String var6 = "";
      if (a.player != null) {
         int var7 = (int)MathUtils.j(x - a.player.getX());
         int var8 = (int)MathUtils.j(z - a.player.getZ());
         var6 = "\n" + MathUtils.getDirectionName(var7, var8) + " X" + (var7 >= 0 ? "+" : "-") + "Z" + (var8 >= 0 ? "+" : "-");
      }

      return Text.literal("[%.2f,%.2f,%.2f]".formatted(x, y, z))
         .setStyle(
            Style.EMPTY
               .withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, "%.2f %.2f %.2f".formatted(x, y, z)))
               .withHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, Text.literal("click to copy coord" + var6)))
         )
         .formatted(Formatting.GREEN);
   }

   @Modifiable
   public static String textToPlainString(Text component) {
      if (component == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         MutableBoolean var2 = new MutableBoolean(false);
         component.visit((style, str) -> {
            TextColor var5 = style.getColor();
            if (var5 != null) {
               if (var5 != null) {
                  Formatting var6 = f.get(var5);
                  if (var6 != null) {
                     var1.append(var6);
                  } else {
                     var1.append('§').append("x");

                     for (char var10 : var5.getName().substring(1).toCharArray()) {
                        var1.append('§').append(var10);
                     }
                  }

                  var2.setValue(true);
               } else if (var2.booleanValue()) {
                  var1.append("§r");
                  var2.setValue(false);
               }
            }

            if (style.isBold()) {
               var1.append(Formatting.BOLD);
               var2.setValue(true);
            }

            if (style.isItalic()) {
               var1.append(Formatting.ITALIC);
               var2.setValue(true);
            }

            if (style.isUnderlined()) {
               var1.append(Formatting.UNDERLINE);
               var2.setValue(true);
            }

            if (style.isStrikethrough()) {
               var1.append(Formatting.STRIKETHROUGH);
               var2.setValue(true);
            }

            if (style.isObfuscated()) {
               var1.append(Formatting.OBFUSCATED);
               var2.setValue(true);
            }

            var1.append(str);
            return Optional.empty();
         }, Style.EMPTY);
         return var1.toString();
      }
   }

   public static String d(String str) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < str.length(); var2++) {
         char var3 = str.charAt(var2);
         var1.append(toFullWidth(var3));
      }

      return var1.toString();
   }

   @Modifiable
   public static HoverEvent G(List<Text> showText) {
      return new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, concatLineText(showText));
   }

   @Modifiable
   public static MutableText copyText(Text text) {
      MutableText var1 = MutableText.of(text.getContent());
      var1.setStyle(text.getStyle());
      text.getSiblings().forEach(var1::append);
      return var1;
   }

   public static String orderedTextToLegacyString(OrderedText... text) {
      SimpleOrderedTextVisitor var1 = new SimpleOrderedTextVisitor();

      for (OrderedText var5 : text) {
         var5.accept(var1);
      }

      return var1.getContent().toString();
   }

   @Modifiable
   public static String translateAlternateColorCodes(char altColorChar, char translateTo, @NotNull String textToTranslate) {
      Preconditions.checkArgument(textToTranslate != null, "Cannot translate null text");
      char[] var3 = textToTranslate.toCharArray();

      for (int var4 = 0; var4 < var3.length - 1; var4++) {
         if (var3[var4] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(var3[var4 + 1]) > -1) {
            var3[var4] = translateTo;
            var3[var4 + 1] = Character.toLowerCase(var3[var4 + 1]);
         }
      }

      return new String(var3);
   }

   @Modifiable
   public static MutableText concatLineText(List<Text> texts) {
      int var1 = texts.size();
      MutableText var2 = Text.empty();

      for (int var3 = 0; var3 < var1; var3++) {
         Text var4 = (Text)texts.get(var3);
         var2.append(var4);
         if (var3 < var1 - 1) {
            var2.append("\n");
         }
      }

      return var2;
   }

   @Modifiable
   public static ClickEvent getRunCommand(String command) {
      return new ClickEvent(Action.RUN_COMMAND, command);
   }

   @Modifiable
   public static Text t(Vec3d vec3d) {
      return getDisplayedLocationDouble(vec3d.x, vec3d.y, vec3d.z);
   }

   @Modifiable
   public static String l(Text component) {
      if (component == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         component.visit((style, asString) -> {
            var1.append(asString);
            return Optional.empty();
         }, Style.EMPTY);
         return var1.toString();
      }
   }

   @Modifiable
   public static String o(OrderedText... text) {
      if (text == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         MutableObject var2 = new MutableObject(null);
         MutableBoolean var3 = new MutableBoolean(false);

         for (OrderedText var7 : text) {
            var7.accept((index, style, codePoint) -> {
               if (!Objects.equals(style, var2.getValue())) {
                  var2.setValue(style);
                  TextColor var7x = style.getColor();
                  if (var7x == null) {
                     if (var3.booleanValue()) {
                        var1.append("§r");
                        var3.setValue(false);
                     }
                  } else {
                     Formatting var8 = f.get(var7x);
                     if (var8 != null) {
                        var1.append(var8);
                     } else {
                        var1.append('§').append("x");

                        for (char var12 : var7x.getName().substring(1).toCharArray()) {
                           var1.append('§').append(var12);
                        }
                     }

                     var3.setValue(true);
                  }

                  if (style.isBold()) {
                     var1.append(Formatting.BOLD);
                     var3.setValue(true);
                  }

                  if (style.isItalic()) {
                     var1.append(Formatting.ITALIC);
                     var3.setValue(true);
                  }

                  if (style.isUnderlined()) {
                     var1.append(Formatting.UNDERLINE);
                     var3.setValue(true);
                  }

                  if (style.isStrikethrough()) {
                     var1.append(Formatting.STRIKETHROUGH);
                     var3.setValue(true);
                  }

                  if (style.isObfuscated()) {
                     var1.append(Formatting.OBFUSCATED);
                     var3.setValue(true);
                  }
               }

               var1.appendCodePoint(codePoint);
               return true;
            });
         }

         return var1.toString();
      }
   }

   public static boolean hasTranslation(String key) {
      return Language.getInstance().hasTranslation(key);
   }

   @Modifiable
   public static ClickEvent getSuggestCommand(String name) {
      return new ClickEvent(Action.SUGGEST_COMMAND, name);
   }

   public static boolean c(char c) {
      return !a(c) && !b(c);
   }

   @Modifiable
   public static String textToLegacyString(Text component) {
      if (component == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         boolean var2 = false;
         Iterator var3 = textStream(component).iterator();

         while (var3.hasNext()) {
            Text var4 = (Text)var3.next();
            Style var5 = var4.getStyle();
            TextColor var6 = var5.getColor();
            if (var4.getContent() != PlainTextContent.EMPTY || var6 != null) {
               if (var6 == null) {
                  if (var2) {
                     var1.append("§r");
                     var2 = false;
                  }
               } else {
                  Formatting var7 = f.get(var6);
                  if (var7 != null) {
                     var1.append(var7);
                  } else {
                     var1.append('§').append("x");

                     for (char var11 : var6.getName().substring(1).toCharArray()) {
                        var1.append('§').append(var11);
                     }
                  }

                  var2 = true;
               }
            }

            if (var5.isBold()) {
               var1.append(Formatting.BOLD);
               var2 = true;
            }

            if (var5.isItalic()) {
               var1.append(Formatting.ITALIC);
               var2 = true;
            }

            if (var5.isUnderlined()) {
               var1.append(Formatting.UNDERLINE);
               var2 = true;
            }

            if (var5.isStrikethrough()) {
               var1.append(Formatting.STRIKETHROUGH);
               var2 = true;
            }

            if (var5.isObfuscated()) {
               var1.append(Formatting.OBFUSCATED);
               var2 = true;
            }

            var4.getContent().visit(x -> {
               var1.append(x);
               return Optional.empty();
            });
         }

         return var1.toString();
      }
   }

   @Modifiable
   public static MutableText f(String value) {
      if (value == null) {
         return Text.empty();
      } else {
         ChatUtils$TextBuilder var1 = new ChatUtils$TextBuilder();
         var1.withLegacy(value);
         return var1.end().build();
      }
   }

   @Modifiable
   @Nullable
   public static String H(String key) {
      return Language.getInstance().get(key, key);
   }

   @Modifiable
   public static String textToJsonString(Text text) {
      if (text == null) {
         return null;
      } else {
         try {
            JsonElement var1 = (JsonElement)TextCodecs.CODEC
               .encodeStart(ItemStackUtils.registry().getOps(JsonOps.INSTANCE), text)
               .getOrThrow(JsonParseException::new);
            return g.toJson(var1);
         } catch (Throwable var2) {
            return null;
         }
      }
   }

   @Modifiable
   public static String q(Text com) {
      try {
         String var1 = textToLegacyString(com);
         return translateAlternateColorCodes('§', '&', var1);
      } catch (Throwable var2) {
         return "";
      }
   }

   @Modifiable
   public static MutableText z(String literal, String copy) {
      return Text.literal(literal)
         .setStyle(
            Style.EMPTY
               .withHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, Text.literal("click to copy text")))
               .withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, copy))
         );
   }

   @Modifiable
   public static ClickEvent getOpenFile(File path) {
      return new ClickEvent(Action.OPEN_FILE, path.getAbsolutePath());
   }

   @Modifiable
   public static Text s(double x, double z) {
      String var4 = "";
      if (a.player != null) {
         int var5 = (int)MathUtils.j(x - a.player.getX());
         int var6 = (int)MathUtils.j(z - a.player.getZ());
         var4 = "\n" + MathUtils.getDirectionName(var5, var6) + " X" + (var5 >= 0 ? "+" : "-") + "Z" + (var6 >= 0 ? "+" : "-");
      }

      return Text.literal("[%.2f,~,%.2f]".formatted(x, z))
         .setStyle(
            Style.EMPTY
               .withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, "%.2f ~ %.2f".formatted(x, z)))
               .withHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, Text.literal("click to copy coord" + var4)))
         )
         .formatted(Formatting.GREEN);
   }

   public static boolean a(char c) {
      return c >= '\ud800' && c <= '\udbff';
   }
}
