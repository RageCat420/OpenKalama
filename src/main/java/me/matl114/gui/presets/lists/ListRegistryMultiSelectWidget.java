package me.matl114.gui.presets.lists;

import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.matl114.gui.FilterService;
import me.matl114.gui.KalamaHelperHelperK;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.presets.single.RegistryDisplays;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import oshi.util.tuples.Triplet;

public class ListRegistryMultiSelectWidget<T> extends KalamaHelperHelperI<Triplet<Text, Identifier, T>> {
   private static final KalamaHelperHelperK<Triplet<Text, Identifier, Object>> filter = (s, b, bl) -> {
      if (!bl) {
         String var3 = ((Identifier)s.getB()).toString();
         if (FilterService.nameMatch(var3, b)) {
            return true;
         } else {
            String var4 = ((Text)s.getA()).getString();
            return FilterService.nameMatch(var4, b);
         }
      } else {
         try {
            return Pattern.matches(b, ((Identifier)s.getB()).getPath()) || Pattern.matches(b, ((Text)s.getA()).getString());
         } catch (Throwable var5) {
            return false;
         }
      }
   };

   private ListRegistryMultiSelectWidget(
      Pair<List<Triplet<Text, Identifier, T>>, Set<Triplet<Text, Identifier, T>>> pairData,
      BiFunction<Triplet<Text, Identifier, T>, AttrKeyValue<Boolean>, RenderHandler> renderFactory,
      ValueAccessor<String> filterInput,
      int x,
      int y,
      int dx,
      int dy,
      int height
   ) {
      super(
         (List<Triplet<Text, Identifier, T>>)pairData.getFirst(),
         (Set<Triplet<Text, Identifier, T>>)pairData.getSecond(),
         renderFactory,
         filterInput,
         (KalamaHelperHelperK<Triplet<Text, Identifier, T>>)(KalamaHelperHelperK)filter,
         x,
         y,
         dx,
         dy,
         height
      );
   }

   public Set<T> getSelectedRegistries() {
      return this.eh().stream().<T>map(Triplet::getC).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public static <T> ListRegistryMultiSelectWidget<T> registry(
      Registry<T> registry, Set<T> currentSelect, ValueAccessor<String> filterInput, int x, int y, int dx, int dy, int height
   ) {
      return new ListRegistryMultiSelectWidget<>(
         registry,
         currentSelect,
         RegistryDisplays::getDisplay,
         (trp, attr) -> RegistryDisplays.of(registry, trp.getC(), (Text)trp.getA(), (Identifier)trp.getB()),
         filterInput,
         x,
         y,
         dx,
         dy,
         height
      );
   }

   public ListRegistryMultiSelectWidget(
      Registry<T> registry,
      Set<T> currentSelection,
      Function<T, Text> localization,
      BiFunction<Triplet<Text, Identifier, T>, AttrKeyValue<Boolean>, RenderHandler> renderFactory,
      ValueAccessor<String> filterInput,
      int x,
      int y,
      int dx,
      int dy,
      int height
   ) {
      this(buildPairInternal(registry, currentSelection, localization), renderFactory, filterInput, x, y, dx, dy, height);
   }

   private static <T> Pair<List<Triplet<Text, Identifier, T>>, Set<Triplet<Text, Identifier, T>>> buildPairInternal(
      Registry<T> registry, Set<T> currentSelection, Function<T, Text> localization
   ) {
      HashSet var3 = new HashSet();
      List var4 = registry.stream().map(s -> new Triplet((Text)localization.apply(s), registry.getId(s), s)).peek(s -> {
         if (currentSelection.contains(s.getC())) {
            var3.add(s);
         }
      }).toList();
      return new Pair(var4, var3);
   }
}
