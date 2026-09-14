package me.matl114.gui.basic;

import java.util.function.Supplier;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import org.jetbrains.annotations.Nullable;

public class KalamaHelperHelperJ<W extends Element & Drawable & Selectable> extends ContentDelegateWidget<W> {
   ValueAccessor<Integer> eP;
   Supplier<W> eO;
   ValueAccessor<Integer> eQ;

   public KalamaHelperHelperJ(@Nullable Supplier<W> supplier, int x, int y) {
      this(supplier, null, null, x, y);
   }

   @Override
   public int getX() {
      return this.eP.getValue();
   }

   public KalamaHelperHelperJ(@Nullable Supplier<W> supplier, @Nullable ValueAccessor<Integer> xSupplier, int y) {
      this(supplier, xSupplier, null, 0, y);
   }

   @Override
   public W ef() {
      return this.eO.get();
   }

   public KalamaHelperHelperJ(@Nullable Supplier<W> supplier, int x, @Nullable ValueAccessor<Integer> ySupplier) {
      this(supplier, null, ySupplier, x, 0);
   }

   public KalamaHelperHelperJ(
      @Nullable Supplier<W> supplier, @Nullable ValueAccessor<Integer> xSupplier, @Nullable ValueAccessor<Integer> ySupplier, int x, int y
   ) {
      super(x, y, 0, 0);
      this.eO = supplier != null ? supplier : () -> super.ef();
      this.eP = xSupplier != null ? xSupplier : ValueAccessor.of(() -> super.getX(), x$0 -> super.setX(x$0));
      this.eQ = ySupplier != null ? ySupplier : ValueAccessor.of(() -> super.getY(), x$0 -> super.setY(x$0));
   }

   public KalamaHelperHelperJ(@Nullable Supplier<W> supplier, @Nullable ValueAccessor<Integer> xSupplier, @Nullable ValueAccessor<Integer> ySupplier) {
      this(supplier, xSupplier, ySupplier, 0, 0);
   }

   @Override
   public void setY(int y) {
      this.eQ.setValue(y);
   }

   @Override
   public void setX(int x) {
      this.eP.setValue(x);
   }

   @Override
   public int getY() {
      return this.eQ.getValue();
   }
}
