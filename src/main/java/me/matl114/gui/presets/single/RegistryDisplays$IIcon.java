package me.matl114.gui.presets.single;

import java.util.function.Function;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public interface RegistryDisplays$IIcon<T> {
   RegistryDisplays$IIcon<?> b = (x, y, context, registerValue) -> context.K(RegistryDisplays$IIcon.a, x, y, 999, 0);
   ItemStack a = new ItemStack(Items.BARRIER);

   void b(int var1, int var2, VDrawContext var3, T var4);

   static <T> RegistryDisplays$IIcon<T> c(Function<T, ItemStack> function) {
      return (startIndexX, startIndexY, context, registerValue) -> context.K((ItemStack)function.apply(registerValue), startIndexX, startIndexY, 114514, 0);
   }

   default void a(int startIndexX, int startIndexY, VDrawContext context, T registerValue) {
      if (registerValue == null) {
         context.K(a, startIndexX, startIndexY, 114514, 0);
      } else {
         this.b(startIndexX, startIndexY, context, (T)registerValue);
      }
   }

   static <T> RegistryDisplays$IIcon<T> d(Function<T, ?> function) {
      return (startIndexX, startIndexY, context, registerValue) -> {
         Object var5 = function.apply(registerValue);
         if (var5 instanceof Identifier var6) {
            context.V(var6, startIndexX, startIndexY, 16, 16);
         } else if (var5 instanceof Sprite var7) {
            context.drawSprite(var7, startIndexX, startIndexY, 0, 16, 16);
         }
      };
   }
}
