package me.matl114.gui;

import java.util.Optional;
import me.matl114.events.impl.BlockUpdate;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.config.KalamaHelperHelperA;
import me.matl114.gui.elements.ColorLabelTextElement;
import me.matl114.utils.config.AttrKeyValue;

class KalamaHelperHelperC<T> extends KalamaHelperHelperA<T> {
   KalamaHelperHelperC(
      int var1, int var2, int var3, int var4, int var5, int var6, int var7, Optional var8, AttrKeyValue var9, KalamaHelperHelperQ var10, BlockUpdate var11
   ) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      this.F = var10;
      this.G = var11;
   }

   @Override
   public DrawableWidget ab() {
      return ExecutableWidget.instance(0, this.F.blankWidth(), this.F.blankWidth(), this.F.indexWidth())
         .eV(
            new ColorLabelTextElement(TextProvider.c(this.eK()), () -> palette.aav().getColorInt(), () -> palette.aaw().getColorInt())
               .aO(TooltipHandler.ar(this::eL))
         );
   }
   KalamaHelperHelperQ F;
   BlockUpdate G;
}
