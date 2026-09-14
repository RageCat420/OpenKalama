package me.matl114.hacks.modules.inv;

import com.google.common.util.concurrent.Runnables;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.TaskManagers;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.ListRef;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;

public class QuickButton extends BaseModule {
   private static final int buttonHeight = 12;
   public final ModulePath pp = makePath(Configs.l, "quick-buttons");
   public final ListRef buttonToggles;
   public final FlagRef ae = this.flagBuilder(this.pp.add("enable-buttons")).build();
   public final ListRef pq = this.builder(this.pp.add("button-tasks"), ListRef.TYPE)
      .defaultValue(List.of("clear-keep", "take-all", "save-all", "slime-guide"))
      .build();

   public void xI(Event<HandledScreen<?>> event) {
      if (this.ae.get()) {
         this.initButton((HandledScreen<?>)event.b);
      }
   }

   public void initButton(HandledScreen<?> handledScreen) {
      HandledScreenAccess var2 = HandledScreenAccess.of(handledScreen);
      int var4;
      int var5;
      if (handledScreen instanceof CreativeInventoryScreen var3) {
         var4 = var2.getScreenX();
         var5 = resizeCreativeYv(var2.getScreenY());
      } else {
         var4 = var2.getScreenX();
         var5 = var2.getScreenY();
      }

      LinkedHashMap var20 = new LinkedHashMap();

      for (String var7 : this.pq.get()) {
         Runnable var8 = TaskManagers.b().a("button-task." + var7);
         var20.put(var7, var8 == null ? Runnables.doNothing() : var8);
      }

      LinkedHashMap var21 = new LinkedHashMap();

      for (String var26 : this.buttonToggles.get()) {
         FlagRef var9 = TaskManagers.c().n("button-toggle." + var26);
         var21.put(var26, Optional.ofNullable(var9));
      }

      int var23 = 0;
      int var27 = var2.getScreenBackgroundX() / 4 - 1;
      int var28 = var20.size();
      var23 += (var28 - 1) / 4 + 1;
      int var10 = var21.size();
      var23 += (var10 - 1) / 4 + 1;
      int var11 = -14 * var23 - 6;
      int var12 = 0;

      for (Entry var14 : ((java.util.Set<Entry>)(var21).entrySet())) {
         String var15 = (String)var14.getKey();
         FlagRef var16 = (FlagRef)((Optional)var14.getValue()).orElse(null);
         String var17 = "button-toggle." + var15;
         Runnable var18 = var16 != null ? HotKeyUtils.wrapFlagAsToggle(var17, var16) : Runnables.doNothing();
         ExecutableWidget var19 = ExecutableWidget.instance(var4 + var12 * (var27 + 1), var5 + var11, var27, 12)
            .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(Text.literal(var15)), (element, widget1, mouseButton) -> {
               var18.run();
               if (var16 != null) {
                  widget1.setAlpha(var16.get() ? 1.0F : 0.4F);
               }

               return true;
            }))
            .addTo(handledScreen);
         var19.setAlpha(var16 != null && var16.get() ? 1.0F : 0.4F);
         if (++var12 == 4) {
            var12 = 0;
            var11 += 14;
         }
      }

      var12 = 0;
      byte var29 = 14;

      for (Entry var32 : ((java.util.Set<Entry>)(var20).entrySet())) {
         Runnable var33 = (Runnable)var32.getValue();
         ExecutableWidget.instance(var4 + var12 * (var27 + 1), var5 - var29, var27, 12)
            .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(Text.literal((String)var32.getKey())), ButtonAction.a(var33)))
            .addTo(handledScreen);
         if (++var12 == 4) {
            var12 = 0;
            var29 += 14;
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ai().c(HandledScreen.class), this::xI);
   }

   public QuickButton() {
      super("QuickButton");
      this.buttonToggles = this.builder(this.pp.add("button-toggles"), ListRef.TYPE)
         .defaultValue(List.of("keep-inv", "fast-inv", "auto-store", "left-one"))
         .build();
      this.bindFlag(this.ae);
   }

   public static int resizeCreativeYv(int y) {
      return y - 30;
   }
}
