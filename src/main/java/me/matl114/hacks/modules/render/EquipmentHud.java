package me.matl114.hacks.modules.render;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.events.Event;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.Direction2d;
import me.matl114.hacks.utils.config.WidgetPos;
import me.matl114.hacks.utils.render.ItemStackDisplayUtils;
import me.matl114.hacks.utils.render.ItemStackDisplayUtils$DamageDisplay;
import me.matl114.managers.Configs;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class EquipmentHud extends IRender2DModule {
   public NBTRef<WidgetPos> headPos;
   public NBTRef<WidgetPos> feetPos;
   public NBTRef<WidgetPos> handPos;
   public final ModulePath Cn = this.createRoot();
   public NBTRef<WidgetPos> offhandPos;
   public NBTRef<WidgetPos> chestPos;
   public EnumRef<Direction2d> damageDisplayPosition;
   public EnumRef<ItemStackDisplayUtils$DamageDisplay> Vl = this.builder(this.Cn.add("damage-display"), ItemStackDisplayUtils$DamageDisplay.class)
      .defaultValue(ItemStackDisplayUtils$DamageDisplay.NONE)
      .build();
   Map<EquipmentSlot, NBTRef<WidgetPos>> map;
   public NBTRef<WidgetPos> legPos;

   public void onUpdate(Event<Void> event) {
   }

   @Override
   public void render2D(VDrawContext vdraw, float partialTicks) {
      for (Entry var4 : this.map.entrySet()) {
         ItemStack var5 = mc.player.getEquippedStack((EquipmentSlot)var4.getKey());
         WidgetPos var6 = (WidgetPos)((NBTRef)var4.getValue()).get();
         int var7 = var6.getWindowX(mc.getWindow());
         int var8 = var6.getWindowY(mc.getWindow());
         if (!var5.isEmpty()) {
            vdraw.K(var5, var7, var8, 999, 0);
            vdraw.drawItemInSlot(mc.textRenderer, var5, var7, var8, null);
            this.drawDamageIfAbsent(vdraw, var5, var7, var8);
         } else {
            vdraw.V(KalamaHelperHelperB.l.get(var4.getKey()), var7, var8, 16, 16);
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
   }

   private void drawDamageIfAbsent(VDrawContext vdraw, ItemStack stack, int startX, int startY) {
      ItemStackDisplayUtils$DamageDisplay var5 = this.Vl.get();
      if (var5 != ItemStackDisplayUtils$DamageDisplay.NONE) {
         int var6 = stack.getMaxDamage();
         if (var6 > 0) {
            Text var7 = ItemStackDisplayUtils.getDamageShowText(stack, var5);
            if (var7 != null) {
               float var8 = mc.textRenderer.getTextHandler().getWidth(var7);
               int var9;
               int var10;
               switch ((Direction2d)(Object)this.damageDisplayPosition.get()) {
                  case UP:
                     var9 = (int)(startX + 8 - (var8 - 1.0F) / 2.0F);
                     var10 = startY - 8;
                     break;
                  case LEFT:
                     var9 = (int)(startX - var8);
                     var10 = startY + 4;
                     break;
                  case RIGHT:
                     var9 = startX + 16;
                     var10 = startY + 4;
                     break;
                  default:
                     var9 = (int)(startX + 8 - (var8 - 1.0F) / 2.0F);
                     var10 = startY + 15;
               }

               vdraw.z(mc.textRenderer, var7.asOrderedText(), var9, var10, ItemStackDisplayUtils.getDamageDisplayColor(stack), true);
            }
         }
      }
   }

   @Override
   protected ModulePath createRoot() {
      return makePath(Configs.i, "in-game-hud.equipment-hud");
   }

   public EquipmentHud() {
      super("EquipmentHud");
      this.damageDisplayPosition = this.builder(this.Cn.add("damage-display-position"), Direction2d.class).defaultValue(Direction2d.DOWN).build();
      this.handPos = this.builder(this.Cn.add("hand-pos"), WidgetPos.class).defaultValue(new WidgetPos(1, 0.0, 0.0, -60, 0)).build();
      this.offhandPos = this.builder(this.Cn.add("offhand-pos"), WidgetPos.class).defaultValue(new WidgetPos(1, 0.0, 0.0, 40, 0)).build();
      this.headPos = this.builder(this.Cn.add("head-pos"), WidgetPos.class).defaultValue(new WidgetPos(1, 0.0, 0.0, -40, 0)).build();
      this.chestPos = this.builder(this.Cn.add("chest-pos"), WidgetPos.class).defaultValue(new WidgetPos(1, 0.0, 0.0, -20, 0)).build();
      this.legPos = this.builder(this.Cn.add("leg-pos"), WidgetPos.class).defaultValue(new WidgetPos(1, 0.0, 0.0, 0, 0)).build();
      this.feetPos = this.builder(this.Cn.add("feet-pos"), WidgetPos.class).defaultValue(new WidgetPos(1, 0.0, 0.0, 20, 0)).build();
      this.map = new LinkedHashMap<>();
      this.map.put(EquipmentSlot.MAINHAND, this.handPos);
      this.map.put(EquipmentSlot.OFFHAND, this.offhandPos);
      this.map.put(EquipmentSlot.HEAD, this.headPos);
      this.map.put(EquipmentSlot.CHEST, this.chestPos);
      this.map.put(EquipmentSlot.LEGS, this.legPos);
      this.map.put(EquipmentSlot.FEET, this.feetPos);
   }



   @Override
   public void hK(Object arg0) { }

}
