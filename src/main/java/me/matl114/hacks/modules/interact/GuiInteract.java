package me.matl114.hacks.modules.interact;

import com.google.common.base.Suppliers;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import me.matl114.accessors.hacks.KeyBindAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;

public class GuiInteract extends BaseModule {
   public final FlagRef ae;
   public final KeyBindRef J;
   public final Set<KeyBinding> wK;
   public final ModulePath ad = makePath(Configs.n, "interact-fix.gui-interact");
   public final Supplier<KeyBinding[]> wL;
   public final FlagRef useTickWhenScreenOpen;

   public void onResetKeyBind(Event<Screen> eventPost) {
      if (checkNull()) {
         this.wK.clear();
      } else {
         if (this.ae.get()) {
            if (eventPost.b != null) {
               for (KeyBinding var5 : this.wL.get()) {
                  if (this.wK.contains(var5)) {
                     var5.setPressed(true);
                  }
               }
            } else {
               for (KeyBinding var9 : this.wL.get()) {
                  KeyBindAccess.of(var9).resetKeyState();
               }
            }
         }

         this.wK.clear();
      }
   }

   public void onInput(Event<Void> event) {
      if (!checkNull()) {
         if (this.ae.get() && mc.currentScreen != null) {
            if (this.useTickWhenScreenOpen.get() && event.d()) {
               event.j(false);
            }

            for (KeyBinding var5 : this.wL.get()) {
               KeyBindAccess.of(var5).resetKeyState();
            }
         }
      }
   }

   public void onStoreKeyBindState(Event<Screen> eventPre) {
      this.wK.clear();
      if (!checkNull()) {
         if (this.ae.get() && !eventPre.d()) {
            for (KeyBinding var5 : this.wL.get()) {
               if (var5.isPressed()) {
                  this.wK.add(var5);
               }
            }
         }
      }
   }

   public GuiInteract() {
      super("GuiInteract");
      this.wK = new HashSet<>();
      this.wL = Suppliers.memoize(() -> new KeyBinding[]{mc.options.useKey, mc.options.attackKey, mc.options.sprintKey});
      this.ae = this.builder(this.ad.addEnable(), Boolean.class).defaultValue(false).build();
      this.J = this.toggleHotkey(this.ad.addHotkey(), new MultiKeyBind(), this.ad.addEnable()).build();
      this.useTickWhenScreenOpen = this.builder(this.ad.add("use-tick-when-screen-open"), Boolean.class).defaultValue(true).build();
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ae(), this::onStoreKeyBindState);
      this.registerListener(Listener.ag(), this::onResetKeyBind);
      this.registerListener(Listener.bd(), this::onInput, Integer.MIN_VALUE);
   }
}
