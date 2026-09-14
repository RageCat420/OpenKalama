package me.matl114.gui.presets.lists;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import me.matl114.gui.FilterService;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.presets.single.RegistryDisplays;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import oshi.util.tuples.Triplet;

public class ListRegistrySelectWidget<T> extends KalamaHelperHelperD<Triplet<Text, Identifier, T>> {
   private static final BiPredicate<Triplet<Text, Identifier, Object>, String> filter = (s, b) -> {
      String var2 = ((Identifier)s.getB()).toString();
      if (FilterService.nameMatch(var2, b)) {
         return true;
      } else {
         String var3 = ((Text)s.getA()).getString();
         return FilterService.nameMatch(var3, b);
      }
   };

   public T getSelectedRegistry() {
      return (T)(this.bU() == null ? null : this.bU().getC());
   }

   public static <T> ListRegistrySelectWidget<T> registry(Registry<T> registry, ValueAccessor<String> filterInput, int x, int y, int dx, int dy, int height) {
      return new ListRegistrySelectWidget<>(
         list(listRegistry(registry), registry, RegistryDisplays::getDisplay),
         trp -> RegistryDisplays.of(registry, trp.getC(), (Text)trp.getA(), (Identifier)trp.getB()),
         filterInput,
         x,
         y,
         dx,
         dy,
         height
      );
   }

   public static <T> ListRegistrySelectWidget<T> bK(
      List<T> data, Registry<T> registry, ValueAccessor<String> filterInput, int x, int y, int dx, int dy, int height
   ) {
      return new ListRegistrySelectWidget<>(
         list(data, registry, RegistryDisplays::getDisplay),
         trp -> RegistryDisplays.of(registry, trp.getC(), (Text)trp.getA(), (Identifier)trp.getB()),
         filterInput,
         x,
         y,
         dx,
         dy,
         height
      );
   }

   public static <T> List<Triplet<Text, Identifier, T>> list(List<T> lst, Registry<T> registry, Function<T, Text> localization) {
      return lst.stream().map(s -> new Triplet<Text, net.minecraft.util.Identifier, T>((Text)localization.apply(s), registry.getId(s), s)).toList();
   }

   public ListRegistrySelectWidget(
      List<Triplet<Text, Identifier, T>> list,
      Function<Triplet<Text, Identifier, T>, RenderHandler> renderFactory,
      ValueAccessor<String> filterInput,
      int x,
      int y,
      int dx,
      int dy,
      int height
   ) {
      super(list, renderFactory, filterInput, (BiPredicate<Triplet<Text, Identifier, T>, String>)(BiPredicate)filter, x, y, dx, dy, height);
   }

   public static <T> List<T> listRegistry(Registry<T> registry) {
      return registry.stream().toList();
   }
}
