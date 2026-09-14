package me.matl114.hacks.modules.survival;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;

class SurvivalSubHelperL {
   private final List<SurvivalSubHelperJ> b;
   public final String a;
   private int currentIndex;

   public void f() {
      for (SurvivalSubHelperJ var2 : this.b) {
         var2.n();
      }

      this.currentIndex = 0;
   }

   public List<BlockPos> e() {
      ArrayList var1 = new ArrayList();

      for (int var2 = this.currentIndex; var2 < this.b.size(); var2++) {
         var1.addAll(this.b.get(var2).a());
      }

      return List.copyOf(var1);
   }

   public SurvivalSubHelperJ a() {
      return this.currentIndex >= this.b.size() ? null : this.b.get(this.currentIndex);
   }

   public void replaceCurrentSegment(SurvivalSubHelperJ segment) {
      if (segment != null && this.currentIndex < this.b.size()) {
         this.b.set(this.currentIndex, segment);
      }
   }

   public SurvivalSubHelperL(String pathFile, List<SurvivalSubHelperJ> segments) {
      this.a = pathFile == null ? "" : pathFile;
      this.b = new ArrayList<>(segments);
   }

   public int remainingSegments() {
      return Math.max(0, this.b.size() - this.currentIndex);
   }

   public void c() {
      SurvivalSubHelperJ var1 = this.a();
      if (var1 != null) {
         var1.n();
      }

      this.currentIndex++;
   }
}
