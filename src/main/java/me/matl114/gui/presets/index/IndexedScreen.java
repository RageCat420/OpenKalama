package me.matl114.gui.presets.index;

import java.util.List;
import me.matl114.gui.GenericScreen;
import me.matl114.gui.basic.ElementHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

public class IndexedScreen<T, W extends Element & Drawable & Selectable> extends GenericScreen {
   protected int au = 100;
   protected final List<T> bR;
   protected int ax = 20;
   protected IndexedSubScreen<T, W> bS;

   protected abstract W bd(T var1);

   @Override
   protected void init() {
      super.init();
      this.bc();
      this.dg();
      this.addDrawableChild(this.bS);
   }

   public void bc() { }

   public void setGlobal(T var1) { }

   public void close() {
      super.close();
      this.bc();
   }

   protected void bb() {
      if (this.bS != null) {
         this.bS.selectIndexToDisplay(this.getGlobal(), false);
      }
   }

   public void resize(MinecraftClient client, int width, int height) {
      this.bc();
      super.resize(client, width, height);
   }

   public T getGlobal() { }

   public IndexedScreen(List<T> list, int backgroundWidth, int backgroundHeight) {
      super(Text.empty(), backgroundWidth, backgroundHeight);
      this.bR = list;
   }

   protected void dg() {
      this.bS = new KalamaHelperHelperB(this, this.bR, 10, 10, this.width - 20, this.height - 20, this.au, this.ax);
   }

   protected abstract ElementHandler be(T var1);
}
