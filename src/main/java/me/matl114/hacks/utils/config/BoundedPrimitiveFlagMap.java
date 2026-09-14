package me.matl114.hacks.utils.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.CodecUtils;
import net.minecraft.text.Text;
import org.apache.commons.lang3.function.TriFunction;

public class BoundedPrimitiveFlagMap<E extends Enum<E>> extends BoundedPrimitiveMap<E, Boolean> {
   public boolean getState(E element) {
      return this.map.get(element);
   }

   public BoundedPrimitiveFlagMap(List<E> keys, Map<E, Boolean> map, NBTType<Boolean> type) {
      super(keys, map, type);
   }

   public static <S extends Enum<S>, T extends BoundedPrimitiveFlagMap<S>> NBTType<T> createEnumMap(
      String clazzT, Class<S> enumS, TriFunction<List<S>, Map<S, Boolean>, NBTType<Boolean>, T> creator
   ) {
      return create(clazzT, creator, Arrays.asList((Enum[])enumS.getEnumConstants()), CodecUtils.enumCodec(enumS), (v, x, y, width, height) -> {
         short var5 = 180;
         int var6 = (width - var5) / 2;
         return ExecutableWidget.instance(x + var6, y, var5, height).eV(new ButtonElement(TextProvider.c(Text.literal(v.name())), ButtonAction.c()));
      }, NBTTypes.f, 250, 320, 20);
   }

   public BoundedPrimitiveFlagMap(Class<E> clazz) {
      this(Arrays.asList((E[])clazz.getEnumConstants()), Map.of(), NBTTypes.f);
   }
}
