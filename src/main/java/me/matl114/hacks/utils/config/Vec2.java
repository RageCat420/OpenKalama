package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.PairLikeFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.util.math.Vec2f;

public record Vec2(double x, double y) implements NBTParsable<Vec2> {
   public static final PairLikeFactory<Double, Double, Vec2> PAIR_FACTORY = PairLikeFactory.of(Vec2::new, Vec2::x, Vec2::y);
   public static final NBTType<Vec2> TYPE = new NBTType<>(
      "vec2",
      RecordCodecBuilder.create(s -> s.group(Codec.DOUBLE.fieldOf("x").forGetter(Vec2::x), Codec.DOUBLE.fieldOf("y").forGetter(Vec2::y)).apply(s, Vec2::new)),
      (s, x, y, dx, dy) -> {
         KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
         int half = dx / 2;
         return subScreenWidget.Q(
               new TypeConvertAttrKeyValue<>(s, PAIR_FACTORY.asFirstWrapper(s::getOriginValue), NBTTypes.e).generateValueWidget(0, 0, half, dy)
            )
            .Q(new TypeConvertAttrKeyValue<>(s, PAIR_FACTORY.asSecondWrapper(s::getOriginValue), NBTTypes.e).generateValueWidget(half, 0, half, dy));
      },
      new Vec2(0.0, 0.0)
   );

   public Vec2f toVec2f() {
      return new Vec2f((float)this.x, (float)this.y);
   }

   @Override
   public NBTType<Vec2> type() {
      return TYPE;
   }
}
