package me.matl114.hacks.modules.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.RegistryRegex;
import me.matl114.hacks.utils.config.Vec2;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class InvHud extends BaseModule {
   public NBTRef<Vec2> pos;
   public FlagRef showShulkerItems;
   public FlagRef right;
   public final ModulePath Cn = makePath(Configs.i, "in-game-hud.inv-hud");
   public FlagRef down;
   public NBTRef<RegistryRegex<Item>> showItems;
   public KeyBindRef hotkey;
   public final FlagRef ae = this.flagBuilder(this.Cn.addEnable()).build();
   List<ItemStack> Cq;
   public IntRef countPerLine;

   public void sg(Event<VDrawContext> event) {
      if (!checkNull()) {
         if (this.ae.get() && !event.<Boolean>getArgs(1) && this.Cq != null) {
            VDrawContext var2 = (VDrawContext)event.b;
            var2.b();

            try {
               this.handleRenderPosition(var2);
               int var3 = 0;
               int var4 = this.countPerLine.get();
               int var5 = (this.Cq.size() - 1) / var4 + 1;
               int var6 = 0;

               for (ItemStack var8 : this.Cq) {
                  this.drawItem(var2, var5, var6, var3, var8);
                  if (++var6 >= var4) {
                     var6 = 0;
                     var3++;
                  }
               }
            } finally {
               var2.c();
            }
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.V(), this::onPostTick);
      this.registerListener(RenderListener.r(), this::sg);
   }

   public void onPostTick(Event<ClientPlayerEntity> event) {
      if (this.ae.get()) {
         this.Cq = new ArrayList<>();
         Map var2;
         if ((var2 = this.showShulkerItems.get() ? PlayerStateManager.INSTANCE.jN : PlayerStateManager.INSTANCE.jM) != null) {
            for (Entry var4 : ((java.util.Set<Entry>)(var2).entrySet())) {
               if (this.showItems.get().test(((ItemStackSample)var4.getKey()).fS().getItem())) {
                  ItemStack var5 = ((ItemStackSample)var4.getKey()).fS().copyWithCount((Integer)var4.getValue());
                  this.Cq.add(var5);
               }
            }
         }

         this.Cq.sort(Comparator.comparingInt(ItemStack::getCount).reversed());
      } else {
         this.Cq = null;
      }
   }

   public InvHud() {
      super("InvHud");
      this.hotkey = this.moduleEntry(this.Cn.add("hotkey"), new MultiKeyBind(), this.Cn.add("enable")).build();
      this.right = this.flagBuilder(this.Cn.add("right")).build();
      this.down = this.flagBuilder(this.Cn.add("down")).build();
      this.pos = this.builder(this.Cn.add("pos"), Vec2.class)
         .defaultValue(new Vec2(0.02, 0.3))
         .validator(v -> v.x() >= 0.0 && v.y() >= 0.0 && v.x() <= 1.0 && v.y() <= 1.0)
         .build();
      this.showItems = this.builder(this.Cn.add("show-items"), NBTType.parameter(RegistryRegex.class))
         .defaultValue(new RegistryRegex(new Regex("^(.*)$"), Registries.ITEM))
         .build();
      this.countPerLine = this.intBuilder(this.Cn.add("count-per-line")).defaultValue(9).build();
      this.showShulkerItems = this.builder(this.Cn.add("show-shulker-items"), Boolean.class).defaultValue(true).build();
      this.bindFlag(this.ae);
   }

   private void drawItem(VDrawContext vdraw, int totalLine, int x, int y, ItemStack stack) {
      int var6 = this.right.get() ? -18 * x - 18 : 18 * x;
      int var7 = this.down.get() ? -18 * totalLine + 18 * y : 18 * y;
      vdraw.K(stack, var6 + 1, var7 + 1, 999, 0);
      vdraw.drawItemInSlot(mc.textRenderer, stack, var6 + 1, var7 + 1, null);
   }

   public void handleRenderPosition(VDrawContext vdraw) {
      int var2 = mc.getWindow().getScaledWidth();
      int var3 = mc.getWindow().getScaledHeight();
      Vec2 var4 = this.pos.get();
      double var5 = var4.x();
      double var7 = var4.y();
      int var9 = (int)(this.right.get() ? var2 - var5 * var2 : var5 * var2);
      int var10 = (int)(this.down.get() ? var3 - var7 * var3 : var7 * var3);
      vdraw.f().translate(var9, var10);
   }
}
