package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ChatUtils$TextBuilder;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class StringFormat implements NBTParsable<StringFormat> {
   final List<String> formattingArgument;
   final String formatString;
   final boolean colorString;
   BiConsumer<Map<String, String>, Consumer<Object>> cachedFormatter;
   public static NBTType<StringFormat> TYPE = new NBTType<>(
      "stringformat",
      RecordCodecBuilder.create(
         oInstance -> oInstance.group(
               Codec.list(Codec.STRING).fieldOf("arguments").forGetter(StringFormat::formattingArgument),
               Codec.STRING.fieldOf("format").forGetter(StringFormat::formatString),
               Codec.BOOL.optionalFieldOf("color_str", false).forGetter(StringFormat::colorString)
            )
            .apply(oInstance, StringFormat::new)
      ),
      (s, x, y, dx, dy) -> {
         StringFormat original = (StringFormat)s.getOriginValue();
         AttrKeyValue<String> wrapper = new TypeConvertAttrKeyValue<>(s, WrapperFactory.of(original::withFormatString, StringFormat::formatString), NBTTypes.g);
         KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
         boolean hasFormatArgument = !original.formattingArgument().isEmpty();
         int width = dx;
         if (hasFormatArgument) {
            width = dx - dy;
         }

         if (original.colorString()) {
            width -= dy;
         }

         subScreenWidget.Q(wrapper.generateValueWidget(0, 0, width, dy));
         if (hasFormatArgument) {
            subScreenWidget.Q(
               new ExecutableWidget(width, 0, dy, dy)
                  .eV(IconElement.cm(KalamaHelperHelperB.e, ButtonAction.c()).aO(TooltipHandler.ap(generateTooltipsForArgument(original.formattingArgument()))))
            );
         }

         if (original.colorString()) {
            subScreenWidget.Q(
               new ExecutableWidget(width + dy, 0, dy, dy)
                  .eV(
                     IconElement.cm(KalamaHelperHelperB.g, ButtonAction.a(StringFormat::openWikiColorString))
                        .aO(TooltipHandler.aq(el -> ((StringFormat)s.getOriginValue()).generateColorStringPreview()))
                  )
            );
         }

         return subScreenWidget;
      },
      new StringFormat(List.of(), "")
   );
   public static final String URL1 = "https://zh.minecraft.wiki/w/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81";
   public static final String URL2 = "https://mcg.tuanzi.ink/";
   public static final Pattern pattern = Pattern.compile("\\{[^{}]*\\}");

   public StringFormat(List<String> f1, String f2) {
      this(f1, f2, false);
   }

   public StringFormat(List<String> f1, String f2, boolean colorString) {
      this.formattingArgument = f1;
      this.formatString = f2;
      this.colorString = colorString;
   }

   public StringFormat withFormatString(String formatString) {
      return new StringFormat(this.formattingArgument, formatString, this.colorString);
   }

   public static List<Text> generateTooltipsForArgument(List<String> formattingArgument) {
      List<Text> tooltips = new ArrayList<>(ChatUtils.parseTranslation("widget.nbt-parsable.string-format.argument-info.tooltips", ""));

      for (String re : formattingArgument) {
         tooltips.add(Text.literal("- {%s}".formatted(re)));
      }

      return tooltips;
   }

   public static void openWikiColorString() {
      Util.getOperatingSystem().open("https://zh.minecraft.wiki/w/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81");
      Util.getOperatingSystem().open("https://mcg.tuanzi.ink/");
   }

   public List<Text> generateColorStringPreview() {
      List<Text> tooltips = new ArrayList<>(ChatUtils.parseTranslation("widget.nbt-parsable.string-format.color-string-info.tooltips", ""));
      tooltips.add(this.formatText());
      return tooltips;
   }

   @Override
   public NBTType<StringFormat> type() {
      return TYPE.cast();
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return type instanceof StringFormat
         && ((StringFormat)type).formattingArgument().equals(this.formattingArgument())
         && ((StringFormat)type).colorString() == this.colorString;
   }

   @Override
   public <W> Optional<StringFormat> tryTypeConvert(Ref<W> ref) {
      return ref instanceof NBTRef nbtRef && nbtRef.get() instanceof StringFormat format
         ? Optional.of(this.withFormatString(format.formatString()))
         : Optional.empty();
   }

   private <T> BiConsumer<Map<String, T>, Consumer<T>> construct0() {
      if (this.cachedFormatter == null) {
         Matcher matcher = pattern.matcher(this.formatString);
         int lastEnd = 0;

         List<BiConsumer<Consumer<Object>, Map<String, String>>> sequenceBuilders;
         for (sequenceBuilders = new ArrayList<>(); matcher.find(); lastEnd = matcher.end()) {
            String lastSeq = this.formatString.substring(lastEnd, matcher.start());
            sequenceBuilders.add((a, b) -> a.accept(lastSeq));
            String placeholder = matcher.group();
            if (placeholder.length() <= 2) {
               sequenceBuilders.add((a, b) -> a.accept(placeholder));
            } else {
               String key = placeholder.substring(1, placeholder.length() - 1);
               sequenceBuilders.add((a, b) -> a.accept(b.getOrDefault(key, placeholder)));
            }
         }

         if (lastEnd < this.formatString.length()) {
            String lastSeq = this.formatString.substring(lastEnd);
            sequenceBuilders.add((a, b) -> a.accept(lastSeq));
         }

         this.cachedFormatter = (map, consumer) -> {
            for (BiConsumer<Consumer<Object>, Map<String, String>> re : sequenceBuilders) {
               re.accept(consumer, map);
            }
         };
      }

      return (BiConsumer<Map<String, T>, Consumer<T>>)this.cachedFormatter;
   }

   public String format(String... arguments) {
      int size = Math.min(arguments.length, this.formattingArgument().size());
      Map<String, String> availableMap = new HashMap<>();

      for (int i = 0; i < size; i++) {
         availableMap.put(this.formattingArgument.get(i), arguments[i]);
      }

      return this.format(availableMap);
   }

   public String format(Map<String, String> arguments) {
      BiConsumer<Map<String, String>, Consumer<String>> builder = this.construct0();
      StringBuilder result = new StringBuilder();
      builder.accept(arguments, result::append);
      return result.toString();
   }

   public MutableText formatText(Object... arguments) {
      int size = Math.min(arguments.length, this.formattingArgument().size());
      Map<String, Object> availableMap = new HashMap<>();

      for (int i = 0; i < size; i++) {
         availableMap.put(this.formattingArgument.get(i), arguments[i]);
      }

      return this.formatText(availableMap);
   }

   public MutableText formatText(Map<String, Object> arguments) {
      BiConsumer<Map<String, Object>, Consumer<Object>> builder = this.construct0();
      ChatUtils$TextBuilder result = ChatUtils.builder();
      builder.accept(arguments, obj -> {
         if (obj instanceof Text txt) {
            result.appendText(txt);
         } else {
            result.withColorString(obj == null ? "null" : obj.toString());
         }
      });
      return result.end().build();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof StringFormat format)
            ? false
            : Objects.equals(format.formatString, this.formatString)
               && Objects.equals(format.formattingArgument, this.formattingArgument)
               && Objects.equals(format.colorString, this.colorString);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.formatString, this.formattingArgument, this.colorString);
   }

   public List<String> formattingArgument() {
      return this.formattingArgument;
   }

   public String formatString() {
      return this.formatString;
   }

   public boolean colorString() {
      return this.colorString;
   }
}
