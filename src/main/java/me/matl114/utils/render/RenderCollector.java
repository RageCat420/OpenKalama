package me.matl114.utils.render;

import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.util.math.MatrixStack;

public interface RenderCollector<B> {
   void submit(B var1, int var2);

   void b(VDrawContext var1);

   void clear();

   void a(MatrixStack var1);

   default void a(MatrixStack var1) {  }

}
