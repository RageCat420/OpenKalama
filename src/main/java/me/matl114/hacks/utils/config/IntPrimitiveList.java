package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.IntListAttrKeyValue;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;

public class IntPrimitiveList extends PrimitiveList<Integer> {
   public static final WrapperFactory<String, IntPrimitiveList> JQ = IntPrimitiveList.JP.concat(WrapperFactory.of(IntPrimitiveList::new, PrimitiveList::list));
   public static final WrapperFactory<String, List<Integer>> JP = WrapperFactory.of(str -> {
      String[] var1 = str.split(",", -1);
      ArrayList var2 = new ArrayList();

      for (String var6 : var1) {
         var2.add(Integer.parseInt(var6));
      }

      return var2;
   }, arr -> arr.stream().map(String::valueOf).collect(Collectors.joining(",")));
   public static final NBTType<IntPrimitiveList> TYPE = new NBTType<>(
      "intprimitivelist",
      JQ.wrapCodecComapFlatMap(Codec.STRING),
      (s, x, y, dx, dy) -> new KalamaHelperHelperCX(x, y, dx, dy)
         .Q(new TypeConvertAttrKeyValue<>(s, JQ, NBTTypes.g).generateValueWidget(0, 0, dx - dy, dy))
         .Q(
            WidgetUtils.l(
               () -> new IntListAttrKeyValue(s.getKeyName(), ((IntPrimitiveList)s.getOriginValue()).list(), JP),
               lst -> s.valueChangeInternal(null, new IntPrimitiveList(lst)),
               dx - dy,
               0,
               dy,
               dy
            )
         ),
      JQ,
      new IntPrimitiveList(List.of())
   );

   @Override
   protected PrimitiveList<Integer> withDefault(List<Integer> list, Optional<Primitive<Integer>> defaultPrimitive) {
      return new IntPrimitiveList(list, defaultPrimitive);
   }

   public IntPrimitiveList(List<Integer> list) {
      super(NBTTypes.c, list);
   }

   @Override
   public NBTType<PrimitiveList<Integer>> type() {
      return TYPE.cast();
   }

   protected IntPrimitiveList(List<Integer> list, Optional<Primitive<Integer>> defaultPrimitive) {
      super(NBTTypes.c, list, defaultPrimitive);
   }
}
