package me.matl114.hacks.utils.config;

import java.util.function.Consumer;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.presets.choices.RegistryChooseScreen;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.registry.Registry;

class HackUtilHelperF<T> extends RegistryChooseScreen<T> {
   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      return this.cn.isValidate();
   }

   HackUtilHelperF(Registry var1, Consumer var2, AttrKeyValue var3, AttrKeyValue var4, AttrKeyValue var5) {
      super(var1, var2);
      this.cn = var3;
      this.co = var4;
      this.cp = var5;
      this.ar.ch(false);
      this.ar.bP(v -> originalCopy.isValidate() && ((WeakRegistryRegex)originalCopy.getOriginValue()).test(v.getC()));
      this.cn.addListener(s -> this.ar.bT());
      KalamaHelperHelperCX var6 = this.ar.aV();
      var6.L();
      var6.Q(this.co.generateValueWidget(0, -20, var6.getWidth(), 20));
      var6.Q(this.dp(var6));
   }

   public DrawableWidget dp(KalamaHelperHelperCX subScreenWidget) {
      return ExecutableWidget.instance(subScreenWidget.getWidth(), -20, 20, 20)
         .eV(IconElement.cm(KalamaHelperHelperB.g, ButtonAction.a(() -> {})).aO(TooltipHandler.ap(((WeakRegistryRegex)(Object)this.cp.getOriginValue()).getRules())));
   }
   AttrKeyValue cn;
   AttrKeyValue co;
   AttrKeyValue cp;
}
