package me.matl114.gui.presets.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

class KalamaHelperHelperJ<T> implements ListEntryWidgetController {
   final List<T> b;

   @Override
   public boolean g(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean j() {
      throw new UnsupportedOperationException();
   }

   KalamaHelperHelperJ(List var1, Function var2, int var3, int var4) {
      this.b = var1;
      this.g = var2;
      this.d = var3;
      this.e = var4;
      this.b = new ArrayList();

      for (Object var6 : this.b) {
         this.b.add((Element)(Object)this.g.apply(var6));
      }
   }

   @Override
   public <T extends Element & Drawable & Selectable> T getEntryWidget(int index) {
      return this.b.get(index);
   }

   @Override
   public void markDirty(boolean mark) {
   }

   @Override
   public boolean i() {
      return false;
   }

   @Override
   public void resync() {
   }

   @Override
   public boolean f(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean h(int index) {
      if (index >= 0 && index < this.a()) {
         this.b.set(index, (Element)(Object)this.g.apply(this.b.get(index)));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean d(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int a() {
      return this.b.size();
   }

   @Override
   public boolean e(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int c() {
      return this.e;
   }

   @Override
   public int b() {
      return this.d;
   }
   Function g;
   int d;
   int e;
}
