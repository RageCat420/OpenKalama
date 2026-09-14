package me.matl114.hacks;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;

public record KalamaHelperHelperIX(
   MutableObject<Vec3d> from, MutableObject<Vec3d> tickFirstGoodVec, AtomicInteger currentTokenLimit, AtomicInteger currentTokenInTick
) {
   public AtomicInteger currentTokenLimit() {
      return this.currentTokenLimit;
   }

   public MutableObject<Vec3d> from() {
      return this.from;
   }

   public AtomicInteger currentTokenInTick() {
      return this.currentTokenInTick;
   }


   public static KalamaHelperHelperIX create(Vec3d from) {
      return new KalamaHelperHelperIX(new MutableObject(from), new MutableObject(from), new AtomicInteger(0), new AtomicInteger(0));
   }

   public MutableObject<Vec3d> tickFirstGoodVec() {
      return this.tickFirstGoodVec;
   }

   public KalamaHelperHelperIX mS() {
      this.currentTokenInTick.set(0);
      this.currentTokenLimit.set(0);
      this.tickFirstGoodVec.setValue((Vec3d)(Object)this.from.getValue());
      return this;
   }
}
