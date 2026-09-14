package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;

public record Vec3(double x, double y, double z) implements NBTParsable<Vec3> {
   public static final NBTType<Vec3> TYPE = new NBTType<>(
      "vec3",
      RecordCodecBuilder.create(
         s -> s.group(Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x), Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y), Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z))
            .apply(s, Vec3::new)
      ),
      (s, x, y, dx, dy) -> {
         KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
         int half = dx / 3;
         WrapperFactory<Double, Vec3> firstWrapper = WrapperFactory.of(d -> ((Vec3)s.getOriginValue()).withX(d), Vec3::x);
         WrapperFactory<Double, Vec3> secondWrapper = WrapperFactory.of(d -> ((Vec3)s.getOriginValue()).withY(d), Vec3::y);
         WrapperFactory<Double, Vec3> thirdWrapper = WrapperFactory.of(d -> ((Vec3)s.getOriginValue()).withZ(d), Vec3::z);
         return subScreenWidget.Q(new TypeConvertAttrKeyValue<>(s, firstWrapper, NBTTypes.e).generateValueWidget(0, 0, half, dy))
            .Q(new TypeConvertAttrKeyValue<>(s, secondWrapper, NBTTypes.e).generateValueWidget(half, 0, half, dy))
            .Q(new TypeConvertAttrKeyValue<>(s, thirdWrapper, NBTTypes.e).generateValueWidget(2 * half, 0, half, dy));
      },
      new Vec3(0.0, 0.0, 0.0)
   );

   @Override
   public NBTType<Vec3> type() {
      return TYPE;
   }

   public Vec3 withX(double x) {
      return this.x == x ? this : new Vec3(x, this.y, this.z);
   }

   public Vec3 withY(double y) {
      return this.y == y ? this : new Vec3(this.x, y, this.z);
   }

   public Vec3 withZ(double z) {
      return this.z == z ? this : new Vec3(this.x, this.y, z);
   }
}
