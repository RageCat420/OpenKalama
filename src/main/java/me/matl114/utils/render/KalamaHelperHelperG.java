package me.matl114.utils.render;

import java.util.ArrayList;
import java.util.List;
import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.client.util.math.MatrixStack;

public abstract class KalamaHelperHelperG<B> implements RenderCollector<B> {
   protected List<KalamaHelperHelperK<B>> entries = new ArrayList<>();

   @Override
   public void clear() {
      if (!this.entries.isEmpty()) {
         this.entries.clear();
      }
   }

   @Override
   public void submit(B val, int color) {
      this.entries.add(new KalamaHelperHelperK<>(color, (B)val));
   }

   @Override
   public abstract void a(MatrixStack var1);

}
