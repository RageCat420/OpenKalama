package me.matl114.gui.a.e;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.matl114.gui.FilterService;
import me.matl114.gui.GridSubScreen;
import me.matl114.gui.PageSwitchSubScreen;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.utils.config.ValueAccessor;

public class KalamaHelperHelperA<R> extends KalamaHelperHelperCX {
   final KalamaHelperHelperCX cK;
   final int cO;
   Supplier<List<R>> cM;
   final int cS;
   final int cQ;
   final ContentDelegateWidget<KalamaHelperHelperCX> cJ;
   final int cP;
   final PageSwitchSubScreen aN;
   final Function<R, DrawableWidget> cN;
   final int cR;
   ValueAccessor<String> aV;
   BiPredicate<String, R> aQ;
   List<R> cL;
   GridSubScreen<DrawableWidget> cI;

   public void dE(int gridHeight) {
      if (!this.dF(gridHeight)) {
         this.dI();
      }
   }

   protected synchronized void bC() {
      this.aN.co(Math.max(1, 1 + (this.cL.size() - 1) / this.cI.getEntryPerPage()));
      int var1 = this.aN.cq();
      this.cI.refreshPage(this.cL, this.cN, var1);
   }

   public void dC(BiPredicate<String, R> filter) {
      BiPredicate var2 = this.aQ;
      this.aQ = filter;
      if (this.aQ != null) {
         this.cJ.setContentDelegate(this.cK);
      } else {
         this.cJ.setContentDelegate(null);
      }

      if (var2 != filter) {
         this.dG();
      }
   }

   public BiPredicate<String, R> dJ() {
      return this.aQ;
   }

   public KalamaHelperHelperA(
      int x,
      int y,
      int dx,
      int pageHeight,
      int page2Grid,
      int gridHeight,
      int grid2Filter,
      int filterHeight,
      int elementX,
      int elementY,
      Supplier<List<R>> origins,
      BiPredicate<String, R> filter,
      ValueAccessor<String> filterInput,
      Function<R, DrawableWidget> function
   ) {
      super(x, y, dx, 0);
      this.cO = pageHeight + page2Grid;
      this.cR = elementX;
      this.cS = elementY;
      this.cQ = grid2Filter;
      this.cP = filterHeight;
      this.cM = origins;
      this.cN = function;
      this.aV = filterInput;
      this.aN = new PageSwitchSubScreen(0, 0, dx, pageHeight, 100, i -> this.bC()).addToSub(this);
      this.cK = FilterService.createFilter(filterInput, v -> this.dH().accept(v), 5, 0, dx - 10, this.cP);
      this.cJ = new ContentDelegateWidget<KalamaHelperHelperCX>(0, 0, 0, 0).setContentDelegate(this.cK);
      this.cJ.addToSub(this);
      this.dC(filter);
      this.dD(pageHeight + page2Grid + gridHeight + grid2Filter + filterHeight);
   }

   public boolean dF(int gridHeight) {
      return this.dD(gridHeight + this.cO + this.cQ + this.cP);
   }

   public boolean dD(int newHeight) {
      if (newHeight != this.dy) {
         this.dy = newHeight;
         int var2 = newHeight - this.cO - this.cQ - this.cP;
         this.cJ.setY(newHeight - this.cP);
         if (this.cI != null) {
            this.R(this.cI);
         }

         this.cI = new GridSubScreen(0, this.cO, this.dx, var2, this.cR, this.cS).addToSub(this);
         CompletableFuture.supplyAsync(this::dG).thenRun(this::bC);
         return true;
      } else {
         return false;
      }
   }

   public void dI() {
      CompletableFuture.supplyAsync(this::dG).thenAccept(i -> {
         if (i) {
            this.bC();
         }
      });
   }

   public Consumer<String> dH() {
      return str -> {
         this.aV.setValue(str);
         this.dI();
      };
   }

   public synchronized boolean dG() {
      BiPredicate var1 = this.aQ;
      if (this.aQ != null) {
         this.cL = this.cM.get().stream().filter(t -> var1.test(this.aV.getValue(), t)).toList();
         return true;
      } else {
         List var2 = this.cM.get();
         if (this.cL != var2) {
            this.cL = var2;
            return true;
         } else {
            return false;
         }
      }
   }
}
