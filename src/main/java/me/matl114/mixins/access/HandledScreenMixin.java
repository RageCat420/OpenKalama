package me.matl114.mixins.access;

import me.matl114.accessors.access.HandledScreenAccess;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({HandledScreen.class})
public class HandledScreenMixin extends Screen implements HandledScreenAccess {
   @Shadow
   protected int field_2776;
   @Shadow
   protected int field_2800;

   protected HandledScreenMixin(Text title) {
      super(title);
   }

   @Shadow
   protected abstract Slot method_2386(double var1, double var3);

   @Unique
   @Override
   public Slot reallyGetSlotAt(double var1, double var3) {
      return this.method_2386(var1, var3);
   }

   @Accessor("x")
   @Override
   public int getScreenX() { }

   @Accessor("y")
   @Override
   public int getScreenY() { }

   @Accessor("backgroundWidth")
   @Override
   public int getScreenBackgroundX() { }

   @Accessor("backgroundHeight")
   @Override
   public int getScreenBackgroundY() { }
}
