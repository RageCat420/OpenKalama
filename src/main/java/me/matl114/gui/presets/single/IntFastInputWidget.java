package me.matl114.gui.presets.single;

import java.util.function.Consumer;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.PlateElement;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.kv.AttrKeyValues;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IntFastInputWidget extends KalamaHelperHelperCX {
   final int aB;
   private static final Text aG = Text.literal("+64");
   private static final Text aE = Text.literal("+1");
   private static final Text aK = Text.translatable("widget.gui.int-fast-input-widget.confirm");
   private static final Text aI = Text.literal("-16");
   ContentDelegateWidget<TextFieldWidget> aA;
   private static final Text aH = Text.literal("-1");
   final AttrKeyValue<Integer> az;
   private static final Text aF = Text.literal("+16");
   private static final Text aJ = Text.literal("-64");
   protected static Identifier ai = new Identifier("minecraft", "container/beacon/cancel");
   int aC;
   Runnable aD;
   final Consumer<AttrKeyValue<Integer>> c;

   protected void af() {
      this.setPriority(1);
      this.bo();
      this.initBackgroundAndText();
      this.bm();
      this.bp();
   }

   protected void initBackgroundAndText() {
      DisplayWidget.instance(0, 0, this.aB, this.dy).<DrawableWidget>setRenderHandler(PlateElement.cg()).addToSub(this);
      DisplayWidget.instance(4, 1, this.aB - 8, 10)
         .<DrawableWidget>setRenderHandler(RawTextElement.g(Text.literal(this.az.getKeyName())).setAlignment(-1))
         .addToSub(this);
      this.aA = this.az.generateValueWidget(4, 12, this.aC - 8, this.dy - 16).addToSub(this);
   }

   public static IntFastInputWidget instance(AttrKeyValue<Integer> keyValue, Consumer<AttrKeyValue<Integer>> callback, int x, int y, int dx, int dy, int dx0) {
      return (IntFastInputWidget)(keyValue instanceof AttrKeyValues.ClampedIntAttrKeyValue var7 && var7.getMin() <= 1
         ? new KalamaHelperHelperH(keyValue, callback, var7.getMax(), x, y, dx, dy, dx0)
         : new IntFastInputWidget(keyValue, callback, x, y, dx, dy, dx0));
   }

   public IntFastInputWidget(AttrKeyValue<Integer> keyValue, Consumer<AttrKeyValue<Integer>> callback, int x, int y, int dx, int dy, int dx0) {
      super(x, y, dx, dy);
      this.az = keyValue;
      this.c = callback;
      this.aB = dx0;
      this.aC = this.aB;
      this.af();
   }

   protected void addValue(int val) {
      Integer var2 = this.az.getOriginValue();
      int var3 = var2 == null ? val : var2 + val;
      if (this.az instanceof AttrKeyValues.ClampedIntAttrKeyValue var5) {
         var3 = var5.clampInput(var3);
      }

      if (this.aA.ef() != null) {
         ((TextFieldWidget)(Object)this.aA.ef()).setText(String.valueOf(var3));
      }
   }

   public IntFastInputWidget setFinishRunning(Runnable finishRunning) {
      this.aD = finishRunning;
      return this;
   }

   protected void bm() {
      int var1 = this.dx / 3;
      ExecutableWidget.instance(-6, -14, var1 + 4, 12)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(aE), ButtonAction.a(() -> this.addValue(1))))
         .addToSub(this);
      ExecutableWidget.instance(var1 - 2, -14, var1 + 4, 12)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(aF), ButtonAction.a(() -> this.addValue(16))))
         .addToSub(this);
      ExecutableWidget.instance(this.dx - var1 + 2, -14, var1 + 4, 12)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(aG), ButtonAction.a(() -> this.addValue(64))))
         .addToSub(this);
      ExecutableWidget.instance(-6, this.dy + 2, var1 + 4, 12)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(aH), ButtonAction.a(() -> this.addValue(-1))))
         .addToSub(this);
      ExecutableWidget.instance(var1 - 2, this.dy + 2, var1 + 4, 12)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(aI), ButtonAction.a(() -> this.addValue(-16))))
         .addToSub(this);
      ExecutableWidget.instance(this.dx - var1 + 2, this.dy + 2, var1 + 4, 12)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(aJ), ButtonAction.a(() -> this.addValue(-64))))
         .addToSub(this);
   }

   protected void bp() {
      ExecutableWidget.instance(this.dx + 6, -30, 24, 24)
         .<ExecutableWidget>eV(
            IconElement.cm(ai, ButtonAction.a(this::br))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.int-fast-input-widget.cancel-gui.tooltips", "")))
               .ag(i -> this.aD != null)
         )
         .addToSub(this);
      ExecutableWidget.instance(-30, -30, this.dx + 60, this.dy + 60).<ExecutableWidget>eV(PlateElement.ch().cF(KalamaHelperHelperP.ax((el, am) -> {
         this.addValue(am > 0.0 ? -1 : 1);
         return true;
      }))).addToSub(this, -1);
   }

   protected void bo() {
      DisplayWidget.instance(this.aB - 2, 3, this.dx - this.aB + 2, this.dy - 6).<DrawableWidget>setRenderHandler(PlateElement.cg()).addToSub(this);
      ExecutableWidget.instance(this.aB + 2, 7, this.dx - this.aB - 6, this.dy - 14)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(aK), ButtonAction.a(this::bq))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.int-fast-input-widget.confirm.tooltips", "")))
               .cF(KalamaHelperHelperP.aw((el, keycode) -> {
                  if (keycode == 257) {
                     this.bq();
                     return true;
                  } else {
                     return false;
                  }
               }))
         )
         .addToSub(this);
   }

   protected void bq() {
      if (this.c != null) {
         this.c.accept(this.az);
      }

      this.br();
   }

   protected void br() {
      if (this.aD != null) {
         this.aD.run();
      }
   }
}
