package me.matl114.hacks.modules.render;

import java.util.List;
import java.util.Map.Entry;
import me.matl114.events.Event;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.WidgetPos;
import me.matl114.hacks.utils.render.ItemStackDisplayUtils;
import me.matl114.hacks.utils.render.ItemStackDisplayUtils$DamageDisplay;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

public class NameList extends INameTag {
   public FlagRef listRight;
   public IntRef playerListMaxLength;
   public NBTRef<WidgetPos> listPos;

   public void handleNameLineList(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.b != null) {
         float var3 = player.c;
         if (this.listRight.get()) {
            vdraw.f().translate(-var3, 0.0F);
         }

         vdraw.z(mc.textRenderer, player.b.asOrderedText(), 0, 0, -1, true);
         if (!this.listRight.get()) {
            vdraw.f().translate(var3, 0.0F);
         }
      }
   }

   public void handleEquipmentList(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.equipments != null) {
         ItemStack[] var3 = player.equipments;
         if (this.listRight.get()) {
            vdraw.f().translate(-(var3.length * 9), 0.0F);
         }

         vdraw.f().pushMatrix();
         vdraw.f().scale(0.5625F, 0.5625F);

         for (int var4 = 0; var4 < var3.length; var4++) {
            if (var3[var4].isEmpty()) {
               EquipmentSlot var5 = SLOTS[var4];
               vdraw.V(KalamaHelperHelperB.l.get(var5), var4 * 16, 0, 16, 16);
            } else {
               vdraw.K(var3[var4], var4 * 16, 0, 999, 0);
               String var7 = null;
               ItemStack var6 = var3[var4];
               if (this.equipmentDamage.get().isNotIn(new ConfigEnum[]{ItemStackDisplayUtils$DamageDisplay.NONE})
                  && var6.getCount() == 1
                  && var6.isDamageable()) {
                  var7 = ItemStackDisplayUtils.getDamageShowText(var6, this.equipmentDamage.get()).getString();
                  var6 = var6.copy();
                  var6.setDamage(0);
               }

               vdraw.drawItemInSlot(mc.textRenderer, var6, var4 * 16, 0, var7);
            }
         }

         vdraw.f().popMatrix();
         if (!this.listRight.get()) {
            vdraw.f().translate(var3.length * 9, 0.0F);
         }
      }
   }

   public void handleTooManyPlayerList(VDrawContext vdraw) {
      vdraw.b();
      OrderedText var2 = Text.literal("......(" + (this.GU.size() - this.playerListMaxLength.get()) + " more)").asOrderedText();
      float var3 = mc.textRenderer.getTextHandler().getWidth(var2);
      if (this.listRight.get()) {
         vdraw.f().translate(-var3, 0.0F);
      }

      vdraw.z(mc.textRenderer, var2, 0, 0, -1, true);
      vdraw.c();
   }

   @Override
   public void B(Event<VDrawContext> event) {
      if (!checkNull()) {
         if (this.ae.get() && this.GU != null) {
            VDrawContext var2 = (VDrawContext)event.b;
            var2.f().pushMatrix();
            this.handleRenderPosition(var2);
            int var3 = 0;

            for (RenderSubHelperIX var5 : this.GU) {
               if (var3 >= this.playerListMaxLength.get()) {
                  this.handleTooManyPlayerList(var2);
                  break;
               }

               this.onRenderList(var5, var2, event.<Float>getArgs(0));
               var2.f().translate(0.0F, 9.0F);
               var3++;
            }

            var2.f().popMatrix();
         }
      }
   }

   public void handleEffectDisplayList(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.g != null) {
         List var3 = player.g.entrySet().stream().toList();
         int var4 = var3.size();

         for (int var5 = 0; var5 < var4; var5++) {
            Entry var6 = (Entry)var3.get(var5);
            float var7 = mc.textRenderer.getTextHandler().getWidth((StringVisitable)var6.getValue());
            if (this.listRight.get()) {
               vdraw.f().translate(-9.0F - var7, 0.0F);
            }

            vdraw.f().pushMatrix();
            vdraw.f().scale(0.5625F, 0.5625F);
            rH.a(0, 0, vdraw, (StatusEffect)((RegistryEntry)var6.getKey()).value());
            vdraw.f().popMatrix();
            vdraw.z(mc.textRenderer, ((Text)var6.getValue()).asOrderedText(), 9, 0, this.potionColor.get().withAlpha(255), true);
            if (!this.listRight.get()) {
               vdraw.f().translate(9.0F + var7, 0.0F);
            }
         }
      }
   }

   protected void initializeModuleSettings() {
      this.playerListMaxLength = this.builder(this.GE.add("player-list-max-length"), IntRef.TYPE).defaultValue(20).build();
      this.listRight = this.flagBuilder(this.GE.add("list-right")).build();
      this.listPos = this.builder(this.GE.add("list-pos"), WidgetPos.class).defaultValue(new WidgetPos(0, 0.02, 0.02, 5, 5)).build();
   }

   public void gq(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.e != null) {
         float var3 = player.f;
         if (this.listRight.get()) {
            vdraw.f().translate(-var3, 0.0F);
         }

         vdraw.z(mc.textRenderer, player.e.asOrderedText(), 0, 0, -1, true);
         if (!this.listRight.get()) {
            vdraw.f().translate(var3, 0.0F);
         }
      }
   }

   public void handleRenderPosition(VDrawContext vdraw) {
      WidgetPos var2 = this.listPos.get();
      double var3 = var2.getWindowX(mc.getWindow());
      double var5 = var2.getWindowY(mc.getWindow());
      vdraw.f().translate((float)var3, (float)var5);
   }

   public NameList() {
      super("NameList");
   }

   @Override
   protected ModulePath createRoot() {
      return makePath(Configs.i, "player-info.name-list");
   }

   public void onRenderList(RenderSubHelperIX player, VDrawContext vdraw, float tick) {
      vdraw.b();

      try {
         this.handleNameLineList(vdraw, player);
         this.handleEquipmentList(vdraw, player);
         this.gq(vdraw, player);
         this.handleEffectDisplayList(vdraw, player);
      } finally {
         vdraw.c();
      }
   }



   @Override
   public void gl() { }

}
