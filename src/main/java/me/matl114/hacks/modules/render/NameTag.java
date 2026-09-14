package me.matl114.hacks.modules.render;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.render.ItemStackDisplayUtils;
import me.matl114.hacks.utils.render.ItemStackDisplayUtils$DamageDisplay;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.RenderUtils;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2d;

public class NameTag extends INameTag {
   public DoubleRef playerSize;
   public DoubleRef playerExtraHeight;
   public FlagRef hideVanilla;

   public void handleNameLinePlayer(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.e != null) {
         float var3 = player.f;
         float var4 = var3 / 2.0F;
         vdraw.f().translate(0.0F, -6.75F);
         vdraw.f().pushMatrix();
         vdraw.f().scale(0.75F, 0.75F);
         vdraw.z(mc.textRenderer, player.e.asOrderedText(), (int)(-var4), 0, -1, true);
         vdraw.c();
      }
   }

   public void Xt(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.b != null) {
         float var3 = player.c;
         float var4 = var3 / 2.0F;
         vdraw.f().translate(0.0F, -9.0F);
         vdraw.z(mc.textRenderer, player.b.asOrderedText(), (int)(-var4), 0, -1, true);
      }
   }

   public void handleEffectDisplayPlayer(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.g != null) {
         List var3 = player.g.entrySet().stream().toList();
         int var4 = var3.size();

         for (byte var5 = 0; var5 < var4; var5 += 3) {
            int var6 = Math.min(var5 + 3, var4);
            if (var6 != var5) {
               float var7 = var6 - var5 - 1;

               for (int var8 = var5; var8 < var6; var8++) {
                  var7 += 9.0F;
                  var7 += mc.textRenderer.getTextHandler().getWidth((StringVisitable)((Entry)var3.get(var8)).getValue());
               }

               vdraw.f().translate(0.0F, -9.0F);
               vdraw.f().pushMatrix();
               vdraw.f().translate(-var7 / 2.0F, 0.0F);

               for (int var11 = var5; var11 < var6; var11++) {
                  Entry var9 = (Entry)var3.get(var11);
                  vdraw.f().pushMatrix();
                  vdraw.f().scale(0.5F, 0.5F);
                  rH.a(1, 1, vdraw, (StatusEffect)((RegistryEntry)var9.getKey()).value());
                  vdraw.f().popMatrix();
                  vdraw.f().translate(9.0F, 0.0F);
                  vdraw.z(mc.textRenderer, ((Text)var9.getValue()).asOrderedText(), 0, 0, this.potionColor.get().withAlpha(255), true);
                  vdraw.f().translate(mc.textRenderer.getTextHandler().getWidth((StringVisitable)((Entry)var3.get(var11)).getValue()) + 1.0F, 0.0F);
               }

               vdraw.f().popMatrix();
            }
         }
      }
   }

   public NameTag() {
      super("NameTag");
   }

   public void onRenderPlayer(RenderSubHelperIX player, VDrawContext vdraw, Function<Vec3d, Vector2d> projector, float tick) {
      Vec3d var5 = player.a.getLerpedPos(tick).add(0.0, player.a.getHeight() + 0.5, 0.0);
      Vector2d var6 = (Vector2d)projector.apply(var5);
      if (var6 != null) {
         vdraw.b();

         try {
            vdraw.f().translate((float)var6.x, (float)var6.y);
            this.Xs(vdraw);
            this.Xt(vdraw, player);
            this.handleEquipmentPlayer(vdraw, player);
            this.handleNameLinePlayer(vdraw, player);
            this.handleEffectDisplayPlayer(vdraw, player);
         } finally {
            vdraw.c();
         }
      }
   }

   public void Xs(VDrawContext vdraw) {
      vdraw.f().translate(0.0F, -((float)(Object)this.playerExtraHeight.get()));
      vdraw.f().scale((float)(Object)this.playerSize.get(), (float)(Object)this.playerSize.get());
   }

   public void onRender(Event<VDrawContext> event) {
      if (!checkNull()) {
         if (this.ae.get() && this.GU != null) {
            VDrawContext var2 = (VDrawContext)event.b;
            Matrix4f var3 = RenderListener.B();
            Matrix4f var4 = RenderListener.D();
            Function var5 = RenderUtils.D(var3, var4);

            for (RenderSubHelperIX var7 : this.GU) {
               if (var7.a != mc.getCameraEntity()) {
                  this.onRenderPlayer(var7, var2, var5, event.<Float>getArgs(0));
               }
            }
         }
      }
   }

   @Override
   protected ModulePath createRoot() {
      return makePath(Configs.i, "player-info.name-tag");
   }

   public void handleEquipmentPlayer(VDrawContext vdraw, RenderSubHelperIX player) {
      if (player.equipments != null) {
         ItemStack[] var3 = player.equipments;
         int var4 = -var3.length * 9;
         boolean var5 = false;
         vdraw.f().pushMatrix();
         vdraw.f().scale(0.75F, 0.75F);

         for (int var6 = 0; var6 < var3.length; var6++) {
            if (var3[var6].isEmpty()) {
               EquipmentSlot var7 = SLOTS[var6];
               vdraw.V(KalamaHelperHelperB.l.get(var7), var4 + var6 * 18, -17, 16, 16);
            } else {
               vdraw.K(var3[var6], var4 + var6 * 18, -17, 999, 0);
               ItemStack var11 = var3[var6];
               vdraw.drawItemInSlot(mc.textRenderer, var11, var4 + var6 * 18, -17, null);
               if (this.equipmentDamage.get().isNotIn(new ConfigEnum[]{ItemStackDisplayUtils$DamageDisplay.NONE})
                  && var11.getCount() == 1
                  && var11.isDamageable()) {
                  var5 = true;
                  OrderedText var8 = ItemStackDisplayUtils.getDamageShowText(var11, this.equipmentDamage.get()).asOrderedText();
                  float var9 = mc.textRenderer.getTextHandler().getWidth(var8);
                  int var10 = ItemStackDisplayUtils.getDamageDisplayColor(var11);
                  vdraw.z(mc.textRenderer, var8, var4 + var6 * 18 + 9 + (int)((-var9 - 1.0F) / 2.0F), -25, var10, true);
               }
            }
         }

         vdraw.f().popMatrix();
         vdraw.f().translate(0.0F, var5 ? -18.0F : -13.5F);
      }
   }

   protected void initializeModuleSettings() {
      this.playerExtraHeight = this.doubleBuilder(this.GE.add("player-extra-height")).defaultValue(1.0).build();
      this.playerSize = this.doubleBuilder(this.GE.add("player-size")).defaultValue(1.0).build();
      this.hideVanilla = this.flagBuilder(this.GE.add("hide-vanilla")).build();
   }

   @Override
   public void B(Object arg0) { }

}
